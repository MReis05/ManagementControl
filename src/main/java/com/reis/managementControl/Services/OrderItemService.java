package com.reis.managementControl.Services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.reis.managementControl.Entities.OrderItem;
import com.reis.managementControl.Entities.DTO.OrderItemHistoryDTO;
import com.reis.managementControl.Entities.Enums.Category;
import com.reis.managementControl.Entities.PK.OrderItemPK;
import com.reis.managementControl.Repositories.OrderItemRepository;

@Service
public class OrderItemService {

	@Autowired
	private OrderItemRepository repository;
	
	public List<OrderItem> findAll(){
		return repository.findAll();
	}
	
	public List<OrderItemHistoryDTO> findByDate(LocalDate date, LocalDate finalDate, List<String> names, Category category){
		if(finalDate == null) {
			if(category != null) {
				return repository.findByDateAndCategory(date, category);
			}
			if(names.size() > 0) {
				return repository.findByDateAndNames(date, names);
			}
			else {
				return repository.findByDate(date);
			}
		}
		else {
			if(category != null) {
				return repository.findByDateBetweenAndCategory(date, finalDate, category);
			}
			if(names.size() > 0) {
				return repository.findByDateBetweenAndNames(date, finalDate, names);
			}
			else {
				return repository.findByDateBetween(date, finalDate);
			}
		}
	}
	
	public List<OrderItemHistoryDTO> findByDateWeeklyRanking(LocalDate monday, LocalDate sunday) {
		Pageable pageable = PageRequest.of(0, 5);
		return repository.findByDateBetweenWeekly(monday, sunday, pageable);
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
