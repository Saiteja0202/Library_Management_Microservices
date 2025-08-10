package com.book.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.book.DTO.MemberDTO;
import com.book.member.service.MemberClient;
import com.book.model.Book;
import com.book.security.CurrentUser;
import com.book.service.BookService;

import java.util.List;



@RestController
@Validated
@RequestMapping("/api/books")
public class BookController {
	
	
	private MemberDTO memberDTO;
	
	private final CurrentUser currentUser;
	
	private final MemberClient memberClient;

    private final BookService bookService;

    public BookController(BookService bookService,MemberClient memberClient,CurrentUser currentUser) {
        this.bookService = bookService;
        this.memberClient=memberClient;
        this.currentUser=currentUser;
        
    }

    @PostMapping("/admin/add/{memberId}")
    public ResponseEntity<String> addBook(@RequestBody Book book,
                                          @RequestHeader("Authorization") String authHeader,
                                          @PathVariable("memberId") long memberId) {
        
        MemberDTO member = memberClient.getById(memberId, authHeader);
        currentUser.setCurrentUser(member);
        return ResponseEntity.ok(bookService.addBook(book));
    }


    @DeleteMapping("/admin/delete-by-id/{id}/{memberId}")
    public ResponseEntity<String> deleteBook(@PathVariable int id,
                                             @PathVariable("memberId") long memberId,
                                             @RequestHeader("Authorization") String authHeader) {

        MemberDTO member = memberClient.getById(memberId, authHeader);
        currentUser.setCurrentUser(member);

        return ResponseEntity.ok(bookService.deleteBook(id));
    }

    @PutMapping("/admin/update-book/{id}/{memberId}")
    public ResponseEntity<String> updateBook(@PathVariable int id,
                                             @PathVariable("memberId") long memberId,
                                             @RequestHeader("Authorization") String authHeader,
                                             @RequestBody Book book) {

        MemberDTO member = memberClient.getById(memberId, authHeader);
        currentUser.setCurrentUser(member);

        return ResponseEntity.ok(bookService.updateBook(id, book));
    }


    @GetMapping("/get-books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/get-book-by-id/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable int id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }


    @GetMapping("/search-title/{title}")
    public ResponseEntity<List<Book>> searchByTitle(@PathVariable String title) {
        return ResponseEntity.ok(bookService.searchByTitle(title));
    }


    @GetMapping("/search-genre/{genre}")
    public ResponseEntity<List<Book>> searchByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(bookService.searchByGenre(genre));
    }



    @GetMapping("/search-author/{author}")
    public ResponseEntity<List<Book>> searchByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(bookService.searchByAuthor(author));
    }
    
    
    @GetMapping("/get-book-with-member/{memberId}")
    public MemberDTO getBookWithMember(@PathVariable long memberId) {
        return bookService.getBookWithMemberDetails(memberId);
    }
    
    
    @GetMapping("/api/members/get/{id}")
    MemberDTO getById(@PathVariable("id") long memberId,
                      @RequestHeader("Authorization") String authHeader)
    {
    	memberDTO.toString();
    	return memberClient.getById(memberId, authHeader);
    }

}

