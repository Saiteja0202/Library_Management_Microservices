package com.borrowingtransactions.service;

import java.util.List;

import com.borrowingtransactions.DTO.BookDTO;
import com.borrowingtransactions.model.BorrowingTransaction;


public interface BorrowingTransactionService {
    String borrowBook(Long bookId, Long memberId);
    String returnBook(Long bookId, Long memberId);
    public List<BorrowingTransaction> getTransactions(Long memberId);
    public List<BorrowingTransaction> getAllTransactions();
    public List<BookDTO> getBooksBorrowedByMember(Long memberId);

}