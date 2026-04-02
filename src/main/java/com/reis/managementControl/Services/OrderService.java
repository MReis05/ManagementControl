package com.reis.managementControl.Services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.managementControl.Entities.Order;
import com.reis.managementControl.Entities.DTO.DailyTotalDTO;
import com.reis.managementControl.Entities.DTO.TotalPerLocationDTO;
import com.reis.managementControl.Entities.Enums.PaymentMethod;
import com.reis.managementControl.Repositories.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;
	
	public List<Order> findAll(){
		return repository.findAll();
	}
	
	public DailyTotalDTO sumTotalValueByDate(LocalDate date, LocalDate finalDate){
		DailyTotalDTO dto = new DailyTotalDTO();
		if(finalDate == null) {
			dto.setTotalValue(repository.sumTotalValueByDate(date));
			dto.setCashTotalValue(repository.sumTotalValueByDateAndPaymentMethod(date, PaymentMethod.DINHEIRO));
			dto.setCardTotalValue(repository.sumTotalValueByDateAndPaymentMethod(date, PaymentMethod.CARTAO));
			dto.setPixTotalValue(repository.sumTotalValueByDateAndPaymentMethod(date, PaymentMethod.PIX));
			return dto;
		}
		else {
			dto.setTotalValue(repository.sumTotalValueByDateBetween(date, finalDate));
			dto.setCashTotalValue(repository.sumTotalValueByDateBetweenAndPaymentMethod(date, finalDate, PaymentMethod.DINHEIRO));
			dto.setCardTotalValue(repository.sumTotalValueByDateBetweenAndPaymentMethod(date, finalDate, PaymentMethod.CARTAO));
			dto.setPixTotalValue(repository.sumTotalValueByDateBetweenAndPaymentMethod(date, finalDate, PaymentMethod.PIX));
			return dto;
		}
	}
	
	public List<TotalPerLocationDTO> findByDate(LocalDate date, LocalDate finalDate){
			List<TotalPerLocationDTO> list = new ArrayList<>();
		if(finalDate == null) {
			list.addAll(repository.findByDate(date));
			return list;
		}
		else {
			list.addAll(repository.findByDateBetween(date, finalDate));
			return list;
		}
	}
	
	public Order findById(Long id) {
		Order order = repository.findById(id).orElseThrow();
		return order;
	}
	
	public Order save (Order order) {
		return repository.save(order);
	}
	
	public void update(Order order) {
		repository.save(order);
	}
	
	public void delete(Order order) {
		repository.deleteById(order.getId());
	}
}
