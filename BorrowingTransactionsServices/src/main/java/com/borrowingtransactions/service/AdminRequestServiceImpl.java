package com.borrowingtransactions.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.borrowingtransactions.clientservices.BookService;
import com.borrowingtransactions.clientservices.MemberService;
import com.borrowingtransactions.DTO.BookDTO;
import com.borrowingtransactions.DTO.MemberDTO;
import com.borrowingtransactions.exception.BookNotFoundException;
import com.borrowingtransactions.exception.MemberNotFoundException;
import com.borrowingtransactions.model.AdminRequests;
import com.borrowingtransactions.model.BorrowingTransaction;
import com.borrowingtransactions.repo.AdminRequestsRepo;
import com.borrowingtransactions.repo.BorrowingTransactionRepo;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AdminRequestServiceImpl implements AdminRequestService {

    private final BorrowingTransactionRepo borrowingTransactionRepo;
    private final AdminRequestsRepo adminRequestsRepo;
    private final BookService bookService;
    private final MemberService memberService;

    public AdminRequestServiceImpl(BorrowingTransactionRepo borrowingTransactionRepo,
                                   AdminRequestsRepo adminRequestsRepo,
                                   BookService bookService,
                                   MemberService memberService) {
        this.borrowingTransactionRepo = borrowingTransactionRepo;
        this.adminRequestsRepo = adminRequestsRepo;
        this.bookService = bookService;
        this.memberService = memberService;
    }
    
    private String extractAuthToken() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String token = request.getHeader("Authorization");
            if (token != null && !token.isBlank()) {
                return token;
            }
        }
        return null;
    }

    @Override
    public List<AdminRequests> getAllRequests() {
        return adminRequestsRepo.findAll();
    }

    @Override
    public Map<String, String> acceptRequest(Long transactionId, Long memberId, Long bookId) {
        Map<String, String> response = new HashMap<>();
        BorrowingTransaction transaction = borrowingTransactionRepo.findByTransactionId(transactionId);

        if (transaction == null) {
            response.put("message", "Borrowing transaction not found.");
            return response;
        }

        BookDTO book = bookService.getBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book not found with ID: " + bookId);
        }

        MemberDTO member = memberService.getMemberById(memberId, extractAuthToken());
        if (member == null) {
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }

        switch (transaction.getStatus()) {
            case PENDING:
                transaction.setStatus(BorrowingTransaction.Status.BORROWED);
                borrowingTransactionRepo.save(transaction);
                response.put("message", "Request accepted: status changed from PENDING → BORROWED");
                break;

            case RETURN_PENDING:
                transaction.setStatus(BorrowingTransaction.Status.RETURNED);
                borrowingTransactionRepo.save(transaction);
                response.put("message", "Request accepted: status changed from RETURN_PENDING → RETURNED");
                break;

            default:
                response.put("message", "Invalid status for accept action: " + transaction.getStatus());
                break;
        }

        return response;
    }

    @Override
    public Map<String, String> rejectRequest(Long transactionId, Long memberId, Long bookId) {
        Map<String, String> response = new HashMap<>();
        BorrowingTransaction transaction = borrowingTransactionRepo.findByTransactionId(transactionId);

        if (transaction == null) {
            response.put("message", "Borrowing transaction not found.");
            return response;
        }
        MemberDTO member = memberService.getMemberById(memberId, extractAuthToken());
        if (member == null) {
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }

        switch (transaction.getStatus()) {
            case PENDING:
                transaction.setStatus(BorrowingTransaction.Status.BORROW_REJECTED);
                borrowingTransactionRepo.save(transaction);
                response.put("message", "Request rejected: status changed from PENDING → BORROW_REJECTED");
                break;

            case RETURN_PENDING:
                transaction.setStatus(BorrowingTransaction.Status.RETURN_REJECTED);
                borrowingTransactionRepo.save(transaction);
                response.put("message", "Request rejected: status changed from RETURN_PENDING → RETURN_REJECTED");
                break;

            default:
                response.put("message", "Invalid status for reject action: " + transaction.getStatus());
                break;
        }

        return response;
    }
}
