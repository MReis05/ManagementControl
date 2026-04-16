package com.reis.managementControl.Gui.Controllers;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reis.managementControl.Entities.Transaction;
import com.reis.managementControl.Gui.Util.ImageManager;
import com.reis.managementControl.Gui.Util.Utils;
import com.reis.managementControl.Services.TransactionService;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

@Component
public class TransactionsViewController implements Initializable {

	@Autowired
	private TransactionService service;
	
	@FXML
	private DatePicker dpDate;
	
	@FXML
	private DatePicker dpFinalDate;
	
	@FXML
	private Button btSearch;
	
	@FXML
	private Button btClear;
	
	@FXML
	private TableView<Transaction> tableViewTransaction;
	
	@FXML
	private TableColumn<Transaction, Long> tableColumnId;
	
	@FXML
	private TableColumn<Transaction, String> tableColumnSource;
	
	@FXML
	private TableColumn<Transaction, BigDecimal> tableColumnCurrentCashier;
	
	@FXML
	private TableColumn<Transaction, BigDecimal> tableColumnTransactionValue;
	
	@FXML
	private TableColumn<Transaction, BigDecimal> tableColumnNewValue;
	
	@FXML
	private TableColumn<Transaction, LocalDateTime> tableColumnDateTime;
	
	private ObservableList<Transaction> obsTransaction;
	
	@FXML
	public void onBtSearchAction() {
		LocalDate startDate = dpDate.getValue();
		LocalDate finalDate = dpFinalDate.getValue();
		
		if(dpFinalDate.getEditor().getText().trim().isEmpty()) {
			finalDate = null;
			dpFinalDate.setValue(null);
		}
		
		obsTransaction = FXCollections.observableArrayList(service.findByDate(startDate, finalDate));
		
		updateTableView();
	}
	
	@FXML
	public void onBtClearAction() {
		obsTransaction.clear();
		updateTableView();
	}
	
	private void updateTableView() {
		tableViewTransaction.setItems(obsTransaction);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Utils.formatDatePicker(dpDate, "dd/MM/yyyy");
		Utils.formatDatePicker(dpFinalDate, "dd/MM/yyyy");
		initializeTable();
		initalizeResources();
	}
	
	private void initializeTable() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnSource.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getSource()));
		tableColumnCurrentCashier.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCurrentCashier()));
		Utils.formatTableColumnBigDecimal(tableColumnCurrentCashier, 2);
		tableColumnTransactionValue.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTransactionValue()));
		Utils.formatTableColumnBigDecimal(tableColumnTransactionValue, 2);
		tableColumnNewValue.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getNewValue()));
		Utils.formatTableColumnBigDecimal(tableColumnNewValue, 2);
		tableColumnDateTime.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTransactionTime()));
		Utils.formatTableColumnDateTime(tableColumnDateTime, "dd/MM/yyyy HH:mm:ss");
	}
	
	private void initalizeResources() {
		ImageView search = new ImageView(ImageManager.getImage("searchIcon"));
		
		search.setFitHeight(23);
		search.setFitWidth(23);
		
		btSearch.setGraphic(search);
	}
}
