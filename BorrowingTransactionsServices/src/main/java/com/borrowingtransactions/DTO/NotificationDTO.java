package com.borrowingtransactions.DTO;

import java.time.LocalDate;

public class NotificationDTO {

    private Long transactionId;
    private Long memberId;
    private String memberName;
    private Long bookId;
    private String bookName;
    private String message;
    private LocalDate dateSent;
    private int overdueDays;

    public NotificationDTO(Long transactionId, Long memberId, String memberName, Long bookId, String bookName, 
                           String message, LocalDate dateSent, int overdueDays) {
        this.transactionId = transactionId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.bookId = bookId;
        this.bookName = bookName;
        this.message = message;
        this.dateSent = dateSent;
        this.overdueDays = overdueDays;
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

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getDateSent() {
        return dateSent;
    }

    public void setDateSent(LocalDate dateSent) {
        this.dateSent = dateSent;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(int overdueDays) {
        this.overdueDays = overdueDays;
    }
}
