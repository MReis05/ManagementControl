package com.reis.managementControl.Gui.Controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.reis.managementControl.Entities.Order;
import com.reis.managementControl.Gui.Listerners.DataChangeListener;
import com.reis.managementControl.Gui.Util.Alerts;
import com.reis.managementControl.Gui.Util.ImageManager;
import com.reis.managementControl.Gui.Util.Utils;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class OrderManagementViewController implements Initializable {

	@Autowired
	private ApplicationContext applicationContext;
	
	private DataChangeListener listener;
	
	@Autowired
	private OrderService service;
	
	@FXML
	private DatePicker dpDate;
	
	@FXML
	private DatePicker dpFinalDate;
	
	@FXML
	private Button btSearch;
	
	@FXML
	private Button btClear;
	
	@FXML
	private TableView<Order> tableViewOrder;
	
	@FXML
	private TableColumn<Order, Long> tableColumnId;
	
	@FXML
	private TableColumn<Order, BigDecimal> tableColumnTotalValue;
	
	@FXML
	private TableColumn<Order, String> tableColumnLocationName;
	
	@FXML
	private TableColumn<Order, LocalDate> tableColumnDate;
	
	@FXML
	private TableColumn<Order, Order> tableColumnUpdateButton;
	
	@FXML
	private TableColumn<Order, Order> tableColumnDeleteButton;
	
	private ObservableList<Order> obsOrder;
	
	@FXML
	public void onBtSearchAction() {
		LocalDate startDate = dpDate.getValue();
		LocalDate finalDate = dpFinalDate.getValue();
		if(dpFinalDate.getEditor().getText().trim().isEmpty()) {
			finalDate = null;
			dpFinalDate.setValue(null);
		}
		
		obsOrder = FXCollections.observableArrayList(service.findOrderByDate(startDate, finalDate));
		updateTableView();
	}
	
	@FXML
	public void onBtClearAction() {
		obsOrder.clear();
		updateTableView();
	}
	
	private void notifyUpdateValuesListeners(BigDecimal totalValue, String source) {
		if(listener != null) {
			listener.updateValues(totalValue, source);
		}
	}
	
	public void subscribeUpdateValuesListener(DataChangeListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Utils.formatDatePicker(dpDate, "dd/MM/yyyy");
		Utils.formatDatePicker(dpFinalDate, "dd/MM/yyyy");;
		initializeTable();
		initializeResources();
	}
	
	private void updateTableView() {
		tableViewOrder.setItems(obsOrder);
		initDeleteButtons();
		initUpdateButtons();
	}
	
	private void initializeTable() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnTotalValue.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalValue()));
		Utils.formatTableColumnBigDecimal(tableColumnTotalValue, 2);
		tableColumnLocationName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getLocation().getName()));
		tableColumnDate.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDate()));
		Utils.formatTableColumnDate(tableColumnDate, "dd/MM/yyyy");
	}
	
	private void initializeResources() {
		ImageView search = new ImageView(ImageManager.getImage("searchIcon"));
		
		search.setFitHeight(16);
		search.setFitWidth(16);
	
		btSearch.setGraphic(search);
	}
	
	private void deleteOrder(Order obj, ActionEvent event){
		try {
			Optional<ButtonType> result = Alerts.showConfirmation("Apagando Pedido",
					"Tem certeza que deseja apagar o Pedido?");
			if(result.get() == ButtonType.OK) {
				service.delete(obj);
				notifyUpdateValuesListeners(obj.getTotalValue().negate(), "Deleção de Pedido");
				Utils.currentStage(event).close();
			}
		}
		catch(DataIntegrityViolationException e) {
			Alerts.showAlert("", "Erro em deletar pedido", null, Alert.AlertType.ERROR);
		}
	}
	
	private void updateOrder(Order obj, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddOrderDialogForm.fxml"));
			loader.setControllerFactory(applicationContext::getBean);
			VBox vbox = loader.load();
			
			BigDecimal currentValue = obj.getTotalValue();
			
			AddOrderFormController controller = loader.getController();
			controller.setOrder(obj);
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados da compra");
			dialogStage.setScene(new Scene(vbox));
			dialogStage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
			dialogStage.getIcons().add(ImageManager.getImage("programIcon"));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
			BigDecimal newValue = obj.getTotalValue();
			BigDecimal valueDifference = newValue.subtract(currentValue);
			if(valueDifference.compareTo(BigDecimal.ZERO) > 0) {
				notifyUpdateValuesListeners(valueDifference, obj.getLocation().getName());
			}
			else {
				notifyUpdateValuesListeners(valueDifference, "Deleção de Item");
			}
			parentStage.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initDeleteButtons() {
		tableColumnDeleteButton.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDeleteButton.setCellFactory(param -> new TableCell<Order, Order>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Order obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> deleteOrder(obj, event));
			}
		});
	}
	
	private void initUpdateButtons() {
		tableColumnUpdateButton.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnUpdateButton.setCellFactory(param -> new TableCell<Order, Order>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Order obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> updateOrder(obj, Utils.currentStage(event)));
			}
		});
	}
}
