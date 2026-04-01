package com.reis.managementControl.Gui.Controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.reis.managementControl.Entities.Location;
import com.reis.managementControl.Entities.Order;
import com.reis.managementControl.Entities.OrderItem;
import com.reis.managementControl.Gui.Util.Utils;

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
	private ApplicationContext applicationContext;
	
	@FXML
	private Button btIncreaseTransfer;
	
	@FXML
	private Button btNewOrder;
	
	@FXML
	private Label currentCashier;
	
	@FXML
	private Label expectedTransfer;
	
	@FXML
	private Label currentCashierPlusTransfer;
	
	@FXML
	private TableView<OrderItem> tableViewRankingItems;
	
	@FXML
	private TableColumn<OrderItem, String> tableColumnItemName;
	
	@FXML
	private TableColumn<OrderItem, BigDecimal> tableColumnItemValue;
	
	@FXML
	private TableView<Location> tableViewRankingLocation;
	
	@FXML
	private TableColumn<Location, String> tableColumnLocationName;
	

	@FXML
	private TableColumn<Location, BigDecimal> tableColumnLocationValue;
	
	
	@FXML
	public void onBtNewOrderAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		dialogForm("/fxml/AddOrderDialogForm.fxml", parentStage);
	}
	
	public void dialogForm(String absoluteView, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteView));
			loader.setControllerFactory(applicationContext::getBean);
			VBox vbox = loader.load();
			
			AddOrderFormController controller = loader.getController();
			Order order = new Order();
			controller.setOrder(order);
			
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
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
