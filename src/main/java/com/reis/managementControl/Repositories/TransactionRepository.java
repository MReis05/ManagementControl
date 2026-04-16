package com.reis.managementControl.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.managementControl.Entities.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	
	List<Transaction> findByTransactionTimeBetween(LocalDateTime dateTime, LocalDateTime finalDateTime);
}
