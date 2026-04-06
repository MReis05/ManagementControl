package com.reis.managementControl.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.reis.managementControl.Entities.OrderItem;
import com.reis.managementControl.Entities.DTO.OrderItemHistoryDTO;
import com.reis.managementControl.Entities.PK.OrderItemPK;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {

	@Query("SELECT new com.reis.managementControl.Entities.DTO.OrderItemHistoryDTO(o.id.product.name, SUM(o.quantity * o.unitValue), o.id.product.category)"
			+ "FROM OrderItem o WHERE o.id.order.date = :date GROUP By o.id.product.name, o.id.product.category")
	List<OrderItemHistoryDTO> findByDate(LocalDate date);
	
	@Query("SELECT new com.reis.managementControl.Entities.DTO.OrderItemHistoryDTO(o.id.product.name, SUM(o.quantity * o.unitValue), o.id.product.category)"
			+ "FROM OrderItem o WHERE o.id.order.date BETWEEN :date AND :finalDate GROUP By o.id.product.name, o.id.product.category")
	List<OrderItemHistoryDTO> findByDateBetween(LocalDate date, LocalDate finalDate);
}
