package com.book.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.book.DTO.MemberDTO;

@Component
@RequestScope
public class CurrentUser {
	private MemberDTO member;

	public MemberDTO getCurrentUser() {
		return member;
	}

	public void setCurrentUser(MemberDTO member) {
		this.member = member;
	}
}
