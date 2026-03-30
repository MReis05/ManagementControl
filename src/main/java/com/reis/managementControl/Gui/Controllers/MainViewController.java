package com.reis.managementControl.Gui.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

@Component
public class MainViewController implements Initializable {

	@FXML
	private VBox contentHolder;
	
	@FXML
	private Button btDashboard;
	
	@FXML
	private Button btDailyTotal;
	
	@FXML
	private Button btTotalPerLocation;
	
	@FXML
	private Button btDetailsPerWeek;
		
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
	
	public synchronized <T> void loadView(String absoluteView, Consumer<T> consumer) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteView));
			VBox vbox = loader.load();
			
			contentHolder.getChildren().clear();
			
			contentHolder.getChildren().addAll(vbox);
			
			T controller = loader.getController();
			
			consumer.accept(controller);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

}
