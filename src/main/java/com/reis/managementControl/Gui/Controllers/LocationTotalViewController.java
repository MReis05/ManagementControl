package com.reis.managementControl.Gui.Controllers;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reis.managementControl.Entities.DTO.TotalPerLocationDTO;
import com.reis.managementControl.Gui.Util.ImageManager;
import com.reis.managementControl.Gui.Util.Utils;
import com.reis.managementControl.Services.OrderService;

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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

@Component
public class LocationTotalViewController implements Initializable {

	@Autowired
	private OrderService service;
	
	@FXML
	private DatePicker dpDate;
	
	@FXML
	private DatePicker dpFinalDate;
	
	@FXML
	private TextField txtProductNames;
	
	@FXML
	private Button btSearch;
	
	@FXML
	private Button btClear;
	
	@FXML
	private TableView<TotalPerLocationDTO> tableViewLocationsTotalValue;
	
	@FXML
	private TableColumn<TotalPerLocationDTO, String> tableColumnLocations;
	
	@FXML
	private TableColumn<TotalPerLocationDTO, BigDecimal> tableColumnTotalValues;
	
	private ObservableList<TotalPerLocationDTO> obsLocationTotalValue;
	
	@FXML
	private void onBtSearchAction() {
		List<String> names = new ArrayList<>();
		if(txtProductNames.getText() != null && !txtProductNames.getText().trim().isEmpty()) {
			names.addAll(Arrays.asList(txtProductNames.getText().split("//s*,s*//")));
		}
		LocalDate startDate = dpDate.getValue();
		LocalDate finalDate = dpFinalDate.getValue();
		
		if(dpFinalDate.getEditor().getText().trim().isEmpty()) {
			finalDate = null;
			dpFinalDate.setValue(null);
		}
		
		obsLocationTotalValue = FXCollections.observableArrayList(service.findByDate(startDate, finalDate, names));
		
		updateTableView();
	}
	
	@FXML
	private void onBtClearAction() {
		obsLocationTotalValue.clear();
		updateTableView();
		
	}
	
	private void updateTableView() {
		tableViewLocationsTotalValue.setItems(obsLocationTotalValue);
	}
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		InitializeTable();
		initalizeResources();
		Utils.formatDatePicker(dpDate, "dd/MM/yyyy");
		Utils.formatDatePicker(dpFinalDate, "dd/MM/yyyy");
	}
	
	private void InitializeTable() {
		tableColumnLocations.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getLocationName()));
		tableColumnTotalValues.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalValue()));
		Utils.formatTableColumnBigDecimal(tableColumnTotalValues, 2);
	}
	
	private void initalizeResources() {
		ImageView search = new ImageView(ImageManager.getImage("searchIcon"));
		
		search.setFitHeight(23);
		search.setFitWidth(23);
		
		btSearch.setGraphic(search);
	}

}
