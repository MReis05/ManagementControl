package com.reis.managementControl.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.managementControl.Entities.Product;
import com.reis.managementControl.Repositories.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	public List<Product> findAll(){
		return repository.findAll();
	}
	
	public List<Product> findByName(String name){
		return repository.findByName(name);
	}
	
	public Product findById(Long id) {
		Product product = repository.findById(id).orElseThrow();
		return product;
	}
	
	public void save (Product product) {
		repository.save(product);
	}
	
	public void update(Product product) {
		repository.save(product);
	}
	
	public void delete(Product product) {
		repository.deleteById(product.getId());
	}
}
