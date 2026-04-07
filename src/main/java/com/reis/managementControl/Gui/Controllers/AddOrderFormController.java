package com.reis.managementControl.Gui.Controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.reis.managementControl.Entities.Location;
import com.reis.managementControl.Entities.Order;
import com.reis.managementControl.Entities.OrderItem;
import com.reis.managementControl.Entities.Product;
import com.reis.managementControl.Entities.Enums.Category;
import com.reis.managementControl.Entities.Enums.PaymentMethod;
import com.reis.managementControl.Gui.Listerners.DataChangeListener;
import com.reis.managementControl.Gui.Util.Alerts;
import com.reis.managementControl.Gui.Util.Constraints;
import com.reis.managementControl.Gui.Util.Utils;
import com.reis.managementControl.Services.LocationService;
import com.reis.managementControl.Services.OrderService;
import com.reis.managementControl.Services.ProductService;

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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class AddOrderFormController implements Initializable {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private Order order;
	
	private Product product;
	
	private DataChangeListener listener;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private ProductService productService;
	
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
	private ComboBox<Category> comboBoxCategory;
	
	private ObservableList<Location> obsLocation;
	
	private ObservableList<PaymentMethod> obsPaymentMethod;
	
	//private ObservableList<Category> obsCategory;
	
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
	
	private List<OrderItem> orderItemList = new ArrayList<>();
	
	
	@FXML
	public void onBtSearchAction(ActionEvent event) {
		Stage stage = Utils.currentStage(event);
		dialogForm("/fxml/SearchProductDialogForm.fxml", stage);
	}
	
	@FXML
	public void onBtAddProductAction() {
		if(this.product == null) {
			Alerts.showAlert("Aviso", null, "Por favor, selecione um produto na lupa primeiro!", AlertType.WARNING);
			return;
		}
		
		Product product = new Product();
		OrderItem orderItem = new OrderItem();
		orderItem = getFormData(product, orderItem);
		orderItemList.add(orderItem);
		updateTableView();
		txtProductName.clear();
		txtQuantity.clear();
		txtUnitValue.clear();
		
		product = null;
	}
	
	@FXML
	public void onBtSaveOrderAction(ActionEvent event) {
		if(orderItemList.size() == 0) {
			Alerts.showAlert("Aviso", null, "Adicione pelo menos um item antes de salvar o pedido.", AlertType.WARNING);
			return;
		}
		this.order = getFormOrderData(this.order);
		this.order = orderService.save(this.order);
		
		for(OrderItem i : orderItemList) {
			Product p = i.getProduct();
			productService.save(p);
			i.setOrder(this.order);
		}
		
		this.order.getItems().addAll(orderItemList);
		this.order.updateTotal();
		this.order = orderService.save(order);
		orderItemList.clear();
		notifyDataChangeListeners(order.getTotalValue());
		Utils.currentStage(event).close();
	}
	
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	private OrderItem getFormData(Product product, OrderItem orderItem) {
		product = this.product;
		
		orderItem.setProduct(product);
		orderItem.setQuantity(new BigDecimal (txtQuantity.getText()));
		orderItem.setUnitValue(new BigDecimal(txtUnitValue.getText()));
		return orderItem;
	}
	
	private Order getFormOrderData(Order order) {
		if(dpPurchaseDate != null && dpPurchaseDate.getValue() != null) {
			order.setDate(dpPurchaseDate.getValue());
		}
		else {
			order.setDate(LocalDate.now());
		}
		
		order.setPaymentMethod(comboBoxPaymentMethods.getValue());
		Location locationSelected = comboBoxLocations.getValue();
		if(locationSelected.getId() == null) {
			locationSelected = locationService.save(locationSelected);
		}
		order.setLocation(locationSelected);
		return order;
	}
	
	private void notifyDataChangeListeners(BigDecimal totalValue) {
		if(listener != null) {
			listener.updateValues(totalValue);
		}
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		this.listener = listener;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
		
	}
	
	private void updateTableView() {
		obsOrderItem = FXCollections.observableArrayList(orderItemList);
		tableViewOrderItem.setItems(obsOrderItem);
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
	
	private void removeEntity(OrderItem obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Removendo Item",
				"Tem certeza que deseja apagar o Item?");
		if (result.get() == ButtonType.OK) {
			obsOrderItem.remove(obj);
			orderItemList.remove(obj);
		}
	}
	
	private void initRemoveButtons() {
		tableColumnRemoveButton.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemoveButton.setCellFactory(param -> new TableCell<OrderItem, OrderItem>() {
			private final Button button = new Button("remove");

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
	
	public void dialogForm(String absoluteView, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteView));
			loader.setControllerFactory(applicationContext::getBean);
			VBox vbox = loader.load();
			
			SearchProductFormController controller = loader.getController();
			controller.subscribeAddProductListener((Product product) ->{
				this.product = product;
				txtProductName.setText(product.getName());
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

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
}
