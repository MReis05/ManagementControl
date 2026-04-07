package com.reis.managementControl.Gui.Controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.reis.managementControl.Entities.Order;
import com.reis.managementControl.Entities.DTO.OrderItemHistoryDTO;
import com.reis.managementControl.Entities.DTO.TotalPerLocationDTO;
import com.reis.managementControl.Gui.Util.Utils;
import com.reis.managementControl.Services.OrderItemService;
import com.reis.managementControl.Services.OrderService;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class DashboardController implements Initializable {

	@Autowired
	private OrderItemService orderItemService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@FXML
	private Button btUpdateValues;
	
	@FXML
	private Button btNewOrder;
	
	@FXML
	private Label currentCashier;
	
	@FXML
	private Label expectedTransfer;
	
	@FXML
	private Label currentCashierPlusTransfer;
	
	@FXML
	private TableView<OrderItemHistoryDTO> tableViewRankingItems;
	
	@FXML
	private TableColumn<OrderItemHistoryDTO, String> tableColumnItemName;
	
	@FXML
	private TableColumn<OrderItemHistoryDTO, BigDecimal> tableColumnItemValue;
	
	@FXML
	private TableView<TotalPerLocationDTO> tableViewRankingLocation;
	
	@FXML
	private TableColumn<TotalPerLocationDTO, String> tableColumnLocationName;
	
	@FXML
	private TableColumn<TotalPerLocationDTO, BigDecimal> tableColumnLocationValue;
	
	private ObservableList<OrderItemHistoryDTO> obsOrderHistory;
	
	private ObservableList<TotalPerLocationDTO> obsLocation;
	
	
	@FXML
	public void onBtNewOrderAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		dialogForm("/fxml/AddOrderDialogForm.fxml", parentStage);
	}
	
	@FXML
	public void onBtUpdateValuesAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		dialogUpdateValues("/fxml/UpdateValuesFormView.fxml", parentStage);
	}
	
	public void dialogForm(String absoluteView, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteView));
			loader.setControllerFactory(applicationContext::getBean);
			VBox vbox = loader.load();
			
			AddOrderFormController controller = loader.getController();
			Order order = new Order();
			controller.setOrder(order);
			controller.subscribeDataChangeListener((BigDecimal totalValue) ->{
				BigDecimal currentValue = new BigDecimal(currentCashier.getText());
				BigDecimal currentValueMinusOrder = currentValue.subtract(totalValue);
				currentCashier.setText(currentValueMinusOrder.toString());
				updateNodes();
			});
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados da compra");
			dialogStage.setScene(new Scene(vbox));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void dialogUpdateValues(String absoluteView, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteView));
			loader.setControllerFactory(applicationContext::getBean);
			VBox vbox = loader.load();
			
			UpdateValuesController controller = loader.getController();
			controller.subscribeUpdateValuesListener((BigDecimal current, BigDecimal expected) ->{
				if(current.compareTo(BigDecimal.ZERO) > 0) {
					currentCashier.setText(current.toString());
				}
				if(expected.compareTo(BigDecimal.ZERO) > 0) {
					expectedTransfer.setText(expected.toString());
				}
				updateNodes();
			});
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados do produto");
			dialogStage.setScene(new Scene(vbox));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
		
	}
	
	private void updateNodes() {
		BigDecimal currentValue = new BigDecimal(currentCashier.getText());
		BigDecimal expected = new BigDecimal(expectedTransfer.getText());
		currentCashierPlusTransfer.setText(currentValue.add(expected).toString());
		updateTables();
	}
	
	private void updateTables() {
		LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate sunday = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
		
		obsOrderHistory = FXCollections.observableArrayList(orderItemService.findByDateWeeklyRanking(monday, sunday));
		obsLocation = FXCollections.observableArrayList(orderService.findByDateWeeklyRanking(monday, sunday));
		
		tableViewRankingItems.setItems(obsOrderHistory);
		tableViewRankingLocation.setItems(obsLocation);
	}
	
	private void initializeNodes() {
		initalizeTables();
	}
	
	private void initalizeTables() {
		tableColumnItemName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
		tableColumnItemValue.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalValue()));
		Utils.formatTableColumnBigDecimal(tableColumnItemValue, 2);
		
		tableColumnLocationName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getLocationName()));
		tableColumnLocationValue.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalValue()));
		Utils.formatTableColumnBigDecimal(tableColumnLocationValue, 2);
	}

}
