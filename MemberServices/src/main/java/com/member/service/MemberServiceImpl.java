package com.member.service;

import com.member.model.Member;
import com.member.model.Member.MembershipStatus;
import com.member.model.Member.Role;
import com.member.repo.MemberRepo;
import com.member.security.CurrentUser;
import com.member.exception.ResourceNotFoundException;
import com.member.exception.UnauthorizedAccessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepo memberRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    
    @Autowired
    private CurrentUser currentUser;
    
    @Autowired
    public MemberServiceImpl(MemberRepo memberRepo) {
        this.memberRepo = memberRepo;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String registerMember(Member member) {
        if (memberRepo.existsByUsername(member.getUsername())) {
            throw new RuntimeException("Username already taken.");
        }
        member.setRole(Role.MEMBER);
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setMembershipStatus(MembershipStatus.BASIC);
        member.setBorrowingLimit(2);
        memberRepo.save(member);
        return "Member registered successfully.";
    }

    @Override
    public String createAdmin(Member newAdmin) {
        if (memberRepo.existsByUsername(newAdmin.getUsername())) {
            throw new RuntimeException("Username already taken.");
        }
        newAdmin.setRole(Role.ADMIN);
        newAdmin.setPassword(passwordEncoder.encode(newAdmin.getPassword()));
        memberRepo.save(newAdmin);
        return "Admin created successfully.";
    }

    @Override
    @Transactional
    public String updateMember(Long id, Member updated, Member currentUser) {
        if (currentUser == null || currentUser.getMemberId() != id || currentUser.getRole() != Role.MEMBER) {
            throw new UnauthorizedAccessException("Not allowed to update this profile.");
        }
        Member existing = getMemberById(id);
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        existing.setBorrowingLimit(updated.getBorrowingLimit());
        memberRepo.save(existing);
        return "Profile updated.";
    }

    @Override
    @Transactional
    public String updatePassword(Long id, String currentPassword, String newPassword, Member currentUser) {
        if (currentUser == null || currentUser.getMemberId() != id || currentUser.getRole() != Role.MEMBER) {
            throw new UnauthorizedAccessException("Not allowed to change password.");
        }
        Member existing = getMemberById(id);
        if (!passwordEncoder.matches(currentPassword, existing.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        existing.setPassword(passwordEncoder.encode(newPassword));
        memberRepo.save(existing);
        return "Password updated successfully.";
    }

    @Override
    @Transactional
    public String promoteRole(Long id, Member currentUser) {
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only admins can promote.");
        }
        Member m = getMemberById(id);
        m.setRole(Role.ADMIN);
        memberRepo.save(m);
        return "Promoted to ADMIN.";
    }

    @Override
    @Transactional
    public String deleteMemberById(Long id, Member currentUser) {
        if (currentUser == null || currentUser.getMemberId() != id || currentUser.getRole() != Role.MEMBER) {
            throw new UnauthorizedAccessException("Not allowed to delete this account.");
        }
        Member m = getMemberById(id);
        memberRepo.delete(m);
        return "Account deleted.";
    }

    @Override
    public List<Member> getAllMembers() {
        Member user = currentUser.getCurrentUser();
        
        if (user == null) {
            System.out.println("Current user is null");
            throw new UnauthorizedAccessException("No authenticated user found.");
        }

        System.out.println("Authenticated Role: " + user.getRole());

        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only admins can view all.");
        }

        return memberRepo.findAll();
    }


    @Override
    public Member getMemberById(Long id) {
    	Member user = currentUser.getCurrentUser();
        Member m = memberRepo.findById(id).orElseThrow(() -> 
                new ResourceNotFoundException("Member with ID " + id + " not found."));
        if (user.getRole() != Role.ADMIN && user.getMemberId() != id) {
            throw new UnauthorizedAccessException("Access denied.");
        }
        return m;
    }

    @Override
    @Transactional
    public String activateMembership(Long id, int months, Member currentUser) {
        Member m = getMemberById(id);
        if (currentUser.getRole() != Role.MEMBER || currentUser.getMemberId() != id) {
            throw new UnauthorizedAccessException("Not allowed.");
        }
        LocalDate newExpiry = (m.getMembershipExpiryDate() == null)
                ? LocalDate.now().plusMonths(months)
                : m.getMembershipExpiryDate().plusMonths(months);
        m.setMembershipExpiryDate(newExpiry);
        m.setMembershipStatus(MembershipStatus.PRIME);
        memberRepo.save(m);
        return "Membership active until " + newExpiry;
    }

    @Override
    public void updateMembershipStatus(Member member, Member currentUser) {
        if (member.getMembershipExpiryDate() != null &&
                LocalDate.now().isAfter(member.getMembershipExpiryDate())) {
            member.setMembershipStatus(MembershipStatus.EXPIRED);
            memberRepo.save(member);
        }
    }

    @Override
    @Transactional
    public HashMap<String, String> loginMember(Member memberInput) {
        Member dbMember = memberRepo.findByUsername(memberInput.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("Member not found."));

        // Compare encoded password from DB with plaintext password from login request
        if (!passwordEncoder.matches(memberInput.getPassword(), dbMember.getPassword())) {
            throw new UnauthorizedAccessException("Invalid credentials.");
        }

        // Generate token if not present
        String token = dbMember.getMemberToken();
        if (token == null || token.isEmpty()) {
            token = UUID.randomUUID().toString();
            dbMember.setMemberToken(token);
        }
        dbMember.setTokenGeneratedAt(LocalDateTime.now());
        memberRepo.save(dbMember);

        HashMap<String, String> response = new HashMap<>();
        response.put("memberId", String.valueOf(dbMember.getMemberId()));
        response.put("role", dbMember.getRole().name());
        response.put("token", token);
        return response;
    }
    
    public String logoutMember() {
    	Member user = currentUser.getCurrentUser();
    	user.setMemberToken(null);
    	user.setTokenGeneratedAt(null);
        memberRepo.save(user);
        return "Logged out successfully";
    }


}
