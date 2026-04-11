package com.reis.managementControl.Entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_cashier")
public class Cashier implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	private BigDecimal currentCashier;
	private BigDecimal expectedTransfer;
	private BigDecimal currentCashierPlusTransfer;
	
	public Cashier() {
	}

	public Cashier(Long id, BigDecimal currentCashier, BigDecimal expectedTransfer, BigDecimal currentCashierPlusTransfer) {
		super();
		this.id = id;
		this.currentCashier = currentCashier;
		this.expectedTransfer = expectedTransfer;
		this.currentCashierPlusTransfer = currentCashierPlusTransfer;
	}

	public BigDecimal getCurrentCashier() {
		return currentCashier;
	}

	public void setCurrentCashier(BigDecimal currentCashier) {
		this.currentCashier = currentCashier;
	}

	public BigDecimal getExpectedTransfer() {
		return expectedTransfer;
	}

	public void setExpectedTransfer(BigDecimal expectedTransfer) {
		this.expectedTransfer = expectedTransfer;
	}

	public Long getId() {
		return id;
	}

	public BigDecimal getCurrentCashierPlusTransfer() {
		return currentCashierPlusTransfer;
	}
	
	public void updateTotal() {
		this.currentCashierPlusTransfer = currentCashier.add(expectedTransfer);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cashier other = (Cashier) obj;
		return Objects.equals(id, other.id);
	}
}
