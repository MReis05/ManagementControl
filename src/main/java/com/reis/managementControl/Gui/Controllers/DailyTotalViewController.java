package com.reis.managementControl.Gui.Controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reis.managementControl.Entities.DTO.DailyTotalDTO;
import com.reis.managementControl.Gui.Util.Utils;
import com.reis.managementControl.Services.OrderService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

@Component
public class DailyTotalViewController implements Initializable {

	@Autowired
	private OrderService service;
	
	@FXML
	private Label labelTotalValue;
	
	@FXML
	private Label labelCashTotalValue;
	
	@FXML
	private Label labelCardTotlValue;
	
	@FXML
	private Label labelPixTotalValue;
	
	@FXML
	private DatePicker dpDate;
	
	@FXML
	private DatePicker dpFinalDate;
	
	@FXML
	private Button btSearch;
	
	@FXML
	private Button btClear;
	
	@FXML
	private void onBtSearchAction() {
		LocalDate startDate = dpDate.getValue();
		LocalDate finalDate = dpFinalDate.getValue();
		if(dpFinalDate.getEditor().getText().trim().isEmpty()) {
			finalDate = null;
			dpFinalDate.setValue(null);
		}
		
		DailyTotalDTO dto = service.sumTotalValueByDate(startDate, finalDate);
		
		labelTotalValue.setText(dto.getTotalValue() != null ? String.format("%.2f",dto.getTotalValue()) : "R$ 0.00");
		labelCashTotalValue.setText(dto.getCashTotalValue() != null ? String.format("%.2f",dto.getCashTotalValue()) : "R$ 0.00");
		labelCardTotlValue.setText(dto.getCardTotalValue() != null ? String.format("%.2f",dto.getCardTotalValue()) : "R$ 0.00");
		labelPixTotalValue.setText(dto.getPixTotalValue() != null ? String.format("%.2f", dto.getPixTotalValue()) : "R$ 0.00");
	}
	
	@FXML
	private void onBtClearAction() {
		labelTotalValue.setText("");
		labelCashTotalValue.setText("");
		labelCardTotlValue.setText("");
		labelPixTotalValue.setText("");
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Utils.formatDatePicker(dpDate, "dd/MM/yyyy");
		Utils.formatDatePicker(dpFinalDate, "dd/MM/yyyy");
	}

}
