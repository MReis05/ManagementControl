package com.reis.managementControl.Gui.Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

@Component
public class DailyTotalViewController implements Initializable {

	@FXML
	private Label labelTotalValue;
	
	@FXML
	private Label labelCashTotalValue;
	
	@FXML
	private Label labelCardTotlValue;
	
	@FXML
	private Label labelPixTotalValue;
	
	@FXML
	private DatePicker datePicker;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
