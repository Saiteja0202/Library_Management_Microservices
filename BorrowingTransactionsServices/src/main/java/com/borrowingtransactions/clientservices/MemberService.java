package com.borrowingtransactions.clientservices;

import com.borrowingtransactions.DTO.MemberDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "memberservices")
public interface MemberService {

    @GetMapping("/api/members/get/{id}")
    MemberDTO getMemberById(@PathVariable("id") Long id,
                            @RequestHeader("Authorization") String authHeader);
}
