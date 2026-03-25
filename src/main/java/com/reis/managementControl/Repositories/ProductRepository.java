package com.reis.managementControl.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.managementControl.Entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
