package com.borrowingtransactions.service;

import java.util.List;
import java.util.Map;

import com.borrowingtransactions.model.AdminRequests;

public interface AdminRequestService {
    public List<AdminRequests> getAllRequests();
    Map<String, String> acceptRequest(Long transactionId, Long memberId, Long bookId);
    Map<String, String> rejectRequest(Long transactionId, Long memberId, Long bookId);
}
