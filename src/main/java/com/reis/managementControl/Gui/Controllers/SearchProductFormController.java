package com.reis.managementControl.Gui.Controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reis.managementControl.Entities.Product;
import com.reis.managementControl.Entities.Enums.Category;
import com.reis.managementControl.Gui.Listerners.AddProductListener;
import com.reis.managementControl.Gui.Util.Alerts;
import com.reis.managementControl.Gui.Util.Utils;
import com.reis.managementControl.Services.ProductService;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

@Component
public class SearchProductFormController implements Initializable {
	
	private AddProductListener listener;
	
	@Autowired
	private ProductService service;
	
	@FXML
	private TextField txtProductName;
	
	@FXML
	private ComboBox<Category> comboBoxCategory;
	
	@FXML
	private Button btSearch;
	
	@FXML
	private Button btSave;
	
	@FXML
	private TableView<Product> tableViewProduct;
	
	@FXML
	private TableColumn<Product, Long> tableColumnId;
	
	@FXML
	private TableColumn<Product, String> tableColumnName;
	
	@FXML
	private TableColumn<Product, Category> tableColumnCategory;
	
	@FXML
	private TableColumn<Product, Product> tableColumnSelect;
	
	private ObservableList<Product> obsProduct;
	
	private ObservableList<Category> obsCategory;
	
	private List<Product> products = new ArrayList<>();
	
	@FXML
	public void onBtSearchAction() {
		products.clear();
		products.addAll(service.findByName(txtProductName.getText()));
		updateTableView();
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		Product product = new Product();
		product = getFormData(product);
		service.save(product);
		notifyAddProductListeners(product);
		Utils.currentStage(event).close();
	}
	
	private Product getFormData(Product product) {
		product.setName(txtProductName.getText());
		product.setCategory(comboBoxCategory.getValue());
		return product;
	}
	
	private void notifyAddProductListeners(Product product) {
		if(listener != null) {
			listener.addProductListener(product);
		}
	}
	
	public void subscribeAddProductListener(AddProductListener listener) {
		this.listener = listener;
	}
	
	
	private void updateTableView() {
		obsProduct = FXCollections.observableArrayList(products);
		tableViewProduct.setItems(obsProduct);
		initSelectButtons();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initalizeNodes();
	}
	
	private void initalizeNodes() {
		initialzeTable();
		loadAssociatedObjects();
	}
	
	private void initialzeTable() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
		tableColumnCategory.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCategory()));
	}
	
	private void loadAssociatedObjects() {
		obsCategory = FXCollections.observableArrayList(Category.values());
		
		comboBoxCategory.setItems(obsCategory);
	}
	
	private void selectProduct(Product obj, ActionEvent event) {
		Optional<ButtonType> result = Alerts.showConfirmation("Escolhendo Item",
				"Tem certeza que deseja escolher esse Item?");
		if (result.get() == ButtonType.OK) {
			notifyAddProductListeners(obj);
			Utils.currentStage(event).close();
		}
	}
	
	private void initSelectButtons() {
		tableColumnSelect.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnSelect.setCellFactory(param -> new TableCell<Product, Product>() {
			private final Button button = new Button("Select");

			@Override
			protected void updateItem(Product obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> selectProduct(obj, event));
			}
		});
	}
}
