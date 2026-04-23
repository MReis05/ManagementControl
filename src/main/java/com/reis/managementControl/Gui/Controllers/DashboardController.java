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

import com.reis.managementControl.Entities.Cashier;
import com.reis.managementControl.Entities.Order;
import com.reis.managementControl.Entities.Transaction;
import com.reis.managementControl.Entities.DTO.OrderItemHistoryDTO;
import com.reis.managementControl.Entities.DTO.TotalPerLocationDTO;
import com.reis.managementControl.Gui.Util.ImageManager;
import com.reis.managementControl.Gui.Util.Utils;
import com.reis.managementControl.Services.CashierService;
import com.reis.managementControl.Services.OrderItemService;
import com.reis.managementControl.Services.OrderService;
import com.reis.managementControl.Services.TransactionService;

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
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class DashboardController implements Initializable {

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private CashierService cashierService;
	
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
	private Button btOrderManagement;
	
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
	
	@FXML
	public void onBtOrderManagementAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		dialogOrderManagement("/fxml/OrderManagementView.fxml", parentStage);
	}
	
	public void dialogForm(String absoluteView, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteView));
			loader.setControllerFactory(applicationContext::getBean);
			VBox vbox = loader.load();
			
			AddOrderFormController controller = loader.getController();
			Order order = new Order();
			controller.setOrder(order);
			controller.subscribeDataChangeListener((BigDecimal totalValue, String source) ->{
				Cashier cashier = cashierService.getCompanyCashier();
				Transaction transaction = transactionService.save(cashier, totalValue.negate(), source);
				BigDecimal currentValueMinusOrder = cashier.getCurrentCashier().subtract(totalValue);
				cashier.setCurrentCashier(currentValueMinusOrder);
				transaction.setNewValue(cashier.getCurrentCashier());
				cashier.getTransactions().add(transaction);
				cashier.updateTotal();
				cashier = cashierService.save(cashier);
				updateNodes(cashier);
			});
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados da compra");
			dialogStage.setScene(new Scene(vbox));
			dialogStage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
			dialogStage.getIcons().add(ImageManager.getImage("programIcon"));
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
				Cashier cashier = cashierService.getCompanyCashier();
				if(current.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal transactionValue = current.subtract(cashier.getCurrentCashier());
					Transaction transaction = transactionService.save(cashier, transactionValue, "Usuário");
					cashier.setCurrentCashier(current);
					transaction.setNewValue(cashier.getCurrentCashier());
					cashier.getTransactions().add(transaction);
				}
				if(expected.compareTo(BigDecimal.ZERO) > 0) {
					cashier.setExpectedTransfer(expected);
				}
				cashier.updateTotal();
				cashier = cashierService.save(cashier);
				updateNodes(cashier);
			});
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os valores para atualizar");
			dialogStage.setScene(new Scene(vbox));
			dialogStage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
			dialogStage.getIcons().add(ImageManager.getImage("programIcon"));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void dialogOrderManagement(String absoluteView, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteView));
			loader.setControllerFactory(applicationContext::getBean);
			VBox vbox = loader.load();
			
			OrderManagementViewController controller = loader.getController();
			controller.subscribeUpdateValuesListener((BigDecimal totalValue, String source) ->{
				Cashier cashier = cashierService.getCompanyCashier();
				Transaction transaction = transactionService.save(cashier, totalValue.negate(), source);
				BigDecimal current = cashier.getCurrentCashier();
				cashier.setCurrentCashier(current.subtract(totalValue));
				transaction.setNewValue(cashier.getCurrentCashier());
				cashier.getTransactions().add(transaction);
				cashier.updateTotal();
				cashier = cashierService.save(cashier);
				updateNodes(cashier);
			});
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Pesquise o pedido para atualizar ou excluir");
			dialogStage.setScene(new Scene(vbox));
			dialogStage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
			dialogStage.getIcons().add(ImageManager.getImage("programIcon"));
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
	
	private void updateNodes(Cashier cashier) {
		currentCashier.setText("R$ " + cashier.getCurrentCashier().toString());
		expectedTransfer.setText("R$ " + cashier.getExpectedTransfer().toString());
		currentCashierPlusTransfer.setText("R$ " + cashier.getCurrentCashierPlusTransfer().toString());
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
		initializeResources();
		updateNodes(cashierService.getCompanyCashier());
	}
	
	private void initalizeTables() {
		tableColumnItemName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
		tableColumnItemValue.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalValue()));
		Utils.formatTableColumnBigDecimal(tableColumnItemValue, 2);
		
		tableColumnLocationName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getLocationName()));
		tableColumnLocationValue.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalValue()));
		Utils.formatTableColumnBigDecimal(tableColumnLocationValue, 2);
	}
	
	private void initializeResources() {
		ImageView plusSign =  new ImageView(ImageManager.getImage("plusIcon"));
		ImageView moneySign = new ImageView(ImageManager.getImage("moneyIcon"));
		ImageView editSign = new ImageView(ImageManager.getImage("editIcon"));
		
		plusSign.setFitHeight(23);
		plusSign.setFitWidth(23);
		
		moneySign.setFitHeight(23);
		moneySign.setFitWidth(23);
		
		editSign.setFitHeight(23);
		editSign.setFitWidth(23);
		
		btNewOrder.setGraphic(plusSign);
		btUpdateValues.setGraphic(moneySign);
		btOrderManagement.setGraphic(editSign);
	}

}
