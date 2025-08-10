package com.borrowingtransactions.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="borrowing_transactions")
public class BorrowingTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionId;

	private Long bookId;
	
	private Long memberId;
	
	private String memberName;
	
	private String bookName;

	
	private LocalDate borrowDate;
	private LocalDate returnDate;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private Status status;

	public enum Status {
		BORROW_REJECTED,PENDING,BORROWED,RETURN_PENDING, RETURNED,RETURN_REJECTED
	}
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private FineStatus fineStatus;

	public enum FineStatus {
		PENDING,PAID
	}
	

	private int fineAmount;
	
	private String message;
	
	
	
	


	public int getFineAmount() {
		return fineAmount;
	}


	public void setFineAmount(int fineAmount) {
		this.fineAmount = fineAmount;
	}


	public BorrowingTransaction(Long transactionId, Long bookId, Long memberId, String memberName, String bookName,
			LocalDate borrowDate, LocalDate returnDate, Status status, FineStatus fineStatus, int fineAmount,
			String message) {
		super();
		this.transactionId = transactionId;
		this.bookId = bookId;
		this.memberId = memberId;
		this.memberName = memberName;
		this.bookName = bookName;
		this.borrowDate = borrowDate;
		this.returnDate = returnDate;
		this.status = status;
		this.fineStatus = fineStatus;
		this.fineAmount = fineAmount;
		this.message = message;
	}


	

	public BorrowingTransaction() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getMemberName() {
		return memberName;
	}


	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}


	public String getBookName() {
		return bookName;
	}


	public void setBookName(String bookName) {
		this.bookName = bookName;
	}


	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public LocalDate getBorrowDate() {
		return borrowDate;
	}

	public void setBorrowDate(LocalDate borrowDate) {
		this.borrowDate = borrowDate;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	public FineStatus getFineStatus() {
		return fineStatus;
	}


	public void setFineStatus(FineStatus fineStatus) {
		this.fineStatus = fineStatus;
	}

}
