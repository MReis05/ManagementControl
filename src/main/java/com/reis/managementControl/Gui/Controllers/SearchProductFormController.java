package com.reis.managementControl.Gui.Controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.reis.managementControl.Entities.Product;
import com.reis.managementControl.Entities.Enums.Category;
import com.reis.managementControl.Gui.Listerners.AddProductListener;
import com.reis.managementControl.Gui.Util.Alerts;
import com.reis.managementControl.Gui.Util.ImageManager;
import com.reis.managementControl.Gui.Util.Utils;
import com.reis.managementControl.Services.ProductService;
import com.reis.managementControl.Services.Exceptions.ValidationExceptions;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

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
	private Label labelErrorProductName;
	
	@FXML
	private Label labelErrorCategory;
	
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
		products.addAll(service.findByName(txtProductName.getText().trim()));
		updateTableView();
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		try {
			Product product = new Product();
			product = getFormData(product);
			service.save(product);
			notifyAddProductListeners(product);
			Utils.currentStage(event).close();
		}
		catch(ValidationExceptions e) {
			setErrorMessages(e.getErrors());
		}
		catch(DataIntegrityViolationException e) {
			Alerts.showAlert("Aviso", null, "Já existe um produto cadastrado com este nome!", AlertType.WARNING);
		}
	}
	
	private Product getFormData(Product product) {
		labelErrorProductName.setText("");
		
		ValidationExceptions exception = new ValidationExceptions("Validation Error");
		
		if(txtProductName.getText() == null || txtProductName.getText().trim().isEmpty()) {
			exception.addError("Product Name", "Field can't be empty");
		}
		product.setName(txtProductName.getText().trim());
		if(comboBoxCategory.getValue() == null) {
			exception.addError("Category", "You must select one Category");
		}
		else {
			product.setCategory(comboBoxCategory.getValue());
		}
		
		if(!exception.getErrors().isEmpty()) {
			throw exception;
		}
		
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
		initializeTable();
		loadAssociatedObjects();
		initializeResources();
	}
	
	private void initializeTable() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
		tableColumnCategory.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCategory()));
	}
	
	private void loadAssociatedObjects() {
		obsCategory = FXCollections.observableArrayList(Category.values());
		
		comboBoxCategory.setItems(obsCategory);
	}
	
	private void initializeResources() {
		ImageView search = new ImageView(ImageManager.getImage("searchIcon"));
		ImageView save = new ImageView(ImageManager.getImage("saveIcon"));
		
		search.setFitHeight(16);
		search.setFitWidth(16);
		
		save.setFitHeight(23);
		save.setFitWidth(23);
		
		btSave.setGraphic(save);
		btSearch.setGraphic(search);
	}
	
	private void selectProduct(Product obj, ActionEvent event) {
			notifyAddProductListeners(obj);
			Utils.currentStage(event).close();
	}
	
	private void initSelectButtons() {
		tableColumnSelect.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnSelect.setCellFactory(param -> new TableCell<Product, Product>() {
			private final Button button = new Button("Selecionar");

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
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> keys = errors.keySet();
		
		labelErrorProductName.setText(keys.contains("Product Name") ? errors.get("Product Name"): "");
		labelErrorCategory.setText(keys.contains("Category") ? errors.get("Category") : "");
	}
}
