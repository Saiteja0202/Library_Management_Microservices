package com.member.service;

import com.member.model.Member;

import java.util.HashMap;
import java.util.List;

public interface MemberService {

    String registerMember(Member member);

    String createAdmin(Member newAdmin);

    String updateMember(Long id, Member updated, Member currentUser);

    String updatePassword(Long id, String currentPassword, String newPassword, Member currentUser);

    String promoteRole(Long id, Member currentUser);

    String deleteMemberById(Long id, Member currentUser);

    List<Member> getAllMembers();

    Member getMemberById(Long id);

    String activateMembership(Long id, int months, Member currentUser);

    void updateMembershipStatus(Member member, Member currentUser);

    HashMap<String, String> loginMember(Member meber);
    
    public String logoutMember();
}
