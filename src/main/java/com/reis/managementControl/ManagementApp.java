package com.reis.managementControl;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.reis.managementControl.Gui.Util.Alerts;
import com.reis.managementControl.Gui.Util.ImageManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ManagementApp extends Application {
	
	private ConfigurableApplicationContext springContext;
	
	private static Scene mainScene;

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	loadResources();
    	FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/fxml/SplashScreenView.fxml"));
    	Parent splashRoot = splashLoader.load();
    	
    	Stage splashStage = new Stage();
    	splashStage.initStyle(StageStyle.UNDECORATED);
    	splashStage.setScene(new Scene(splashRoot));
    	splashStage.getIcons().add(ImageManager.getImage("programIcon"));
    	splashStage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    	splashStage.show();
    	
    	Task<Parent> loadMainTask = new Task<Parent>() {

			@Override
			protected Parent call() throws Exception {
				springContext = new SpringApplicationBuilder(ManagementControlApplication.class).headless(false).run();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
		    	loader.setControllerFactory(springContext::getBean);
				
				return loader.load();
			}
    		
    	};
    	
    	loadMainTask.setOnSucceeded(event ->{
    		BorderPane pane = (BorderPane)loadMainTask.getValue();
        	mainScene = new Scene(pane);
        	mainScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        	primaryStage.setScene(mainScene);
        	primaryStage.getIcons().add(ImageManager.getImage("programIcon"));
            primaryStage.setTitle("Controle de Caixa e Insumos");
            primaryStage.setWidth(800);
            primaryStage.setHeight(600);
            
            splashStage.close();
            primaryStage.show();
    	});
    	
    	loadMainTask.setOnFailed(event -> {
            Alerts.showAlert("", "Erro em carregar aplicação", "", AlertType.ERROR);
            loadMainTask.getException().printStackTrace();
        });
    	
    	Thread thread = new Thread(loadMainTask);
        thread.setDaemon(true);
        thread.start();
    }
    
    private void loadResources() {
    	Font.loadFont(getClass().getResourceAsStream("/fonts/Inter.ttf"), 10);
    	Font.loadFont(getClass().getResourceAsStream("/fonts/Lato-Bold.ttf"), 10);
    	Font.loadFont(getClass().getResourceAsStream("/fonts/Lato-Regular.ttf"), 10);
    	Font.loadFont(getClass().getResourceAsStream("/gui/gui.resources/fonts/Montserrat-VariableFont_wght.ttf"), 10);
    	ImageManager.loadImages();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
        Platform.exit();
    }
}
