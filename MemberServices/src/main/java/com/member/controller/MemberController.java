package com.member.controller;

import com.member.model.Member;
import com.member.security.CurrentUser;
import com.member.service.MemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private CurrentUser currentUser;

    @PostMapping("/register")
    public String register(@RequestBody Member member) {
        return memberService.registerMember(member);
    }

    @PostMapping("/admin/create")
    public String createAdmin(@RequestBody Member member) {
        return memberService.createAdmin(member);
    }

    @PostMapping("/login")
    public HashMap<String, String> login(@RequestBody Member member) {
        return memberService.loginMember(member);
    }

    @GetMapping("/get-all")
    public List<Member> getAll() {
        return memberService.getAllMembers();
    }


    @GetMapping("/get/{id}")
    public Member getById(@PathVariable Long id) {
        return memberService.getMemberById(id);
    }

    @PutMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @RequestBody Member updated,
                         @RequestAttribute Member currentUser) {
        return memberService.updateMember(id, updated, currentUser);
    }

    @PutMapping("/{id}/password")
    public String changePassword(@PathVariable Long id,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestAttribute Member currentUser) {
        return memberService.updatePassword(id, currentPassword, newPassword, currentUser);
    }

    @PutMapping("/{id}/membership")
    public String activateMembership(@PathVariable Long id,
                                     @RequestParam int months,
                                     @RequestAttribute Member currentUser) {
        return memberService.activateMembership(id, months, currentUser);
    }

    @PutMapping("/{id}/promote")
    public String promoteToAdmin(@PathVariable Long id, @RequestAttribute Member currentUser) {
        return memberService.promoteRole(id, currentUser);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, @RequestAttribute Member currentUser) {
        return memberService.deleteMemberById(id, currentUser);
    }
    
    
    @PutMapping("/logout")
    public String logout() {
        return memberService.logoutMember();
    }

}
