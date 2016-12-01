package com.project.editor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class GameEditor extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		MenuBar menuBar = new MenuBar();
		Menu menuFile = new Menu("File");
		Menu menuEdit = new Menu("Edit");
		Menu menuView = new Menu("View");
		
		menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
		
		HBox mainPane = new HBox();
		
		mainPane.getChildren().add(menuBar);
		
		Scene scene = new Scene(mainPane);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args){
		GameEditor.launch(args);
	}
}
