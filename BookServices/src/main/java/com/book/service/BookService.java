package com.book.service;

import java.util.List;

import com.book.DTO.MemberDTO;
import com.book.model.Book;

import jakarta.transaction.Transactional;

public interface BookService {

	
	public String addBook(com.book.model.Book book);
	
	@Transactional
	public String deleteBook(int id);
	
	@Transactional
	public String updateBook(int id, Book book);
	public Book getBookById(int id);
	public List<Book> getAllBooks();
	public List<Book> searchByTitle(String title);
	public List<Book> searchByGenre(String genre);
	public List<Book> searchByAuthor(String author);

	MemberDTO getBookWithMemberDetails(long memberId);

}
