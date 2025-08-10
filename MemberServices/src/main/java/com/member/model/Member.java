package com.member.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberId;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 10, max = 10)
    private String phone;

    @NotBlank
    private String address;

    @NotBlank
    @Size(min = 4, max = 20)
    private String username;

    @NotBlank
    @Size(min = 8)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private int borrowingLimit = 2;
    private LocalDate membershipExpiryDate;

    // ========== Enums ==========
    public enum MembershipStatus {
        BASIC,
        PRIME,
        EXPIRED
    }

    public enum Role {
        ADMIN,
        MEMBER
    }

    @Enumerated(EnumType.STRING)
    private MembershipStatus membershipStatus = MembershipStatus.BASIC;

    @Enumerated(EnumType.STRING)
    private Role role = Role.MEMBER;

    // ========== Token Info ==========
    private String memberToken;
    private LocalDateTime tokenGeneratedAt;

    // ========== Login Details ==========
    @Transient
    private Role loginRole;

    @Transient
    private String loginUsername;

    @Transient
    private String loginPassword;


    // ========== Business Logic ==========
    public boolean isMembershipActive() {
        return membershipExpiryDate != null && LocalDate.now().isBefore(membershipExpiryDate);
    }

    public MembershipStatus getMembershipStatus() {
        if (membershipExpiryDate == null || LocalDate.now().isAfter(membershipExpiryDate)) {
            return MembershipStatus.EXPIRED;
        }
        return membershipStatus;
    }

    public void setMembershipStatus(MembershipStatus status) {
        if (this.membershipStatus != MembershipStatus.PRIME || status == MembershipStatus.EXPIRED) {
            this.membershipStatus = status;
        }
    }

    public void setRole(Role role) {
        if (this.role != Role.ADMIN) {
            this.role = role;
        }
    }

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getBorrowingLimit() {
		return borrowingLimit;
	}

	public void setBorrowingLimit(int borrowingLimit) {
		this.borrowingLimit = borrowingLimit;
	}

	public LocalDate getMembershipExpiryDate() {
		return membershipExpiryDate;
	}

	public void setMembershipExpiryDate(LocalDate membershipExpiryDate) {
		this.membershipExpiryDate = membershipExpiryDate;
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
		this.loginUsername = username;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public Role getRole() {
		return role;
	}

	public Member(long memberId, @NotBlank String name, @NotBlank @Email String email,
			@NotBlank @Size(min = 10, max = 10) String phone, @NotBlank String address,
			@NotBlank @Size(min = 4, max = 20) String username, @NotBlank @Size(min = 8) String password,
			int borrowingLimit, LocalDate membershipExpiryDate, MembershipStatus membershipStatus, Role role,
			String memberToken, LocalDateTime tokenGeneratedAt, Role loginRole, String loginUsername,
			String loginPassword) {
		super();
		this.memberId = memberId;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.username = username;
		this.password = password;
		this.borrowingLimit = borrowingLimit;
		this.membershipExpiryDate = membershipExpiryDate;
		this.membershipStatus = membershipStatus;
		this.role = role;
		this.memberToken = memberToken;
		this.tokenGeneratedAt = tokenGeneratedAt;
		this.loginRole = loginRole;
		this.loginUsername = loginUsername;
		this.loginPassword = loginPassword;
	}

	public Member() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
    

    // Getters and Setters for all fields...
}
