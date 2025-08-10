package com.borrowingtransactions.service;

import com.borrowingtransactions.DTO.MemberDTO;
import com.borrowingtransactions.DTO.NotificationDTO;
import com.borrowingtransactions.clientservices.MemberService;
import com.borrowingtransactions.model.BorrowingTransaction;
import com.borrowingtransactions.model.BorrowingTransaction.Status;
import com.borrowingtransactions.repo.BorrowingTransactionRepo;
import com.borrowingtransactions.security.CurrentUser;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServicesImpl implements NotificationServices {

	private final BorrowingTransactionRepo transactionRepo;
	private final MemberService memberClient;
	private final CurrentUser currentUser;

	@Autowired
	private JavaMailSender mailSender;

	public NotificationServicesImpl(BorrowingTransactionRepo transactionRepo, MemberService memberClient,
			CurrentUser currentUser) {
		this.transactionRepo = transactionRepo;
		this.memberClient = memberClient;
		this.currentUser = currentUser;
	}

	private String extractAuthToken() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
			HttpServletRequest request = servletRequestAttributes.getRequest();
			String token = request.getHeader("Authorization");
			if (token != null && !token.isBlank()) {
				return token.startsWith("Bearer ") ? token : "Bearer " + token;
			}
		}
		return null; 
	}

	private MemberDTO getCurrentMember(Long memberId) {
		MemberDTO cached = currentUser.getCurrentUser();
		if (cached != null && Long.valueOf(cached.getMemberId()).equals(memberId)) {
			return cached;
		}

		String token = extractAuthToken();
		if (token == null) {
			throw new IllegalStateException("Authorization token is missing in the request.");
		}

		MemberDTO member = memberClient.getMemberById(memberId, token);
		if (member != null) {
			currentUser.setCurrentUser(member);
		}
		return member;
	}

	@Override
	public List<NotificationDTO> getAllNotifications() {
		List<BorrowingTransaction> transactions = transactionRepo
				.findByStatusIn(List.of(Status.BORROWED, Status.RETURN_PENDING, Status.RETURN_REJECTED));
		return transactions.stream().map(this::toNotificationDTO).collect(Collectors.toList());
	}

	@Override
	public List<NotificationDTO> getNotificationsByMemberId(Long memberId) {
		List<BorrowingTransaction> memberTransactions = transactionRepo.findByMemberIdAndStatusIn(memberId,
				List.of(Status.BORROWED, Status.RETURN_PENDING, Status.RETURN_REJECTED));
		return memberTransactions.stream().map(this::toNotificationDTO).collect(Collectors.toList());
	}

	@Override
	public List<NotificationDTO> generateNotifications() {
		List<BorrowingTransaction> overdueTransactions = transactionRepo.findOverdueTransactions(
				List.of(Status.BORROWED, Status.RETURN_PENDING, Status.RETURN_REJECTED), LocalDate.now());

		return overdueTransactions.stream().map(tx -> {
			long overdueDays = calculateOverdueDays(tx);
			String message = createMessage(tx, overdueDays);
			sendEmailNotification(tx, message);
			return new NotificationDTO(tx.getTransactionId(), tx.getMemberId(), tx.getMemberName(), tx.getBookId(),
					tx.getBookName(), message, LocalDate.now(), (int) overdueDays);
		}).collect(Collectors.toList());
	}

	private long calculateOverdueDays(BorrowingTransaction tx) {
		if (tx.getReturnDate() != null && tx.getReturnDate().isBefore(LocalDate.now())) {
			return LocalDate.now().until(tx.getReturnDate(), java.time.temporal.ChronoUnit.DAYS);
		}
		return 0;
	}

	private String createMessage(BorrowingTransaction tx, long overdueDays) {
		LocalDate returnDate = tx.getReturnDate();
		LocalDate currentDate = LocalDate.now();

		if (returnDate != null) {
			long daysLeft = currentDate.until(returnDate, java.time.temporal.ChronoUnit.DAYS);
			if (daysLeft > 0) {
				if (daysLeft == 1) {
					return "Reminder: The book '" + tx.getBookName() + "' is due tomorrow (" + returnDate
							+ "). Please return it on time to avoid fines.";
				} else {
					return "Reminder: The book '" + tx.getBookName() + "' is due in " + daysLeft + " days ("
							+ returnDate + ").";
				}
			} else if (daysLeft == 0) {
				return "Reminder: The book '" + tx.getBookName() + "' is due today. Please return it to avoid fines.";
			} else {
			
				return "Alert: The book '" + tx.getBookName() + "' is overdue by " + overdueDays
						+ " day(s). Please return immediately to avoid further fines.";
			}
		} else {
			return "No return date set for the book '" + tx.getBookName() + "'. Please contact the library.";
		}
	}

	private void sendEmailNotification(BorrowingTransaction tx, String message) {
		try {
			MemberDTO member = getCurrentMember(tx.getMemberId());
			if (member != null && member.getEmail() != null) {
				SimpleMailMessage email = new SimpleMailMessage();
				email.setTo(member.getEmail());
				email.setSubject("Library Notification: Book Due Date Reminder");
				email.setText(message);
				mailSender.send(email);
			}
		} catch (Exception e) {
		
			System.err.println(
					"Failed to send email notification to member ID " + tx.getMemberId() + ": " + e.getMessage());
		}
	}

	private NotificationDTO toNotificationDTO(BorrowingTransaction tx) {
		long overdueDays = calculateOverdueDays(tx);
		String message = createMessage(tx, overdueDays);

		return new NotificationDTO(tx.getTransactionId(), tx.getMemberId(), tx.getMemberName(), tx.getBookId(),
				tx.getBookName(), message, LocalDate.now(), (int) overdueDays);
	}

	@Scheduled(cron = "0 0 9 * * ?") 
	public void sendDailyNotifications() {
		List<NotificationDTO> notifications = generateNotifications();
		notifications.forEach(notification -> {
			System.out.println("Sent Notification: " + notification.getMessage()); 
		});
	}
}
