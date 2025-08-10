package com.member.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.member.exception.UnauthorizedAccessException;
import com.member.model.Member;
import com.member.model.Member.Role;
import com.member.repo.MemberRepo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final MemberRepo memberRepo;  // Use MemberRepo instead of MemberTokenRepo
    private final CurrentUser currentUser;

    public AuthenticationFilter(MemberRepo memberRepo, CurrentUser currentUser) {
        this.memberRepo = memberRepo;
        this.currentUser = currentUser;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // Skip the filter for public endpoints (register, login, etc.)
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the Authorization token from the header
        String token = getTokenFromRequest(request);

        if (token == null || token.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization token");
            return;
        }

  
        Member member = memberRepo.findByMemberToken(token).orElse(null);

        if (member == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        // Optionally, handle token expiration
        if (isTokenExpired(member)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired token");
            return;
        }

        System.err.println("Member extracted: " + member.getName() + ", Role: " + member.getRole());
        currentUser.setCurrentUser(member);
        request.setAttribute("currentUser", member);

        filterChain.doFilter(request, response);

    }

    private boolean isPublicEndpoint(String path) {
        return path.equals("/api/members/register")
            || path.equals("/api/members/login")
            || path.equals("/api/members/admin/create")
            || path.equals("/api/members/get");
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
        	return token.startsWith("Bearer ") ? token.substring(7) : token;  
        }
        return null;  
    }

    private boolean isTokenExpired(Member member) {
        // Check if the token has expired, if applicable
        // For simplicity, we assume that the member's token expires after a certain time
        // You can add your logic here if token expiration is relevant (e.g., based on `tokenGeneratedAt`).
        return member.getTokenGeneratedAt() != null && 
               member.getTokenGeneratedAt().isBefore(java.time.LocalDateTime.now().minusHours(1));  // Example of 1-hour expiration
    }
}
