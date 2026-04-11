package com.reis.managementControl.Services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.managementControl.Entities.Cashier;
import com.reis.managementControl.Repositories.CashierRepository;

@Service
public class CashierService {

	@Autowired
	private CashierRepository repository;
	
	public Cashier getCompanyCashier() {
		return repository.findById(1L).orElseGet(() ->{
			Cashier cashier = new Cashier(1L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
			return repository.save(cashier);
		});
	}
	
	public Cashier save(Cashier cashier) {
		return repository.save(cashier);
	}
}
