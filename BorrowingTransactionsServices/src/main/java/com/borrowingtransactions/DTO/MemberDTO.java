package com.borrowingtransactions.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;



import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class MemberDTO {

    private long memberId;
    private String name;
    private String email;
    private int borrowingLimit;
    public int getBorrowingLimit() {
		return borrowingLimit;
	}
	public void setBorrowingLimit(int borrowingLimit) {
		this.borrowingLimit = borrowingLimit;
	}
	public enum Role {
        ADMIN,
        MEMBER
    }

    @Enumerated(EnumType.STRING)
    private Role role;
    
    private String memberToken;
    private LocalDateTime tokenGeneratedAt;

    public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	private Role loginRole;
    private String loginUsername;
    private String loginPassword;
	public long getMemberId() {
		return memberId;
	}
	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMemberToken() {
		return memberToken;
	}
	public void setMemberToken(String memberToken) {
		this.memberToken = memberToken;
	}
	public LocalDateTime getTokenGeneratedAt() {
		return tokenGeneratedAt;
	}
	public void setTokenGeneratedAt(LocalDateTime tokenGeneratedAt) {
		this.tokenGeneratedAt = tokenGeneratedAt;
	}
	public Role getLoginRole() {
		return loginRole;
	}
	public void setLoginRole(Role loginRole) {
		this.loginRole = loginRole;
	}
	public String getLoginUsername() {
		return loginUsername;
	}
	public void setLoginUsername(String loginUsername) {
		this.loginUsername = loginUsername;
	}
	public String getLoginPassword() {
		return loginPassword;
	}
	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}
	@Override
	public String toString() {
		return "MemberDTO [memberId=" + memberId + ", name=" + name + ", role=" + role + ", memberToken=" + memberToken
				+ ", tokenGeneratedAt=" + tokenGeneratedAt + ", loginRole=" + loginRole + ", loginUsername="
				+ loginUsername + ", loginPassword=" + loginPassword + "]";
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

    
    
    

}
