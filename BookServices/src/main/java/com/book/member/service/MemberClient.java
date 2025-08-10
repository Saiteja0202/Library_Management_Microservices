package com.book.member.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import com.book.DTO.MemberDTO;




@Service
@FeignClient(name = "memberservices")
public interface MemberClient {

    @GetMapping("/api/members/get/{id}")
    MemberDTO getById(@PathVariable("id") long memberId,@RequestHeader("Authorization") String authHeader);
}



