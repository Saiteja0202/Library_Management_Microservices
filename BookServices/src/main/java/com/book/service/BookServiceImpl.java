package com.book.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;



import jakarta.servlet.http.HttpServletRequest;


import com.book.exception.ResourceNotFoundException;
import com.book.exception.UnauthorizedAccessException;
import com.book.member.service.MemberClient;
import com.book.DTO.MemberDTO;
import com.book.DTO.MemberDTO.Role;
import com.book.model.Book;

import com.book.repo.BookRepo;
import com.book.security.CurrentUser;



@Service
public class BookServiceImpl implements BookService {
	
	private final BookRepo bookRepo;
	private final MemberClient memberClient;
    private final CurrentUser currentUser;

    public BookServiceImpl(BookRepo bookRepo,MemberClient memberClient ,CurrentUser currentUser) {
        this.bookRepo = bookRepo;
        this.memberClient = memberClient;
        this.currentUser = currentUser;
    }

    public String addBook(Book book) {
    	
    	
    	
    	 System.out.println("THE CURRENT USER IN THE ADD BOOKS **********   "+currentUser.getCurrentUser().getName());
    	 if(currentUser.getCurrentUser().getRole() !=Role.ADMIN) {
         	
         	throw new UnauthorizedAccessException("User Not Allowed to Add Book");
         }
    	 
        bookRepo.save(book);
        return "Book has been added successfully.";
    }
    @Transactional
    public String deleteBook(int id) {
    	
        Book exist = getBookById(id);
        
        if(currentUser.getCurrentUser().getRole() != Role.ADMIN) {
       	
        	throw new UnauthorizedAccessException("User Not Allowed to Delete Book");
       	}
        
        bookRepo.delete(exist);
        
        return "Book has been deleted successfully.";
    }
    @Transactional
    public String updateBook(int id, Book updated) {
        Book exist = getBookById(id);
        
        if(currentUser.getCurrentUser().getRole() !=Role.ADMIN) {
        	
        	throw new UnauthorizedAccessException("User Not Allowed to Update Book");
        }

        exist.setBookName(updated.getBookName());
        exist.setGenre(updated.getGenre());
        exist.setISBN(updated.getISBN());
        exist.setAuthor(updated.getAuthor());
        exist.setAvailableCopies(updated.getAvailableCopies());
        exist.setYearPublished(updated.getYearPublished());

        bookRepo.save(exist);
        return "Book has been updated successfully.";
    }

    public List<Book> getAllBooks() {
    	
        return bookRepo.findAll();
    }

    public Book getBookById(int id) {
        return bookRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found"));
    }

    public List<Book> searchByTitle(String title) {
        return bookRepo.findByBookNameContainingIgnoreCase(title);
    }

    public List<Book> searchByGenre(String genre) {
        return bookRepo.findByGenreIgnoreCase(genre);
    }

    public List<Book> searchByAuthor(String author) {
        return bookRepo.findByAuthorContainingIgnoreCase(author);
    }
    
    
    
    private String extractAuthToken() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String token = request.getHeader("Authorization");
            if (token != null && !token.isBlank()) {
                return token;
            }
        }
        return null;
    }
    
    
    
    public MemberDTO getBookWithMemberDetails(long memberId) {
    	
        String token = extractAuthToken();	
        if (currentUser.getCurrentUser() != null) {
            return currentUser.getCurrentUser();
        }
        
        MemberDTO member = memberClient.getById(memberId, token);
        memberClient.getById(memberId, token);
        currentUser.setCurrentUser(member);
        
        return member;
    }
}


