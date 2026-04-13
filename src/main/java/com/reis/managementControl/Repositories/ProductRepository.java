package com.reis.managementControl.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.managementControl.Entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByNameContainingIgnoreCase(String name);
	boolean existsByNameIgnoreCase(String name);
}
