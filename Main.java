package com.intershalla.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	public Controller controller;

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loder = new FXMLLoader(getClass().getResource("game.fxml"));
		GridPane rootGridPane = loder.load();
		controller = loder.getController();
		controller.createPlayground();


		MenuBar menuBar = createMenu();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().add(menuBar);

		Scene scene = new Scene(rootGridPane);


		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect Four");
		primaryStage.setResizable(false);
		primaryStage.show();

	}

	public MenuBar createMenu() {
		//file
		Menu myFile = new Menu("File");
		MenuItem newGame = new MenuItem("New game");
		newGame.setOnAction(event -> controller.reset());
		MenuItem resetGame = new MenuItem("Reset game");
		resetGame.setOnAction(event -> controller.reset());

		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
		MenuItem exitGame = new MenuItem("Exit game");
		exitGame.setOnAction(event -> {
			Platform.exit();
			System.exit(0);
		});
		myFile.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

		//helpmenu
		Menu helpMenu = new Menu("Help");
		MenuItem aboutGame = new MenuItem("About Connect4");
		aboutGame.setOnAction(event -> aboutConnect4Game());
		SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
		MenuItem aboutMe = new MenuItem("About Me");
       aboutMe.setOnAction(event -> aboutMe());
		helpMenu.getItems().addAll(aboutGame, separatorMenuItem1, aboutMe);


		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(myFile, helpMenu);
		return menuBar;

	}

	private void aboutMe() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About the Devloper");
		alert.setHeaderText("Kushal Singh Yadav");
		alert.setContentText("I love to play around and create games." +
				"Connect 4 is one of them . in free time i like to spend time with nears and dears");
		alert.show();
	}

	private void aboutConnect4Game() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About Connect Four");
		alert.setHeaderText("How to Play ?");
		alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing the right moves.");
	    alert.show();
	}



	public static void main(String[] args) {
		launch(args);
	}
}
