package com.reis.managementControl.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.managementControl.Entities.Order;
import com.reis.managementControl.Repositories.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;
	
	public List<Order> findAll(){
		return repository.findAll();
	}
	
	public Order findById(Long id) {
		Order order = repository.findById(id).orElseThrow();
		return order;
	}
	
	public void save (Order order) {
		repository.save(order);
	}
	
	public void update(Order order) {
		repository.save(order);
	}
	
	public void delete(Order order) {
		repository.deleteById(order.getId());
	}
}
