package com.borrowingtransactions.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import com.borrowingtransactions.DTO.BookDTO;
import com.borrowingtransactions.DTO.MemberDTO;
import com.borrowingtransactions.clientservices.BookService;
import com.borrowingtransactions.clientservices.MemberService;
import com.borrowingtransactions.exception.BorrowingLimitExceededException;
import com.borrowingtransactions.exception.MemberNotFoundException;
import com.borrowingtransactions.model.BorrowingTransaction;
import com.borrowingtransactions.repo.BorrowingTransactionRepo;
import com.borrowingtransactions.security.CurrentUser;

@Service
public class BorrowingTransactionServiceImpl implements BorrowingTransactionService {

    private final BorrowingTransactionRepo transactionRepo;
    private final BookService bookClient;
    private final MemberService memberClient;
    private final CurrentUser currentUser;

    public BorrowingTransactionServiceImpl(
            BorrowingTransactionRepo transactionRepo,
            BookService bookClient,
            MemberService memberClient,
            CurrentUser currentUser) {
        this.transactionRepo = transactionRepo;
        this.bookClient = bookClient;
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

    @Override
    public String borrowBook(Long bookId, Long memberId) {
        BookDTO bookDto = bookClient.getBookById(bookId);
        if (bookDto == null) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }

        MemberDTO memberDto = getCurrentMember(memberId);
        if (memberDto == null) {
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }

        if (bookDto.getAvailableCopies() <= 0) {
            return "No copies available for borrowing.";
        }

        if (memberDto.getBorrowingLimit() <= 0) {
            throw new BorrowingLimitExceededException("Borrowing limit reached.");
        }

        BorrowingTransaction txn = new BorrowingTransaction();
        txn.setBookId(bookDto.getBookId());
        txn.setBookName(bookDto.getBookName());
        txn.setMemberId(memberDto.getMemberId());
        txn.setMemberName(memberDto.getName());
        txn.setBorrowDate(LocalDate.now());
        txn.setReturnDate(LocalDate.now().plusDays(10));
        txn.setStatus(BorrowingTransaction.Status.PENDING);

        transactionRepo.save(txn);

        return "Book borrow request is pending for admin approval.";
    }

    @Override
    public String returnBook(Long bookId, Long memberId) {
        BorrowingTransaction txn = transactionRepo.findByBookIdAndMemberId(bookId, memberId);
        if (txn == null || 
            !(txn.getStatus() == BorrowingTransaction.Status.BORROWED 
              || txn.getStatus() == BorrowingTransaction.Status.RETURN_REJECTED)) {
            return "No active borrowing record found for return.";
        }

        txn.setReturnDate(LocalDate.now());
        txn.setStatus(BorrowingTransaction.Status.RETURN_PENDING);
        transactionRepo.save(txn);

        return "Book return request submitted for approval.";
    }

    @Override
    public List<BorrowingTransaction> getTransactions(Long memberId) {
        return transactionRepo.findByMemberId(memberId);
    }

    @Override
    public List<BorrowingTransaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    @Override
    public List<BookDTO> getBooksBorrowedByMember(Long memberId) {
        List<BorrowingTransaction> transactions = transactionRepo.findByMemberId(memberId);

        return transactions.stream()
                .filter(txn -> txn.getStatus() == BorrowingTransaction.Status.BORROWED)
                .map(txn -> bookClient.getBookById(txn.getBookId()))
                .filter(book -> book != null)
                .collect(Collectors.toList());
    }
}
