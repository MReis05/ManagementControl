package com.reis.managementControl.Gui.Util;

import java.util.HashMap;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

public class ImageManager {

	private static final HashMap<String, Image> imageCache = new HashMap<>();
	
	public static void loadImages() {
		String imagesPath = "/images/";
		
		try {
			imageCache.put("dashboard", new Image(ImageManager.class.getResourceAsStream(imagesPath + "dashboard_icon.png")));
			imageCache.put("dailyTotal", new Image(ImageManager.class.getResourceAsStream(imagesPath + "daily_total_icon.png")));
			imageCache.put("location", new Image(ImageManager.class.getResourceAsStream(imagesPath + "location_icon.png")));
			imageCache.put("orderItemHistory", new Image(ImageManager.class.getResourceAsStream(imagesPath + "order_item_history_icon.png")));
			imageCache.put("plusIcon",new Image(ImageManager.class.getResourceAsStream(imagesPath + "plus_icon.png")));
			imageCache.put("moneyIcon", new Image(ImageManager.class.getResourceAsStream(imagesPath + "money_icon.png")));
			imageCache.put("saveIcon", new Image(ImageManager.class.getResourceAsStream(imagesPath + "save_icon.png")));
			imageCache.put("searchIcon", new Image(ImageManager.class.getResourceAsStream(imagesPath + "search_icon.png")));
			imageCache.put("programIcon", new Image(ImageManager.class.getResourceAsStream(imagesPath + "program_icon.png")));
		}
		catch (Exception e) {
			e.printStackTrace();
			Alerts.showAlert("", "Erro em carregar arquivos", "", AlertType.ERROR);
		}
	}
	
	public static Image getImage(String key) {
		return imageCache.get(key);
	}
}
