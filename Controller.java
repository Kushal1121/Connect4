package com.intershalla.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLOUMNS = 7;
	private static final int ROWS=6;
	private static final int CIRCLE_DAIMETER=80;
	private static final String discColor1="24303E";
	private static final String discColor2="4CAA88";

	private static String PLAYER_ONE= "Player One";
	private static String PLAYER_TWO="Player Two";

	private boolean isPlayer_OneTurn = true;




	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscsPane;
	@FXML
	public Label PlayerNameLabel;
	@FXML
	public TextField playerOneTextField;
	@FXML
	public TextField playerTwoTextField;
	@FXML
	public Button setNamesButton;



	private Disc [][] insertedDiscArray=new Disc[ROWS][COLOUMNS]; //for structural changes : for devloper

	private boolean isAllowedToInsert=true;    // flag to avoid same color disc being added

	public void createPlayground(){

		Platform.runLater(() -> setNamesButton.requestFocus());


	  Shape rectangleWithHoles= createGameStructuralGrid();
      rootGridPane.add(rectangleWithHoles,1,2);
    List<Rectangle> rectangleList=createClickableColoumn();
    for (Rectangle rectangle:rectangleList){
	    rootGridPane.add(rectangle,1,2);
    }
		setNamesButton.setOnAction(event -> {
			 PLAYER_ONE  =playerOneTextField.getText();
			 PLAYER_TWO=playerTwoTextField.getText();
			PlayerNameLabel.setText(isPlayer_OneTurn? PLAYER_ONE:PLAYER_TWO);
		});

	}



	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public Shape createGameStructuralGrid(){
		Shape rectangleWithHoles =new	Rectangle((COLOUMNS+1)*CIRCLE_DAIMETER,(ROWS+1)*CIRCLE_DAIMETER);
		for (int row=0;row<ROWS;row++){
			for (int coloumn=0;coloumn<COLOUMNS;coloumn++){
				Circle circle=new Circle();
				circle.setRadius(CIRCLE_DAIMETER/2);
				circle.setCenterX(CIRCLE_DAIMETER/2);
				circle.setCenterY(CIRCLE_DAIMETER/2);
				circle.setSmooth(true);
				circle.setTranslateX(coloumn*(CIRCLE_DAIMETER+5)+ CIRCLE_DAIMETER/4);
				circle.setTranslateY(row*(CIRCLE_DAIMETER+5)+CIRCLE_DAIMETER/4);
				rectangleWithHoles=Shape.subtract(rectangleWithHoles,circle);


			}
		}


		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableColoumn() {
		List<Rectangle> rectangleList = new ArrayList<>();
		for (int coloumn = 0; coloumn < COLOUMNS; coloumn++) {
			Rectangle rectangle = new Rectangle(CIRCLE_DAIMETER, (ROWS + 1) * CIRCLE_DAIMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(coloumn*(CIRCLE_DAIMETER+5)+CIRCLE_DAIMETER / 4);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			 final int col=coloumn;
			rectangle.setOnMouseClicked(event -> {
				if(isAllowedToInsert){
			    isAllowedToInsert=false;  // when disc is being dropeed then no more disc will be inseted
				insertedDisc(new  Disc(isPlayer_OneTurn),col);}
			});

			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private  void insertedDisc( Disc disc, int coloumn ){

		int row=ROWS-1;
		while (row>=0){
			if(getDiscIfPresent(row,coloumn)==null){
				break;
			}
			row--;
		}
		if(row<0){
			return;
		}

		insertedDiscArray[row][coloumn]=disc;  //for structural changes : for devloper
		insertedDiscsPane.getChildren().addAll(disc); // for virtual changes: for player
		disc.setTranslateX(coloumn*(CIRCLE_DAIMETER+5)+CIRCLE_DAIMETER / 4);
		TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.5),disc);

		translateTransition.setToY(row*(CIRCLE_DAIMETER+5)+CIRCLE_DAIMETER / 4);
          int currentRows=row;

		translateTransition.setOnFinished(event -> {
			isAllowedToInsert=true;
			if(gameEnded(currentRows,coloumn)){
                gameOver();
                return;

			}


			isPlayer_OneTurn=!isPlayer_OneTurn;
			PlayerNameLabel.setText(isPlayer_OneTurn? PLAYER_ONE :PLAYER_TWO);


		});




		translateTransition.play();
	}

	private void gameOver() {

		String winner = isPlayer_OneTurn? PLAYER_ONE : PLAYER_TWO;
		System.out.println("Winner is "+ winner);
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect4");
		alert.setHeaderText("The Winner is "+winner);
		alert.setContentText("Want to play again?");
		ButtonType yesBtn=new ButtonType("Yes");
		ButtonType noBtn=new ButtonType("No,Exit");
		alert.getButtonTypes().setAll(yesBtn,noBtn);
        Platform.runLater(()->{               //help to resolve IllegalStateException
	        Optional<ButtonType> clickedBtn =alert.showAndWait();
	        if(clickedBtn.isPresent() && clickedBtn.get()==yesBtn){
		        reset();}
	        else{
		        Platform.exit();
		        System.exit(0);
	        }
        });

		}


	public void reset() {
		insertedDiscsPane.getChildren().clear();   //visually
		for (int row = 0; row < insertedDiscArray.length ; row++) {
			for (int col = 0; col < insertedDiscArray[row].length ; col++) {  //structurally
				insertedDiscArray[row][col]=null;

			}

		}

		isPlayer_OneTurn=true;    // let player start the game
		PlayerNameLabel.setText(PLAYER_ONE);

		createPlayground(); //create fresh playgroung

	}

	public boolean gameEnded(int row, int coloumn){
		//vertical points . a small example:player has inserted his last disc at row =2,cloumn =3
		// range of row values : 0,1,2,3,4,5
		//index of each element present in cloumn [row][coloumn] : 0,3 1,3 2,3 3,3 4,3 5,3 --> point2D

		        List<Point2D> verticalPoints= IntStream.rangeClosed(row-3,row+3).              //range of rows
				mapToObj(r-> new Point2D(r,coloumn)).  // map
				collect(Collectors.toList());


		        List<Point2D> horizontalPoints= IntStream.rangeClosed(coloumn-3,coloumn+3).              //range of column
				mapToObj(col-> new Point2D(row,col)).  // map
				collect(Collectors.toList());

		        Point2D startpoin1= new Point2D(row-3,coloumn+3);
		        List<Point2D> daigonal1=IntStream.rangeClosed(0,6).
				        mapToObj(i -> startpoin1.add(i,-i)).
				        collect(Collectors.toList());
		        Point2D startpoin2= new Point2D(row-3,coloumn-3);
		        List<Point2D> daigonal2=IntStream.rangeClosed(0,6).
				     mapToObj(i -> startpoin2.add(i,i)).
				     collect(Collectors.toList());

		        boolean isEnded = checkCombination(verticalPoints) || checkCombination(horizontalPoints) ||checkCombination(daigonal1) ||checkCombination(daigonal2) ;

            return isEnded;

	}

	private boolean checkCombination(List<Point2D> points) {
       int chain=0;
		for ( Point2D point: points) {
			int rowIndexforArray = (int) point.getX();
			int coloumnIndexforArray= (int) point.getY();
			Disc disc= getDiscIfPresent(rowIndexforArray,coloumnIndexforArray);
			if(disc!=null && disc.isPlayerOneMove==isPlayer_OneTurn) {  //if the last inserted disc is inserted by current player

				chain++;
				if (chain == 4) {
					return true;
				}
			}
				else {
					chain=0;
				}
			}
    return false;
		}


		private Disc getDiscIfPresent(int row,int column){   // to prevent array index out of bound exception
		if (row>=ROWS || row<0 ||column>=COLOUMNS || column<0)
			return null;

		return insertedDiscArray[row][column];
		 }

	private class  Disc extends Circle{
		public    final boolean isPlayerOneMove ;
		public Disc(boolean isPlayerOneMove){
			this.isPlayerOneMove=isPlayerOneMove;
			setRadius(CIRCLE_DAIMETER/2);
			setFill(isPlayerOneMove? Color.valueOf(discColor1): Color.valueOf(discColor2));
           setCenterX(CIRCLE_DAIMETER/2);
           setCenterY(CIRCLE_DAIMETER/2);

		}

	}
}
