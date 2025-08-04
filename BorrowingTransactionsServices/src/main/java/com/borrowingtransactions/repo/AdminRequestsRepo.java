package com.borrowingtransactions.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.borrowingtransactions.model.AdminRequests;


public interface AdminRequestsRepo extends JpaRepository<AdminRequests, Long> {
	
	List<AdminRequests> findAll();

}
