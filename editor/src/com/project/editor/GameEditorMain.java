package com.project.editor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
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
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
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

	EditorGrid grid; //Custom class that handles the content and properties of each Grid
	List<EditorGrid> gridList = new ArrayList<EditorGrid>(); //List containing all of the EditorGridItems

	GridPane gridPane = new GridPane();	//GridPane containing the click-able images

	@Override
	public void start(Stage primaryStage) throws Exception {

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
				if ((r == 0) || (c == 0) || (r == ROWS-1) || (c == COLS-1))
					grid = new EditorGrid(new ImageView(gridImage2), 0xffffff);
				else
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
			g.getImage().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {

					if (event.isPrimaryButtonDown()){
						if (g.getType() == 0){
							g.getImage().setImage(gridImage2);
							g.setType(0xffffff);
							event.consume();
							return;
						}
						if (g.getType() == 0xffffff){
							g.getImage().setImage(gridImageRed);
							g.setType(0xff0000);
							event.consume();
							return;
						}
						if (g.getType() == 0xff0000){
							g.getImage().setImage(gridImagePink);
							g.setType(0xff00ff);
							event.consume();
							return;
						}
						if (g.getType() == 0xff00ff){
							g.getImage().setImage(gridImageGreen);
							g.setType(0x00ff00);
							event.consume();
							return;
						}
						if (g.getType() == 0x00ff00){
							g.getImage().setImage(gridImage1);
							g.setType(0);
							event.consume();
							return;
						}
					} else if (event.isSecondaryButtonDown()){
						g.getImage().setImage(gridImage1);
						g.setType(0);
						event.consume();
						return;
					}					
				}
			});
		}

		gridPane.setStyle("-fx-background-color: #2f4f4f;");

		ScrollPane scrollPane = new ScrollPane(gridPane);

		VBox vbox = new VBox(menuBar);
		vbox.setPrefWidth(1280);
		vbox.setPrefHeight(720);
		vbox.getChildren().add(scrollPane);

		primaryStage.setTitle("Game Editor");
		primaryStage.setScene(new Scene(vbox));
		primaryStage.show();

	}

	private void createMenuBar(){
		/**
		 * Menus
		 */
		Menu menuFile = new Menu("File");

		/**
		 * File MenuItems
		 */
		MenuItem newLevel = new MenuItem("New");
		newLevel.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setHeaderText(null);
				alert.setContentText("Are you sure you want to start a new level? \nAll unsaved changes will be lost!");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					clearImage();
				}
			}
		});
		MenuItem scores = new MenuItem("Scoreboard");
		scores.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Scores");
				alert.setHeaderText(null);
				try {
					List<String> scores = retrieveScores();
					String scoreText = "Player: \tLevel: \t\tTime: \tDate:\n";
					Iterator<String> i = scores.iterator();
					while (i.hasNext()){
						scoreText += i.next() + "\n";
					}
					alert.setContentText(scoreText);
				} catch (ClassNotFoundException | SQLException e) {
					Alert alertError = new Alert(AlertType.ERROR);
					alertError.setHeaderText(null);
					alertError.setContentText(e.getMessage());
					alertError.showAndWait();
					e.printStackTrace();
				}
				finally {
					alert.showAndWait();
				}
				
			}
		});
		MenuItem open = new MenuItem("Open");
		open.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				loadImage();
			}
		});
		MenuItem save = new MenuItem("Save");
		save.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				SaveImage();
			}
		});
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setHeaderText(null);
				alert.setContentText("Are you sure you want to quit? \nAll unsaved changes will be lost!");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					System.exit(0);
				}

			}
		});

		/**
		 * Add all MenuItems to their corresponding Menus and then add all the Menus to the MenuBar
		 */
		menuFile.getItems().addAll(newLevel, open, scores, save, exit);
		menuBar.getMenus().addAll(menuFile);
	}

	/**
	 * Create pixel image that the game will read to create the level 
	 * Save the image in the database
	 */
	private void SaveImage(){
		DbConnect connection = new DbConnect(); //Class that provides the methods to interact with the database.

		try {
			TextInputDialog dialog = new TextInputDialog("TestLevel");
			dialog.setTitle("Save Level");
			dialog.setHeaderText(null);
			dialog.setContentText("Please enter the level name:");

			// Traditional way to get the response value.
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()){
				List<String> choices = retrieveAuthorList();

				ChoiceDialog<String> dialogCombo = new ChoiceDialog<>("", choices);
				dialogCombo.setTitle("Save As");
				dialogCombo.setHeaderText(null);
				dialogCombo.setContentText("Choose an author:");

				// Traditional way to get the response value.
				Optional<String> resultCombo = dialogCombo.showAndWait();
				if (resultCombo.isPresent()){
					BufferedImage img = new BufferedImage(240, 160, BufferedImage.TYPE_INT_RGB);

					for(int i = 0; i < ROWS; i++){
						for(int j = 0; j < COLS; j++){				
							if(gridList.get((i*240)+j).getType() == 0){
								img.setRGB(j, i, 0x000000);
							}
							else if(gridList.get((i*240)+j).getType() == 0xffffff){
								img.setRGB(j, i, 0xffffff);
							}
							else if(gridList.get((i*240)+j).getType() == 0xff0000){
								img.setRGB(j, i, 0xff0000);
							}
							else if(gridList.get((i*240)+j).getType() == 0xff00ff){
								img.setRGB(j, i, 0xff00ff);
							}
							else if(gridList.get((i*240)+j).getType() == 0x00ff00){
								img.setRGB(j, i, 0x00ff00);
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
					connection.imageDb(result.get(), resultCombo.get(), outputFile); //Saves the current image to the database

					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText(null);
					alert.setContentText("Image Saved Successfully!");
					alert.showAndWait();
					System.out.println("Your name: " + result.get());
				}
			}
		} catch (FileNotFoundException | ClassNotFoundException | SQLException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText(e.getMessage());
			alert.showAndWait();
			e.printStackTrace();
		} 
	}


	private void clearImage(){
		gridList = new ArrayList<EditorGrid>();
		gridPane.getChildren().clear();

		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				if ((r == 0) || (c == 0) || (r == ROWS-1) || (c == COLS-1))
					grid = new EditorGrid(new ImageView(gridImage2), 0xffffff);
				else
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
			g.getImage().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {

					if (event.isPrimaryButtonDown()){
						if (g.getType() == 0){
							g.getImage().setImage(gridImage2);
							g.setType(0xffffff);
							event.consume();
							return;
						}
						if (g.getType() == 0xffffff){
							g.getImage().setImage(gridImageRed);
							g.setType(0xff0000);
							event.consume();
							return;
						}
						if (g.getType() == 0xff0000){
							g.getImage().setImage(gridImagePink);
							g.setType(0xff00ff);
							event.consume();
							return;
						}
						if (g.getType() == 0xff00ff){
							g.getImage().setImage(gridImageGreen);
							g.setType(0x00ff00);
							event.consume();
							return;
						}
						if (g.getType() == 0x00ff00){
							g.getImage().setImage(gridImage1);
							g.setType(0);
							event.consume();
							return;
						}
					} else if (event.isSecondaryButtonDown()){
						g.getImage().setImage(gridImage1);
						g.setType(0);
						event.consume();
						return;
					}					
				}
			});
		}
	}

	private void loadImage(){
		try {
			List<String> choices =  retrieveLevelList();

			ChoiceDialog<String> dialogCombo = new ChoiceDialog<>("", choices);
			dialogCombo.setTitle("Load Level");
			dialogCombo.setHeaderText(null);
			dialogCombo.setContentText("Choose a level:");

			// Traditional way to get the response value.
			Optional<String> resultCombo = dialogCombo.showAndWait();
			if (resultCombo.isPresent()){
				gridList = new ArrayList<EditorGrid>();
				gridPane.getChildren().clear();

				BufferedImage img = null;
				retrieveImage(resultCombo.get());
				img = ImageIO.read(new File("levels.png"));


				/**
				 * just a test code to create a simple image for the test level. This only creates a box image.
				 */
				for (int r = 0; r < ROWS; r++) {
					for (int c = 0; c < COLS; c++) {

						int clr = img.getRGB(c, r);

						if(clr == Color.decode("0xffffff").getRGB()){
							grid = new EditorGrid(new ImageView(gridImage2), 0xffffff);					
						} else if(clr == Color.decode("0xff0000").getRGB()){
							grid = new EditorGrid(new ImageView(gridImageRed), 0xff0000);					
						} else if(clr == Color.decode("0xff00ff").getRGB()){
							grid = new EditorGrid(new ImageView(gridImagePink), 0xff00ff);					
						} else if(clr == Color.decode("0x00ff00").getRGB()){
							grid = new EditorGrid(new ImageView(gridImageGreen), 0x00ff00);					
						} else {
							grid = new EditorGrid(new ImageView(gridImage1), 0x000000);
						} 

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
					g.getImage().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent event) {

							if (event.isPrimaryButtonDown()){
								if (g.getType() == 0){
									g.getImage().setImage(gridImage2);
									g.setType(0xffffff);
									event.consume();
									return;
								}
								if (g.getType() == 0xffffff){
									g.getImage().setImage(gridImageRed);
									g.setType(0xff0000);
									event.consume();
									return;
								}
								if (g.getType() == 0xff0000){
									g.getImage().setImage(gridImagePink);
									g.setType(0xff00ff);
									event.consume();
									return;
								}
								if (g.getType() == 0xff00ff){
									g.getImage().setImage(gridImageGreen);
									g.setType(0x00ff00);
									event.consume();
									return;
								}
								if (g.getType() == 0x00ff00){
									g.getImage().setImage(gridImage1);
									g.setType(0);
									event.consume();
									return;
								}
							} else if (event.isSecondaryButtonDown()){
								g.getImage().setImage(gridImage1);
								g.setType(0);
								event.consume();
								return;
							}					
						}
					});
				}
			}
		} catch (ClassNotFoundException | IOException | SQLException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText(e.getMessage());
			alert.showAndWait();
			e.printStackTrace();
		}
	}

	private List<String> retrieveLevelList() throws ClassNotFoundException, SQLException {
		DbConnect connection = new DbConnect();
		List<String> list = new ArrayList<String>();
		ResultSet rs = connection.retrieveLevelList();

		while (rs.next()){
			list.add(rs.getString(1));
		}
		return list;
	}

	private List<String> retrieveAuthorList() throws ClassNotFoundException, SQLException {
		DbConnect connection = new DbConnect();
		List<String> list = new ArrayList<String>();
		ResultSet rs = connection.retrieveAuthorList();

		while (rs.next()){
			list.add(rs.getString(1));
		}
		return list;
	}
	
	private List<String> retrieveScores() throws ClassNotFoundException, SQLException{
		List<String> scores = new ArrayList<String>();
		
		DbConnect connection = new DbConnect();
		ResultSet rs = connection.retrieveScores();
		while (rs.next()){
			scores.add(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getFloat(3) + "\t" + rs.getTimestamp(4));
		}
		return scores;
	}

	private void retrieveImage(String level) throws IOException, ClassNotFoundException, SQLException{
		DbConnect connection = new DbConnect();
		ResultSet rs = connection.retrieveImage(level);

		while (rs.next()){
			InputStream in = rs.getBinaryStream(1);
			OutputStream f = new FileOutputStream(new File("levels.png"));
			int c = 0;
			while ((c = in.read()) > -1) {
				f.write(c);
			}
			f.close();
			in.close();
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
