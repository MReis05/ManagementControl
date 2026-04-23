package com.reis.managementControl.Services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.managementControl.Entities.Cashier;
import com.reis.managementControl.Entities.Transaction;
import com.reis.managementControl.Repositories.TransactionRepository;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository repository;
	
	public List<Transaction> findAll(){
		return repository.findAll();
	}
	
	public List<Transaction> findByDate(LocalDate dateTime, LocalDate finalDateTime){
		LocalDateTime start = dateTime.atStartOfDay();
		if (finalDateTime == null) {
			LocalDateTime end = dateTime.atTime(LocalTime.MAX);;
			return repository.findByTransactionTimeBetween(start, end);
		}
		else {
			LocalDateTime end = finalDateTime.atTime(LocalTime.MAX);
			return repository.findByTransactionTimeBetween(start, end);
		}
	}
	
	public Transaction findById(Long id) {
		return repository.findById(id).orElseThrow();
	}
	
	public Transaction save(Cashier cashier, BigDecimal transactionValue, String source) {
		Transaction obj = new Transaction();
		obj.setCashier(cashier);
		obj.setCurrentCashier(cashier.getCurrentCashier());
		obj.setSource(source);
		obj.setTransactionTime(LocalDateTime.now());
		obj.setTransactionValue(transactionValue);
		return repository.save(obj);
	}
	
	public void delete(Transaction obj) {
		repository.deleteById(obj.getId());
	}
}
