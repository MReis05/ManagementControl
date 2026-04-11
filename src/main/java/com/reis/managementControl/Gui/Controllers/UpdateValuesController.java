package com.reis.managementControl.Gui.Controllers;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.reis.managementControl.Gui.Listerners.UpdateValuesListener;
import com.reis.managementControl.Gui.Util.Constraints;
import com.reis.managementControl.Gui.Util.ImageManager;
import com.reis.managementControl.Gui.Util.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

@Component
public class UpdateValuesController implements Initializable {

	private UpdateValuesListener listener;

	@FXML
	private TextField txtCurrentCashier;
	
	@FXML
	private TextField txtExpectedTransfer;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		BigDecimal currentCashier = new BigDecimal(txtCurrentCashier.getText());
		BigDecimal expectedTransfer = new BigDecimal(txtExpectedTransfer.getText());
		
		notifyUpdateValuesListeners(currentCashier, expectedTransfer);
		
		Utils.currentStage(event).close();
	}
	
	@FXML
	public void onBtCancelAcion(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	private void notifyUpdateValuesListeners(BigDecimal currentCashier, BigDecimal expectedTransfer) {
		if(listener != null) {
			listener.updateValues(currentCashier, expectedTransfer);
		}
	}
	
	public void subscribeUpdateValuesListener(UpdateValuesListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Constraints.setTextFieldDouble(txtCurrentCashier);
		Constraints.setTextFieldDouble(txtExpectedTransfer);
		initializeResources();
	}
	
	private void initializeResources() {
		ImageView save = new ImageView(ImageManager.getImage("saveIcon"));
		
		save.setFitHeight(23);
		save.setFitWidth(23);
		
		btSave.setGraphic(save);
	}
	
	
}
