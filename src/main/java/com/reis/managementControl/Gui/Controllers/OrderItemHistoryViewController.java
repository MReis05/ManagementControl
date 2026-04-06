package com.reis.managementControl.Gui.Controllers;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reis.managementControl.Entities.DTO.OrderItemHistoryDTO;
import com.reis.managementControl.Entities.Enums.Category;
import com.reis.managementControl.Gui.Util.Utils;
import com.reis.managementControl.Services.OrderItemService;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

@Component
public class OrderItemHistoryViewController implements Initializable {

	@Autowired
	private OrderItemService service;
	
	@FXML
	private DatePicker dpDate;
	
	@FXML
	private DatePicker dpFinalDate;
	
	@FXML
	private Button btSearch;
	
	@FXML
	private Button btClear;
	
	@FXML
	private TableView<OrderItemHistoryDTO> tableViewOrderItemHistory;
	
	@FXML
	private TableColumn<OrderItemHistoryDTO, String> tableColumnProductName;
	
	@FXML
	private TableColumn<OrderItemHistoryDTO, BigDecimal> tableColumnTotalValue;
	
	@FXML
	private TableColumn<OrderItemHistoryDTO, Category> tableColumnCategory;
	
	private ObservableList<OrderItemHistoryDTO> obsOrderItemHistory;
	
	@FXML
	private void onBtSearchAction() {
		LocalDate startDate = dpDate.getValue();
		LocalDate finalDate = dpFinalDate.getValue();
		
		if(dpFinalDate.getEditor().getText().trim().isEmpty()) {
			finalDate = null;
			dpFinalDate.setValue(null);
		}
		
		obsOrderItemHistory = FXCollections.observableArrayList(service.findByDate(startDate, finalDate));
		
		updateTableView();
		
	}
	
	@FXML
	private void onBtClearAction() {
		obsOrderItemHistory.clear();
		updateTableView();
	}
	
	private void updateTableView() {
		tableViewOrderItemHistory.setItems(obsOrderItemHistory);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Utils.formatDatePicker(dpDate, "dd/MM/yyyy");
		Utils.formatDatePicker(dpFinalDate, "dd/MM/yyyy");
		initializeTable();
	}
	
	private void initializeTable() {
		tableColumnProductName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
		tableColumnTotalValue.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalValue()));
		Utils.formatTableColumnBigDecimal(tableColumnTotalValue, 2);
		tableColumnCategory.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCategory()));
	}

}
