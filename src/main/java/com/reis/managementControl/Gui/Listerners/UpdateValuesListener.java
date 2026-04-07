package com.reis.managementControl.Gui.Listerners;

import java.math.BigDecimal;

public interface UpdateValuesListener {

	void updateValues(BigDecimal currentCashier, BigDecimal expectedTransfer);
}
