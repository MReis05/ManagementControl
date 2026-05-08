package com.reis.managementControl.Gui.Controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.reis.managementControl.Entities.Location;
import com.reis.managementControl.Entities.Order;
import com.reis.managementControl.Entities.OrderItem;
import com.reis.managementControl.Entities.Product;
import com.reis.managementControl.Entities.Enums.PaymentMethod;
import com.reis.managementControl.Gui.Listerners.DataChangeListener;
import com.reis.managementControl.Gui.Util.Alerts;
import com.reis.managementControl.Gui.Util.Constraints;
import com.reis.managementControl.Gui.Util.ImageManager;
import com.reis.managementControl.Gui.Util.Utils;
import com.reis.managementControl.Services.LocationService;
import com.reis.managementControl.Services.OrderService;
import com.reis.managementControl.Services.Exceptions.ValidationExceptions;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
@Scope("prototype")
public class AddOrderFormController implements Initializable {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private Order order;
	
	private Product product;
	
	private DataChangeListener listener;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private OrderService orderService;
	
	@FXML
	private ComboBox<Location> comboBoxLocations;
	
	@FXML
	private ComboBox<PaymentMethod> comboBoxPaymentMethods;
	
	@FXML
	private DatePicker dpPurchaseDate;
	
	@FXML
	private TextField txtProductName;
	
	@FXML
	private TextField txtUnitValue;
	
	@FXML
	private TextField txtQuantity;
	
	@FXML
	private Button btAddProduct;
	
	@FXML
	private Button btSaveOrder;
	
	@FXML
	private Button btCancel;
	
	@FXML
	private Button btSearch;
	
	@FXML
	private Label labelTotalValue;
	
	@FXML
	private Label labelErrorUnitValue;
	
	@FXML
	private Label labelErrorQuantity;
	
	@FXML
	private Label labelErrorLocation;
	
	@FXML
	private Label labelErrorPaymentMethod;
	
	private ObservableList<Location> obsLocation;
	
	private ObservableList<PaymentMethod> obsPaymentMethod;
	
	@FXML
	private TableView<OrderItem> tableViewOrderItem;
	
	@FXML
	private TableColumn<OrderItem, String> tableColumnName;
	
	@FXML
	private TableColumn<OrderItem, String> tableColumnCategory;
	
	@FXML
	private TableColumn<OrderItem, BigDecimal> tableColumnQuantity;
	
	@FXML
	private TableColumn<OrderItem, BigDecimal> tableColumnUnitValue;
	
	@FXML
	private TableColumn<OrderItem, BigDecimal> tableColumnTotalValue;
	
	@FXML
	private TableColumn<OrderItem, OrderItem> tableColumnRemoveButton;
	
	private ObservableList<OrderItem> obsOrderItem;
	
	private BigDecimal totalValue = BigDecimal.ZERO;
	
	@FXML
	public void onBtSearchAction(ActionEvent event) {
		Stage stage = Utils.currentStage(event);
		dialogForm("/fxml/SearchProductDialogForm.fxml", stage);
	}
	
	@FXML
	public void onBtAddProductAction() {
		try {
			if(this.product == null) {
				Alerts.showAlert("Aviso", null, "Por favor, selecione um produto na lupa primeiro!", AlertType.WARNING);
				return;
			}
			
			Product product = new Product();
			OrderItem orderItem = new OrderItem();
			orderItem = getFormData(product, orderItem);
			totalValue = totalValue.add(orderItem.getTotalValue());
			this.order.getItems().add(orderItem);
			updateTableView();
			txtProductName.clear();
			txtQuantity.clear();
			txtUnitValue.clear();
			
			product = null;
		}
		catch(ValidationExceptions e) {
			setErrorMessages(e.getErrors());
		}
	}
	
	@FXML
	public void onBtSaveOrderAction(ActionEvent event) {
		try {
			if(this.order.getItems().size() == 0) {
				Alerts.showAlert("Aviso", null, "Adicione pelo menos um item antes de salvar o pedido.", AlertType.WARNING);
				return;
			}
			this.order = getFormOrderData(this.order);
			
			for(OrderItem i : this.order.getItems()) {
				i.setOrder(this.order);
			}
			
			this.order.updateTotal();
			this.order = orderService.save(order);
			if(order.getPaymentMethod() == PaymentMethod.DINHEIRO || order.getPaymentMethod() == PaymentMethod.PIX) {
				notifyDataChangeListeners(order.getTotalValue(), order.getLocation().getName());
			}
			Utils.currentStage(event).close();
		}
		catch(ValidationExceptions e) {
			setErrorMessages(e.getErrors());
		}
		catch(Exception e) {
			Alerts.showAlert("Erro em salvar o pedido", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	private OrderItem getFormData(Product product, OrderItem orderItem) {
		labelErrorQuantity.setText("");
		labelErrorUnitValue.setText("");
		product = this.product;
		
		ValidationExceptions exceptions = new ValidationExceptions("Validation Error");
		
		orderItem.setProduct(product);
		if(txtQuantity.getText() == null || txtQuantity.getText().trim().isEmpty()) {
			exceptions.addError("Quantity", "Field can't be empty");
		}
		else {
			orderItem.setQuantity(new BigDecimal(txtQuantity.getText()));
		}
		if(txtUnitValue.getText() == null || txtUnitValue.getText().trim().isEmpty()) {
			exceptions.addError("Unit Value", "Field can't be empty");
		}
		else {
			orderItem.setUnitValue(new BigDecimal(txtUnitValue.getText()));
		}
		
		if(!exceptions.getErrors().isEmpty()) {
			throw exceptions;
		}
		
		return orderItem;
	}
	
	private Order getFormOrderData(Order order) {
		labelErrorLocation.setText("");
		labelErrorPaymentMethod.setText("");
		
		ValidationExceptions exception = new ValidationExceptions("Validation Error");
		
		if(dpPurchaseDate != null && dpPurchaseDate.getValue() != null) {
			order.setDate(dpPurchaseDate.getValue());
		}
		else {
			order.setDate(LocalDate.now());
		}
		
		if(comboBoxPaymentMethods.getValue() == null) {
			exception.addError("Payment Method", "You must select one Payment Method");
		}
		else {
			order.setPaymentMethod(comboBoxPaymentMethods.getValue());
		}
		
		if(comboBoxLocations.getValue() == null) {
			exception.addError("Location", "You must select one Location");
		}
		else {
			Location locationSelected = comboBoxLocations.getValue();
			if(locationSelected.getId() == null) {
				locationSelected = locationService.save(locationSelected);
			}
			order.setLocation(locationSelected);
		}
		
		if(!exception.getErrors().isEmpty()) {
			throw exception;
		}
		
		return order;
	}
	
	private void notifyDataChangeListeners(BigDecimal totalValue, String source) {
		if(listener != null) {
			listener.updateValues(totalValue, source);
		}
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		this.listener = listener;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}
	
	private void initializeOrderFields() {
		comboBoxLocations.setValue(order.getLocation());
		comboBoxPaymentMethods.setValue(order.getPaymentMethod());
		dpPurchaseDate.setValue(order.getDate());
		if(order.getTotalValue() != null) {
			totalValue = order.getTotalValue();
		}
		updateTableView();
	}
	
	private void updateTableView() {
		obsOrderItem = FXCollections.observableArrayList(this.order.getItems());
		tableViewOrderItem.setItems(obsOrderItem);
		labelTotalValue.setText(totalValue.toString());
		initRemoveButtons();
		
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldDouble(txtUnitValue);
		Constraints.setTextFieldDouble(txtQuantity);
		Constraints.setTextFieldLetters(txtProductName);
		Constraints.setTextFieldMaxLength(txtProductName, 30);
		Utils.formatDatePicker(dpPurchaseDate, "dd/MM/yyyy");
		loadAssociatedObjects();
		initializeTable();
		initializeResources();
	}
	
	private void initializeTable() {
		tableColumnName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProduct().getName()));
		tableColumnCategory.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProduct().getCategory().name()));
		tableColumnQuantity.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getQuantity()));
		Utils.formatTableColumnBigDecimal(tableColumnQuantity, 3);
		tableColumnUnitValue.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getUnitValue()));
		Utils.formatTableColumnBigDecimal(tableColumnUnitValue, 2);
		tableColumnTotalValue.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalValue()));
		Utils.formatTableColumnBigDecimal(tableColumnTotalValue, 2);
	}
	
	private void loadAssociatedObjects() {
		obsPaymentMethod = FXCollections.observableArrayList(PaymentMethod.values());
		obsLocation = FXCollections.observableArrayList(locationService.findAll());
		
		comboBoxPaymentMethods.setItems(obsPaymentMethod);
		comboBoxLocations.setItems(obsLocation);
		Utils.formatComboBoxLocation(comboBoxLocations);
	}
	
	private void initializeResources() {
		ImageView search = new ImageView(ImageManager.getImage("searchIcon"));
		ImageView save = new ImageView(ImageManager.getImage("saveIcon"));
		ImageView plus = new ImageView(ImageManager.getImage("plusIcon"));
		
		search.setFitHeight(16);
		search.setFitWidth(16);
		
		save.setFitHeight(23);
		save.setFitWidth(23);
		
		plus.setFitHeight(23);
		plus.setFitWidth(23);
		
		btSearch.setGraphic(search);
		btSaveOrder.setGraphic(save);
		btAddProduct.setGraphic(plus);
	}
	
	private void removeEntity(OrderItem obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Removendo Item",
				"Tem certeza que deseja apagar o Item?");
		if (result.get() == ButtonType.OK) {
			totalValue = totalValue.subtract(obj.getTotalValue());
			updateTableView();
			obsOrderItem.remove(obj);
			this.order.getItems().remove(obj);
		}
	}
	
	private void initRemoveButtons() {
		tableColumnRemoveButton.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemoveButton.setCellFactory(param -> new TableCell<OrderItem, OrderItem>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(OrderItem obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> keys = errors.keySet();
		
		labelErrorQuantity.setText(keys.contains("Quantity") ? errors.get("Quantity") : "");
		labelErrorUnitValue.setText(keys.contains("Unit Value") ? errors.get("Unit Value") : "");
		labelErrorLocation.setText(keys.contains("Location") ? errors.get("Location") : "");
		labelErrorPaymentMethod.setText(keys.contains("Payment Method") ? errors.get("Payment Method") : "");
	}
	
	public void dialogForm(String absoluteView, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteView));
			loader.setControllerFactory(applicationContext::getBean);
			VBox vbox = loader.load();
			Product obj = new Product();
			SearchProductFormController controller = loader.getController();
			controller.setProduct(obj);
			controller.subscribeAddProductListener((Product product) ->{
				this.product = product;
				txtProductName.setText(product.getName());
			});
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados do produto");
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

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
		initializeOrderFields();
	}
}
