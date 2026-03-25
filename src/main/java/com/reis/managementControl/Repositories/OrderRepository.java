package com.reis.managementControl.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.managementControl.Entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
