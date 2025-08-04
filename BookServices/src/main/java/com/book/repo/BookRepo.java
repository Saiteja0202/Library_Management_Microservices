package com.book.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.book.model.Book;

@Repository
public interface BookRepo extends JpaRepository<Book, Integer>{
	
	List<Book> findByBookNameContainingIgnoreCase(String title);
	List<Book> findByGenreIgnoreCase(String Genre);
	List<Book> findByAuthorContainingIgnoreCase(String author);
	
}
