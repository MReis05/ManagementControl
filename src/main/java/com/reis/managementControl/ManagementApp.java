package com.reis.managementControl;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.reis.managementControl.Gui.Util.ImageManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
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
    	loadResources();
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
    	loader.setControllerFactory(springContext::getBean);
    	BorderPane pane = loader.load();
    	mainScene = new Scene(pane);
    	mainScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    	primaryStage.setScene(mainScene);
        primaryStage.setTitle("Controle de Caixa e Insumos");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();
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
