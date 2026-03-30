package com.reis.managementControl.Gui.Controllers;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.reis.managementControl.Entities.Location;
import com.reis.managementControl.Entities.OrderItem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

@Component
public class DashboardController implements Initializable {

	@FXML
	private Button btIncreaseTransfer;
	
	@FXML
	private Button btNewOrder;
	
	@FXML
	private Label currentCashier;
	
	@FXML
	private Label expectedTransfer;
	
	@FXML
	private Label currentCashierPlusTransfer;
	
	@FXML
	private TableView<OrderItem> tableViewRankingItems;
	
	@FXML
	private TableColumn<OrderItem, String> tableColumnItemName;
	
	@FXML
	private TableColumn<OrderItem, BigDecimal> tableColumnItemValue;
	
	@FXML
	private TableView<Location> tableViewRankingLocation;
	
	@FXML
	private TableColumn<Location, String> tableColumnLocationName;
	

	@FXML
	private TableColumn<Location, BigDecimal> tableColumnLocationValue;
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
