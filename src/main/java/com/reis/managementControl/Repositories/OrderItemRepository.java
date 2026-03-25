package com.reis.managementControl.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.managementControl.Entities.OrderItem;
import com.reis.managementControl.Entities.PK.OrderItemPK;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {

}
