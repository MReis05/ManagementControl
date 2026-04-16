package com.reis.managementControl.Gui.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.reis.managementControl.Gui.Util.ImageManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@Component
public class MainViewController implements Initializable {

	@Autowired
	private ApplicationContext applicationContext;
	
	@FXML
	private VBox contentHolder;
	
	@FXML
	private Button btDashboard;
	
	@FXML
	private Button btDailyTotal;
	
	@FXML
	private Button btTotalPerLocation;
	
	@FXML
	private Button btOrderItemHistory;
	
	@FXML
	private Button btTransaction;
		
	@FXML
	public void onBDashboardAction() {
		loadView("/fxml/DashboardView.fxml", (DashboardController controller)->{
		});
	}
	
	@FXML
	public void onBtDailyTotalAction() {
		loadView("/fxml/DailyTotalView.fxml", (DailyTotalViewController controller)->{
		});
	}
	
	@FXML
	public void onBtTotalPerLocationAction() {
		loadView("/fxml/LocationsTotalView.fxml", (LocationTotalViewController controller) ->{
		});
	}
	
	@FXML
	public void onBtOrderHistoryAction() {
		loadView("/fxml/OrderItemHistoryView.fxml", (OrderItemHistoryViewController controller) ->{
		});
	}
	
	@FXML
	private void onBtTransactionAction() {
		loadView("/fxml/TransactionsView.fxml", (TransactionsViewController controller) ->{
		});
	}
	
	public synchronized <T> void loadView(String absoluteView, Consumer<T> consumer) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteView));
			loader.setControllerFactory(applicationContext::getBean);
			VBox vbox = loader.load();
			
			contentHolder.getChildren().clear();
			
			contentHolder.getChildren().addAll(vbox);
			VBox.setVgrow(vbox, Priority.ALWAYS);
			
			T controller = loader.getController();
			
			consumer.accept(controller);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeResources();
	}
	
	private void initializeResources() {
		ImageView dashboard = new ImageView(ImageManager.getImage("dashboard"));
		ImageView dailyTotal = new ImageView(ImageManager.getImage("dailyTotal"));
		ImageView location = new ImageView(ImageManager.getImage("location"));
		ImageView orderItemHistory = new ImageView(ImageManager.getImage("orderItemHistory"));
		ImageView transaction = new ImageView(ImageManager.getImage("transactionIcon"));
		
		dashboard.setFitHeight(32);
		dashboard.setFitWidth(32);
		
		dailyTotal.setFitHeight(32);
		dailyTotal.setFitWidth(32);
		
		location.setFitHeight(32);
		location.setFitWidth(32);
		
		orderItemHistory.setFitHeight(32);
		orderItemHistory.setFitWidth(32);
		
		transaction.setFitHeight(32);
		transaction.setFitWidth(32);
		
		btDashboard.setGraphic(dashboard);
		btDailyTotal.setGraphic(dailyTotal);
		btTotalPerLocation.setGraphic(location);
		btOrderItemHistory.setGraphic(orderItemHistory);
		btTransaction.setGraphic(transaction);
	}

}
