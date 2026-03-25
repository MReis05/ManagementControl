package com.reis.managementControl.Entities;

import java.io.Serializable;
import java.math.BigDecimal;

import com.reis.managementControl.Entities.PK.OrderItemPK;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_order_item")
public class OrderItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private OrderItemPK id = new OrderItemPK();
	
	private Integer quantity;
	private BigDecimal unitValue;
	
	public OrderItem() {
	}

	public OrderItem(Integer quantity, BigDecimal unitValue, Order order, Product product) {
		super();
		id.setOrder(order);
		id.setProduct(product);
		this.quantity = quantity;
		this.unitValue = unitValue;
	}

	public Order getOrder() {
		return id.getOrder();
	}
	
	public void setOrder(Order order) {
		id.setOrder(order);
	}
	
	public Product getProduct() {
		return id.getProduct();
	}
	
	public void setProduct(Product product) {
		id.setProduct(product);
	}
	
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitValue() {
		return unitValue;
	}

	public void setUnitValue(BigDecimal unitValue) {
		this.unitValue = unitValue;
	}

	public BigDecimal getTotalValue() {
		return unitValue.multiply(BigDecimal.valueOf(quantity));
	}
}
