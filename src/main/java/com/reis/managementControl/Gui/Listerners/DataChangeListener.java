package com.reis.managementControl.Gui.Listerners;

import java.math.BigDecimal;

public interface DataChangeListener {

	void updateValues(BigDecimal totalValue, String source);
}
