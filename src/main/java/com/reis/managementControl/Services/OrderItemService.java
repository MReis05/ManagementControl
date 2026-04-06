package com.reis.managementControl.Services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.managementControl.Entities.OrderItem;
import com.reis.managementControl.Entities.DTO.OrderItemHistoryDTO;
import com.reis.managementControl.Entities.PK.OrderItemPK;
import com.reis.managementControl.Repositories.OrderItemRepository;

@Service
public class OrderItemService {

	@Autowired
	private OrderItemRepository repository;
	
	public List<OrderItem> findAll(){
		return repository.findAll();
	}
	
	public List<OrderItemHistoryDTO> findByDate(LocalDate date, LocalDate finalDate){
		if(finalDate == null) {
			return repository.findByDate(date);
		}
		else {
			return repository.findByDateBetween(date, finalDate);
		}
	}
	
	public OrderItem findById(OrderItemPK id) {
		OrderItem orderItem = repository.findById(id).orElseThrow();
		return orderItem;
	}
	
	public OrderItem save (OrderItem orderItem) {
		return repository.save(orderItem);
	}
	
	public void update(OrderItem orderItem) {
		repository.save(orderItem);
	}
	
	public void delete(OrderItem orderItem) {
		repository.delete(orderItem);
	}
}
