package com.borrowingtransactions.DTO;

import com.borrowingtransactions.model.BorrowingTransaction.FineStatus;

public class FineDTO {
    private Long transactionId;
    private Long memberId;
    private String memberName;
    private Long bookId;
    private FineStatus fineStatus;
    private int fineAmount;

    public FineDTO(Long transactionId, Long memberId, String memberName, Long bookId, FineStatus fineStatus, int fineAmount) {
        this.transactionId = transactionId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.bookId = bookId;
        this.fineStatus = fineStatus;
        this.fineAmount = fineAmount;
    }

    // Getters and setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public FineStatus getFineStatus() {
        return fineStatus;
    }

    public void setFineStatus(FineStatus fineStatus) {
        this.fineStatus = fineStatus;
    }

    public int getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(int fineAmount) {
        this.fineAmount = fineAmount;
    }
}
