package com.project.editor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import com.project.editor.util.EditorGrid;
import com.project.editor.util.sql.DbConnect;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class GameEditorMain extends Application{
	private static Integer PADDING = 1; //Used to define the grid thickness
	private static Integer ROWS = 160; //Rows
	private static Integer COLS = 240; //Columns

	Image gridImage1 = new Image("file:gray_grid.png"); 
	Image gridImage2 = new Image("file:white_grid.png");
	Image gridImageRed = new Image("file:red_grid.png"); 
	Image gridImagePink = new Image("file:pink_grid.png");
	Image gridImageGreen = new Image("file:green_grid.png");
	
	MenuBar menuBar = new MenuBar(); //MenuBar
	
	int count = 0;
	EditorGrid grid; //Custom class that handles the content and properties of each Grid
	List<EditorGrid> gridList = new ArrayList<EditorGrid>(); //List containing all of the EditorGridItems
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		
		GridPane gridPane = new GridPane();		
		gridPane.setPadding(new Insets(PADDING));
		gridPane.setHgap(PADDING);
		gridPane.setVgap(PADDING);
 
		//EditorGrid grid; //Custom class that handles the content and properties of each Grid
		//List<EditorGrid> gridList = new ArrayList<EditorGrid>(); //List containing all of the EditorGridItems
		
		createMenuBar(); //Initialize the MenuBar and all it's Items		

		/**
		 * Draw the Grid on screen and add each element to gridList
		 */
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				grid = new EditorGrid(new ImageView(gridImage1), 0);
				gridList.add(grid);
				gridPane.add(grid.getImage(), c, r);	        	
			}
		}

		/**
		 * Add EventHandlers to each item in GridList
		 * Also test code that changes the image depending on the value of EditorGrid.type
		 */
		Iterator<EditorGrid> i = gridList.iterator();
		while (i.hasNext()){
			EditorGrid g = i.next();
			g.getImage().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					System.out.println(g.getId());
					
					if (g.getType() == 0){
						g.getImage().setImage(gridImage2);
						g.setType(0xffffff);
						return;
					}
					if (g.getType() == 0xffffff){
						g.getImage().setImage(gridImageRed);
						g.setType(0xff0000);
						return;
					}
					if (g.getType() == 0xff0000){
						g.getImage().setImage(gridImagePink);
						g.setType(0xff00ff);
						return;
					}
					if (g.getType() == 0xff00ff){
						g.getImage().setImage(gridImageGreen);
						g.setType(0x00ff00);
						return;
					}
					if (g.getType() == 0x00ff00){
						g.getImage().setImage(gridImage1);
						g.setType(0);
						return;
					}
					
					event.consume();
				}
			});
		}
		
		gridPane.setStyle("-fx-background-color: #2f4f4f;");
		
		ScrollPane scrollPane = new ScrollPane(gridPane);
		
		VBox vbox = new VBox(menuBar);
		vbox.setPrefWidth(1280);
		vbox.setPrefHeight(720);
		vbox.getChildren().add(scrollPane);
		
		primaryStage.setScene(new Scene(vbox));
		primaryStage.show();
		
	}
	
	private void createMenuBar(){
		/**
		 * Menus
		 */
		Menu menuFile = new Menu("File");
		Menu menuEdit = new Menu("Edit");
		Menu menuView = new Menu("View");
		
		/**
		 * File MenuItems
		 */
		MenuItem save = new MenuItem("Save");
		save.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	SaveImage();
		    }
		});
		MenuItem saveAs = new MenuItem("SaveAs");
		saveAs.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		        SaveImage();
		    }
		});
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	Alert alert = new Alert(AlertType.CONFIRMATION);
		    	alert.setHeaderText(null);
		    	alert.setContentText("Are you sure?");

		    	Optional<ButtonType> result = alert.showAndWait();
		    	if (result.get() == ButtonType.OK){
		    		System.exit(0);
		    	}
		        
		    }
		});
		
		/**
		 * Add all MenuItems to their corresponding Menus and then add all the Menus to the MenuBar
		 */
		menuFile.getItems().addAll(save, saveAs, exit);
		menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
	}
	
	/**
	 * Create pixel image that the game will read to create the level 
	 * Save the image in the database
	 */
	private void SaveImage(){
		BufferedImage img = new BufferedImage(240, 160, BufferedImage.TYPE_INT_RGB);
		
		/*for(int i = 0; i < ROWS*COLS; i++){
			if(gridList.get(i).getType() == 0){
				img.setRGB(x, y, rgb);
			}
		}*/	
		for(int i = 0; i < ROWS; i++){
			for(int j = 0; j < COLS; i++){
				if(gridList.get(i*j).getType() == 0){
					img.setRGB(i, j, 0x000000);
				}
				else if(gridList.get(i*j).getType() == 0xffffff){
					img.setRGB(i, j, 0xffffff);
				}
				else if(gridList.get(i*j).getType() == 0xff0000){
					img.setRGB(i, j, 0xff0000);
				}
				else if(gridList.get(i*j).getType() == 0xff00ff){
					img.setRGB(i, j, 0xff00ff);
				}
				else if(gridList.get(i*j).getType() == 0x00ff00){
					img.setRGB(i, j, 0x00ff00);
				}

			}
		}
		
		/**
		 * Create the image level
		 */
		File outputFile = new File("levels.png");
		try {
			ImageIO.write(img, "png", outputFile);
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
	        alert.setHeaderText(null);
	    	alert.setContentText(e.getMessage());
	    	alert.showAndWait();
			e.printStackTrace();
		}
		
		DbConnect connection = new DbConnect(); //Class that provides the methods to interact with the database.
		try {
			connection.deleteImages(); //For Testing purposes only! Currently the database can only handle 1 level image at a time. This will be fixed later on and this line will be removed.
		} catch (ClassNotFoundException | SQLException e) {
			Alert alert = new Alert(AlertType.ERROR);
	        alert.setHeaderText(null);
	    	alert.setContentText(e.getMessage());
	    	alert.showAndWait();
			e.printStackTrace();
			
		} 
		try {
			connection.imageDb(outputFile); //Saves the current image to the database
			Alert alert = new Alert(AlertType.INFORMATION);
	    	alert.setHeaderText(null);
	    	alert.setContentText("Image Saved Successfully!");
	    	alert.showAndWait();
		} catch (FileNotFoundException | ClassNotFoundException | SQLException e) {
			Alert alert = new Alert(AlertType.ERROR);
	        alert.setHeaderText(null);
	    	alert.setContentText(e.getMessage());
	    	alert.showAndWait();
			e.printStackTrace();
		} 
	}
	
	/**
	 * Main Method
	 * Launches the JavaFX application
	 */
	public static void main(String[] args){
		GameEditorMain.launch(args);
	}
}
