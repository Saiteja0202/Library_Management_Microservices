package com.borrowingtransactions.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import com.borrowingtransactions.DTO.FineDTO;
import com.borrowingtransactions.DTO.MemberDTO;
import com.borrowingtransactions.clientservices.MemberService;
import com.borrowingtransactions.exception.MemberNotFoundException;
import com.borrowingtransactions.model.BorrowingTransaction;
import com.borrowingtransactions.model.BorrowingTransaction.FineStatus;
import com.borrowingtransactions.model.BorrowingTransaction.Status;
import com.borrowingtransactions.repo.BorrowingTransactionRepo;
import com.borrowingtransactions.security.CurrentUser;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class FinesServiceImpl implements FinesService {

    private final BorrowingTransactionRepo transactionRepo;
    private final CurrentUser currentUser;
    
    private final MemberService memberClient;

    public FinesServiceImpl(BorrowingTransactionRepo transactionRepo, CurrentUser currentUser,MemberService memberClient) {
        this.transactionRepo = transactionRepo;
        this.currentUser = currentUser;
        this.memberClient=memberClient;
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
        return null; // Caller should handle null case
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

    @Scheduled(cron = "0 54 15 * * ?") 
    public void processDailyFines() {
        List<BorrowingTransaction> overdueTransactions =
                transactionRepo.findOverdueTransactions(
                        List.of(Status.BORROWED, Status.RETURN_PENDING, Status.RETURN_REJECTED),
                        LocalDate.now()
                );

        System.out.println("_*_**__*_*_*_*_*_*_*_ Overdue Transactions:");
        overdueTransactions.forEach(tx -> {
            System.out.println("_%_%_%__%_%_%_%_ Transaction ID: " + tx.getTransactionId()
                    + ", Member ID: " + tx.getMemberId()
                    + ", Book ID: " + tx.getBookId()
                    + ", Return Date: " + tx.getReturnDate()
                    + ", Status: " + tx.getStatus());
        });
        for (BorrowingTransaction tx : overdueTransactions) {
            if (tx.getReturnDate() == null) continue;

            long daysLate = ChronoUnit.DAYS.between(tx.getReturnDate(), LocalDate.now());
            if (daysLate <= 0) continue;

            int fine = (int) daysLate * 20;

            tx.setFineAmount(fine);
            tx.setFineStatus(FineStatus.PENDING);
            tx.setMessage("Overdue by " + daysLate + " day(s). Fine: ₹" + fine);
            transactionRepo.save(tx);
        }
    }

    @Override
    public void payFine(Long transactionId) {
        BorrowingTransaction tx = transactionRepo.findByTransactionId(transactionId);
        if (tx != null && tx.getFineAmount() > 0) {
            tx.setFineStatus(FineStatus.PAID);
            transactionRepo.save(tx);
        }
    }

    @Override
    public List<FineDTO> getAllFines() {
        return transactionRepo.findAll().stream()
                .filter(tx -> tx.getFineAmount() > 0)
                .map(tx -> new FineDTO(
                        tx.getTransactionId(),
                        tx.getMemberId(),
                        tx.getMemberName(),
                        tx.getBookId(),
                        tx.getFineStatus(),
                        tx.getFineAmount()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FineDTO> getFinesByMemberId(Long memberId) {
    	
        MemberDTO memberDto = getCurrentMember(memberId);
        if (memberDto == null) {
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
        return transactionRepo.findByMemberId(memberId).stream()
                .filter(tx -> tx.getFineAmount() > 0)
                .map(tx -> new FineDTO(
                        tx.getTransactionId(),
                        tx.getMemberId(),
                        tx.getMemberName(),
                        tx.getBookId(),
                        tx.getFineStatus(),
                        tx.getFineAmount()))
                .collect(Collectors.toList());
    }
}
