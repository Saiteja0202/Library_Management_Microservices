package com.borrowingtransactions.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.borrowingtransactions.model.AdminRequests;
import com.borrowingtransactions.repo.AdminRequestsRepo;
import com.borrowingtransactions.service.AdminRequestService;



@RestController
@RequestMapping("/admin")
public class AdminRequestController {

	private final AdminRequestsRepo adminRequestsRepo;
	private final AdminRequestService adminRequestService;


	public AdminRequestController(AdminRequestsRepo adminRequestsRepo, AdminRequestService adminRequestService) {
	        this.adminRequestsRepo = adminRequestsRepo;
	        this.adminRequestService = adminRequestService;
	    }

	@GetMapping("/requests")
	public ResponseEntity<List<AdminRequests>> getAllTransactions() {
		List<AdminRequests> requests = adminRequestsRepo.findAll(); // Query the Admin_Requests view
		return ResponseEntity.ok(requests);
	} // Return all requests

	@PostMapping(value = "/accept/{transactionId}/{memberId}/{bookId}", produces = "application/json")
	public ResponseEntity<Map<String, String>> acceptRequest(@PathVariable Long transactionId,
			@PathVariable Long memberId, @PathVariable Long bookId) {

		Map<String, String> response = adminRequestService.acceptRequest(transactionId, memberId, bookId);

		if (response.get("message").contains("not found")) {
			return ResponseEntity.status(404).body(response);
		} else if (response.get("message").startsWith("Invalid")) {
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/reject/{transactionId}/{memberId}/{bookId}", produces = "application/json")
	public ResponseEntity<Map<String, String>> rejectRequest(@PathVariable Long transactionId,
			@PathVariable Long memberId, @PathVariable Long bookId) {

		Map<String, String> response = adminRequestService.rejectRequest(transactionId, memberId, bookId);

		if (response.get("message").contains("not found")) {
			return ResponseEntity.status(404).body(response);
		} else if (response.get("message").startsWith("Invalid")) {
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}
}
