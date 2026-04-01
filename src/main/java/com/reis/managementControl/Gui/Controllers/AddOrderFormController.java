package com.reis.managementControl.Gui.Controllers;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reis.managementControl.Entities.Location;
import com.reis.managementControl.Entities.Order;
import com.reis.managementControl.Entities.OrderItem;
import com.reis.managementControl.Entities.Product;
import com.reis.managementControl.Entities.Enums.Category;
import com.reis.managementControl.Entities.Enums.PaymentMethod;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

@Component
public class AddOrderFormController implements Initializable {
	
	private Order order;
	
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
	private ComboBox<Category> comboBoxCategory;
	
	private ObservableList<Location> obsLocation;
	
	private ObservableList<PaymentMethod> obsPaymentMethod;
	
	private ObservableList<Category> obsCategory;
	
	@FXML
	private TableView<OrderItem> tableViewOrderItem;
	
	@FXML
	private TableColumn<OrderItem, String> tableColumnName;
	
	@FXML
	private TableColumn<OrderItem, String> tableColumnCategory;
	
	@FXML
	private TableColumn<OrderItem, Integer> tableColumnQuantity;
	
	@FXML
	private TableColumn<OrderItem, BigDecimal> tableColumnUnitValue;
	
	@FXML
	private TableColumn<OrderItem, BigDecimal> tableColumnTotalValue;
	
	private ObservableList<OrderItem> obsOrderItem;
	
	private List<OrderItem> orderItemList = new ArrayList<>();
	
	@FXML
	public void onBtAddProductAction() {
			Product product = new Product();
			OrderItem orderItem = new OrderItem();
			orderItem = getFormData(product, orderItem);
			orderItemList.add(orderItem);
			updateTableView();
			txtProductName.clear();
			txtQuantity.clear();
			txtUnitValue.clear();
			comboBoxCategory.getSelectionModel().selectFirst();
	}
	
	@FXML
	public void onBtSaveOrderAction(ActionEvent event) {
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
		Utils.currentStage(event).close();
	}
	
	private void updateTableView() {
		obsOrderItem = FXCollections.observableArrayList(orderItemList);
		tableViewOrderItem.setItems(obsOrderItem);
		
	}

	private OrderItem getFormData(Product product, OrderItem orderItem) {
		product.setName(txtProductName.getText());
		product.setCategory(comboBoxCategory.getValue());
		
		orderItem.setProduct(product);
		orderItem.setQuantity(Utils.tryParseToInt(txtQuantity.getText()));
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
		
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldDouble(txtUnitValue);
		Constraints.setTextFieldInteger(txtQuantity);
		Constraints.setTextFieldLetters(txtProductName);
		Constraints.setTextFieldMaxLength(txtProductName, 30);
		Utils.formatDatePicker(dpPurchaseDate, "dd/MM/yyyy");
		loadAssociatedObjects();
		initializeTable();
	}
	
	private void initializeTable() {
		tableColumnName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProduct().getName()));
		tableColumnCategory.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProduct().getCategory().name()));
		tableColumnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		tableColumnUnitValue.setCellValueFactory(new PropertyValueFactory<>("unitValue"));
		Utils.formatTableColumnBigDecimal(tableColumnUnitValue, 2);
		tableColumnTotalValue.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalValue()));
		Utils.formatTableColumnBigDecimal(tableColumnTotalValue, 2);
	}
	
	private void loadAssociatedObjects() {
		obsCategory = FXCollections.observableArrayList(Category.values());
		obsPaymentMethod = FXCollections.observableArrayList(PaymentMethod.values());
		obsLocation = FXCollections.observableArrayList(locationService.findAll());
		
		comboBoxCategory.setItems(obsCategory);
		comboBoxPaymentMethods.setItems(obsPaymentMethod);
		comboBoxLocations.setItems(obsLocation);
		Utils.formatComboBoxLocation(comboBoxLocations);
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
}
