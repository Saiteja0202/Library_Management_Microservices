package com.member.security;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import com.member.model.Member;
import com.member.repo.MemberRepo;

import jakarta.transaction.Transactional;

@Component
public class Scheduler {
		
	private final MemberRepo memberRepo;
	 
    public Scheduler(MemberRepo memberRepo) {
        this.memberRepo = memberRepo;
    }
 
    @Scheduled(fixedRate = 60 * 60 * 1000*24)
    @Transactional
    public void clearExpiredTokens() {
        LocalDateTime expiry = LocalDateTime.now().minusMinutes(60);
        if (expiry!= null) {
 
            List<Member> expiredUsers = memberRepo.findExpiredTokens(expiry);
 
	        for (Member member : expiredUsers) {
	        	memberRepo.deleteById(member.getMemberId());
	        }
        }
     }
}
