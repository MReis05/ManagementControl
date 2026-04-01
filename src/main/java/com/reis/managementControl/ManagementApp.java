package com.reis.managementControl;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ManagementApp extends Application {
	
	private ConfigurableApplicationContext springContext;
	
	private static Scene mainScene;

    @Override
    public void init() throws Exception {
        springContext = new SpringApplicationBuilder(ManagementControlApplication.class).headless(false).run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
    	loader.setControllerFactory(springContext::getBean);
    	BorderPane pane = loader.load();
    	mainScene = new Scene(pane);
    	primaryStage.setScene(mainScene);
        primaryStage.setTitle("Controle de Caixa e Insumos");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
        Platform.exit();
    }
}
