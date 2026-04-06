package com.reis.managementControl.Entities.DTO;

import java.math.BigDecimal;

import com.reis.managementControl.Entities.Enums.Category;

public class OrderItemHistoryDTO {

	private String name;
	private BigDecimal totalValue;
	private Category category;
	
	public OrderItemHistoryDTO() {
	}

	public OrderItemHistoryDTO(String name, BigDecimal totalValue, Category category) {
		super();
		this.name = name;
		this.totalValue = totalValue;
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}
