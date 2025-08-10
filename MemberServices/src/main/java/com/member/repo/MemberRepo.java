package com.member.repo;

import com.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepo extends JpaRepository<Member, Long> {
	
	
    boolean existsByUsername(String username);
    Optional<Member> findByUsername(String username);

    // Use the actual token field name from Member entity
    Optional<Member> findByMemberToken(String memberToken);

    @Query("SELECT m FROM Member m WHERE m.tokenGeneratedAt < :expiry")
    List<Member> findExpiredTokens(LocalDateTime expiry);
}
