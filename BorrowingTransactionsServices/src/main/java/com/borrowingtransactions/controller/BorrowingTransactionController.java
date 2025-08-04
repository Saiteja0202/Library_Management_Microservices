package com.borrowingtransactions.controller;


import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.borrowingtransactions.DTO.BookDTO;
import com.borrowingtransactions.DTO.FineDTO;
import com.borrowingtransactions.DTO.NotificationDTO;
import com.borrowingtransactions.model.BorrowingTransaction;
import com.borrowingtransactions.service.BorrowingTransactionService;
import com.borrowingtransactions.service.FinesService;
import com.borrowingtransactions.service.NotificationServices;



@RestController
@RequestMapping("/borrowing")
public class BorrowingTransactionController {

	 private final FinesService finesService;
	
    private final BorrowingTransactionService transactionService;
    private final NotificationServices notificationService;

    public BorrowingTransactionController(BorrowingTransactionService transactionService,
    		NotificationServices notificationService,FinesService finesService) {
        this.transactionService = transactionService;
        this.finesService=finesService;
        this.notificationService=notificationService;
    }


	@PostMapping("/borrow/{memberId}/{bookId}")
	public ResponseEntity<String> borrowBook(@PathVariable Long memberId, @PathVariable Long bookId) {
	    String result = transactionService.borrowBook(bookId, memberId); 
	    return ResponseEntity.ok(result); 
	    
	}

	@PostMapping("/return/{memberId}/{bookId}")
	public ResponseEntity<String> returnBook(@PathVariable Long memberId, @PathVariable Long bookId) {
	    String result = transactionService.returnBook(bookId, memberId);
	    return ResponseEntity.ok(result);
	}


    @GetMapping("/borrowing-transactions")
    public ResponseEntity<List<BorrowingTransaction>> getAllTransactions() {
        List<BorrowingTransaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/get-borrowed-books/{memberId}")
    public List<BookDTO> getAllBorrowedBooks(@PathVariable Long memberId){
        return transactionService.getBooksBorrowedByMember(memberId);
    }
    
    
    @GetMapping("/fines")
    public ResponseEntity<List<FineDTO>> getAllFines() {
        return ResponseEntity.ok(finesService.getAllFines());
    }

    @GetMapping("/fines/{memberId}")
    public ResponseEntity<List<FineDTO>> getFinesByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(finesService.getFinesByMemberId(memberId));
    }

    @PostMapping("/pay/fine/{transactionId}")
    public ResponseEntity<String> payFine(@PathVariable Long transactionId) {
        finesService.payFine(transactionId);
        return ResponseEntity.ok("Fine paid successfully for transaction ID: " + transactionId);
    }
    
    @GetMapping("/notifications/{memberId}")
    public ResponseEntity<?> getNotificationsByMemberId(@PathVariable Long memberId) {
        try {
            List<NotificationDTO> notifications = notificationService.getNotificationsByMemberId(memberId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching notifications");
        }
    }


    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

}
