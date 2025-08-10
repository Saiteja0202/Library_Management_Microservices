package com.borrowingtransactions.clientservices;

import com.borrowingtransactions.DTO.BookDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "bookservices", path = "/api/books")
public interface BookService {

	@GetMapping("/get-book-by-id/{id}")
	BookDTO getBookById(@PathVariable("id") Long id);
	
}
