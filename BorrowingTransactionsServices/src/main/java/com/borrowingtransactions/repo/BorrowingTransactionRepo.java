package com.borrowingtransactions.repo;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.borrowingtransactions.model.BorrowingTransaction;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface BorrowingTransactionRepo extends JpaRepository<BorrowingTransaction, Long> {
	List<BorrowingTransaction> findByMemberId(Long memberId);
	void deleteByMemberId(Long memberId);
	boolean existsByBookIdAndStatus(Long bookId, BorrowingTransaction.Status status);

	@Query("SELECT bt FROM BorrowingTransaction bt WHERE bt.bookId = :bookId AND bt.memberId = :memberId")
	BorrowingTransaction findByBookIdAndMemberId(@Param("bookId") Long bookId, @Param("memberId") Long memberId);

	

    @Query("SELECT bt FROM BorrowingTransaction bt")
    List<BorrowingTransaction> findAllWithMemberAndBook();
    
    @Query("SELECT b FROM BorrowingTransaction b WHERE b.transactionId = :transactionId")
    BorrowingTransaction findByTransactionId(@Param("transactionId") Long transactionId);

    
    
    @Query("SELECT bt FROM BorrowingTransaction bt " +
    	       "WHERE bt.status IN :statuses " +
    	       "AND bt.returnDate IS NOT NULL " +
    	       "AND bt.returnDate < :date")
    	List<BorrowingTransaction> findOverdueTransactions(
    	    @Param("statuses") List<BorrowingTransaction.Status> statuses,
    	    @Param("date") LocalDate date
    	);

    
    
    @Query("SELECT bt FROM BorrowingTransaction bt WHERE bt.status IN :statuses")
    List<BorrowingTransaction> findByStatusIn(@Param("statuses") List<BorrowingTransaction.Status> statuses);

    @Query("SELECT bt FROM BorrowingTransaction bt WHERE bt.memberId = :memberId AND bt.status IN :statuses")
    List<BorrowingTransaction> findByMemberIdAndStatusIn(@Param("memberId") Long memberId, @Param("statuses") List<BorrowingTransaction.Status> statuses);

    
}

