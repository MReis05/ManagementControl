package com.reis.managementControl.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.managementControl.Entities.OrderItem;
import com.reis.managementControl.Entities.PK.OrderItemPK;
import com.reis.managementControl.Repositories.OrderItemRepository;

@Service
public class OrderItemService {

	@Autowired
	private OrderItemRepository repository;
	
	public List<OrderItem> findAll(){
		return repository.findAll();
	}
	
	public OrderItem findById(OrderItemPK id) {
		OrderItem orderItem = repository.findById(id).orElseThrow();
		return orderItem;
	}
	
	public void save (OrderItem orderItem) {
		repository.save(orderItem);
	}
	
	public void update(OrderItem orderItem) {
		repository.save(orderItem);
	}
	
	public void delete(OrderItem orderItem) {
		repository.delete(orderItem);
	}
}
