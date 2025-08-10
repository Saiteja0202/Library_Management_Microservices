package com.borrowingtransactions.service;

import java.util.List;

import com.borrowingtransactions.DTO.FineDTO;

public interface FinesService {
    void processDailyFines();
    void payFine(Long transactionId);
    List<FineDTO> getAllFines();
    List<FineDTO> getFinesByMemberId(Long memberId);
}
