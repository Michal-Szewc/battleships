package Game.GUI;

import Game.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.LinkedList;

public class App extends Application{
    private Stage primaryStage;
    private Scene scene;
    private GridPane menuPane;
    private GridPane playerBoardGrid;
    private GridPane AIBoardGrid;
    private GridPane shipEditor;
    private GridPane gameGrid;

    private GameSettings gameSettings;
    private LinkedList<TextField> textFields = new LinkedList<>();

    private CheckBox playerStartsCheck;
    private Board playerBoard;
    private Board AIBoard;
    private Board AIBoardView;
    private boolean playerStarts;

    private AI_base AI;
    private int difficulty;
    private int dreadnoughtCount = 0;
    private int cruiserCount = 0;
    private int destroyerCount = 0;
    private int selectedShipLength = 0;
    private Direction selectedShipDirection = Direction.RIGHT;
    private boolean isPlacing = false;
    private boolean isRotating = false;

    private boolean isPressing = false;

    private int turn = 1;
    private int playerPoints = 0;
    private int AIPoints = 0;

    private void addNumberField(LinkedList<TextField> textFields,int columnIndex, int rowIndex, String label, String defaultLabel){
        Label Label = new Label(label);
        menuPane.add(Label,columnIndex,rowIndex);
        TextField textField = new TextField(defaultLabel);
        textFields.add(textField);
        menuPane.add(textField,columnIndex + 1,rowIndex);
    }

    private void selectShip(int Length){
        selectedShipLength = Length;
        selectedShipDirection = Direction.RIGHT;
        placeShips();
    }

    private void rotateShip(boolean forward){
        isRotating = true;
        if(forward)
            selectedShipDirection = selectedShipDirection.next();
        else
            selectedShipDirection = selectedShipDirection.previous();
        placeShips();
        isRotating = false;
    }

    private void placeShip(Button butt){
        isPlacing = true;
        if (selectedShipLength == 0) {
            isPlacing = false;
            return;
        }
        try{
            playerBoard.place(new Ship(new Vector2d(playerBoardGrid.getColumnIndex(butt),playerBoardGrid.getRowIndex(butt)),selectedShipLength,selectedShipDirection,playerBoard));
            switch (selectedShipLength){
                case 5 -> dreadnoughtCount += 1;
                case 4 -> cruiserCount += 1;
                case 2 -> destroyerCount += 1;
            }
            selectedShipLength = 0;
        } catch (Error e) {

        }
        placeShips();
        isPlacing = false;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setMaximized(true);

        menuPane = new GridPane();

        addNumberField(textFields,0,0,"Map size", "10");
        addNumberField(textFields,0,1,"Turn limit", "30");
        addNumberField(textFields,0,2,"Number of dreadnoughts", "1");
        addNumberField(textFields,0,3,"Points for dreadnought", "10");
        addNumberField(textFields,0,4,"Number of cruisers", "2");
        addNumberField(textFields,0,5,"Points for cruiser", "5");
        addNumberField(textFields,0,6,"Number of destroyers", "4");
        addNumberField(textFields,0,7,"Points for destroyer", "2");
        addNumberField(textFields,0,8,"Difficulty level: 1 - easy, 2 - hard", "1");

        playerStartsCheck = new CheckBox("Player starts.");
        menuPane.add(playerStartsCheck,0,9);

        Button startButton = new Button("start");
        startButton.setOnAction(e -> prepareEditor());
        menuPane.add(startButton, 0, 10);

        scene = new Scene(menuPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void prepareEditor(){
        this.gameSettings = new GameSettings();
        this.gameSettings.setMapWidth(Integer.parseInt(textFields.get(0).getText()));
        this.gameSettings.setTurnLimit(Integer.parseInt(textFields.get(1).getText()));
        this.gameSettings.setDreadnoughtCount(Integer.parseInt(textFields.get(2).getText()));
        this.gameSettings.setDreadnoughtPoints(Integer.parseInt(textFields.get(3).getText()));
        this.gameSettings.setCruiserCount(Integer.parseInt(textFields.get(4).getText()));
        this.gameSettings.setCruiserPoints(Integer.parseInt(textFields.get(5).getText()));
        this.gameSettings.setDestroyerCount(Integer.parseInt(textFields.get(6).getText()));
        this.gameSettings.setDestroyerPoints(Integer.parseInt(textFields.get(7).getText()));

        playerStarts = playerStartsCheck.isSelected();
        difficulty = Integer.parseInt(textFields.get(8).getText());
        playerBoard = new Board(gameSettings.getMapWidth(),Tile.EMPTY);

        if(difficulty != 1 && difficulty != 2)
            throw new IllegalArgumentException("Difficulty should be 1 or 2.");
        if(difficulty == 1)
            AI = new EasyAI(gameSettings);
        else
            AI = new HardAI(gameSettings);

        placeShips();
    }

    public void placeShips(){
        if (dreadnoughtCount == gameSettings.getDreadnoughtCount() && cruiserCount == gameSettings.getCruiserCount() && destroyerCount == gameSettings.getDestroyerCount()){
            AIBoard = AI.genBoard();
            AIBoardView = new Board(gameSettings.getMapWidth(),Tile.HIDDEN);
            if(playerStarts)
                playerMove();
            else
                AIMove();
        }
        else {
            playerBoardGrid = new GridPane();
            for (int x = 0; x < gameSettings.getMapWidth(); x++) {
                for (int y = 0; y < gameSettings.getMapWidth(); y++) {
                    Button butt = new Button();
                    butt.setOnAction(e -> {
                        if (!isPlacing) placeShip(butt);
                    });
                    butt.setStyle(switch (playerBoard.getTile(x, y)) {
                        case HIDDEN -> "-fx-border-color: #000000; -fx-background-color: #000000";
                        case EMPTY -> "-fx-border-color: #000000; -fx-background-color: #0000ff";
                        case SHIP -> "-fx-border-color: #000000; -fx-background-color: #00ff00";
                        case SHOT_SHIP -> "-fx-border-color: #000000; -fx-background-color: #ff0000";
                        case SHIPWRECK -> "-fx-border-color: #000000; -fx-background-color: #a0a0a0";
                    });
                    butt.setPrefHeight(Math.min(50, (int) (900 / gameSettings.getMapWidth())));
                    butt.setPrefWidth(Math.min(50, (int) (900 / gameSettings.getMapWidth())));
                    playerBoardGrid.add(butt, x, y);
                }
            }

            shipEditor = new GridPane();

            //Dreadnought button

            Button DreadnoughtButton = new Button("Dreadnought");
            DreadnoughtButton.setOnAction(e -> {
                if (dreadnoughtCount < gameSettings.getDreadnoughtCount()) {
                    selectShip(5);
                    placeShips();
                }
            });
            shipEditor.add(DreadnoughtButton, gameSettings.getMapWidth(), 0);

            Label dreadnoughtLabel = new Label(dreadnoughtCount + "/" + gameSettings.getDreadnoughtCount());
            shipEditor.add(dreadnoughtLabel, gameSettings.getMapWidth() + 1, 0);

            //Cruiser button

            Button cruiserButton = new Button("Cruiser");
            cruiserButton.setOnAction(e -> {
                if (cruiserCount < gameSettings.getCruiserCount()) {
                    selectShip(4);
                    placeShips();
                }
            });
            shipEditor.add(cruiserButton, gameSettings.getMapWidth(), 1);

            Label cruiserLabel = new Label(cruiserCount + "/" + gameSettings.getCruiserCount());
            shipEditor.add(cruiserLabel, gameSettings.getMapWidth() + 1, 1);

            //Destroyer button

            Button destroyerButton = new Button("Destroyer");
            destroyerButton.setOnAction(e -> {
                if (destroyerCount < gameSettings.getDestroyerCount()) {
                    selectShip(2);
                    placeShips();
                }
            });
            shipEditor.add(destroyerButton, gameSettings.getMapWidth(), 2);

            Label destroyerLabel = new Label(destroyerCount + "/" + gameSettings.getDestroyerCount());
            shipEditor.add(destroyerLabel, gameSettings.getMapWidth() + 1, 2);

            shipEditor.add(playerBoardGrid, 0, 0, gameSettings.getMapWidth(), gameSettings.getMapWidth());

            //Rotate button

            Button rotateRightButton = new Button("Rotate clockwise");
            rotateRightButton.setOnAction(e -> {
                if (!isRotating)
                    rotateShip(true);
            });
            shipEditor.add(rotateRightButton, gameSettings.getMapWidth(), 3);

            Button rotateLeftButton = new Button("Rotate anticlockwise");
            rotateLeftButton.setOnAction(e -> {
                if (!isRotating)
                    rotateShip(false);
            });
            shipEditor.add(rotateLeftButton, gameSettings.getMapWidth(), 4);

            //Rotation label
            Label rotationLabel = new Label("rotation: " + selectedShipDirection);
            shipEditor.add(rotationLabel, gameSettings.getMapWidth(), 5);

            scene = new Scene(shipEditor);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        }
    }

    private void buttonPressed(Button butt){
        int x = playerBoardGrid.getColumnIndex(butt);
        int y = playerBoardGrid.getRowIndex(butt);
        isPressing = true;
        if(AIBoardView.getTile(x,y) != Tile.HIDDEN){
            isPressing = false;
            return;
        }
        switch(AIBoard.getTile(x,y)){
            case EMPTY:
                AIBoardView.setTile(x,y,Tile.EMPTY);
                break;
            case SHIP:
                playerPoints += switch (AIBoard.getShip(new Vector2d(x,y)).getLength()){
                    case 5 -> gameSettings.getDreadnoughtPoints();
                    case 4 -> gameSettings.getCruiserPoints();
                    case 2 -> gameSettings.getDestroyerPoints();
                    default -> throw new Error("This ship does not exist.");
                };
                Ship ship = AIBoard.getShip(new Vector2d(x,y));
                ship.hit();
                if(ship.wrecked()) {
                    Vector2d pos = ship.getPosition();
                    do {
                        AIBoardView.setTile(pos.getX(),pos.getY(),Tile.SHIPWRECK);
                        pos = pos.add(ship.getDirection().toUnitVector());
                    } while (!pos.equals(ship.getEnd()));
                    AIBoardView.setTile(pos.getX(), pos.getY(), Tile.SHIPWRECK);
                }
                else
                    AIBoardView.setTile(x,y,Tile.SHOT_SHIP);
                break;
            default:
                throw new Error("This tile does not exist.");
        }
        isPressing = false;
        AIMove();
    }

    private void playerMove(){
        if (!playerStarts)
            turn += 1;
        playerBoardGrid = new GridPane();
        for(int x = 0; x < gameSettings.getMapWidth(); x++){
            for(int y = 0; y < gameSettings.getMapWidth(); y++){
                Button butt = new Button();
                butt.setStyle(switch (playerBoard.getTile(x,y)){
                    case HIDDEN -> "-fx-border-color: #000000; -fx-background-color: #000000";
                    case EMPTY -> "-fx-border-color: #000000; -fx-background-color: #0000ff";
                    case SHIP -> "-fx-border-color: #000000; -fx-background-color: #00ff00";
                    case SHOT_SHIP -> "-fx-border-color: #000000; -fx-background-color: #ff0000";
                    case SHIPWRECK -> "-fx-border-color: #000000; -fx-background-color: #a0a0a0";
                });
                butt.setPrefHeight(Math.min(50,(int)(900/gameSettings.getMapWidth())));
                butt.setPrefWidth(Math.min(50,(int)(900/gameSettings.getMapWidth())));
                playerBoardGrid.add(butt,x,y);
            }
        }

        AIBoardGrid = new GridPane();
        for(int x = 0; x < gameSettings.getMapWidth(); x++){
            for(int y = 0; y < gameSettings.getMapWidth(); y++){
                Button butt = new Button();
                butt.setOnAction(e -> {if(!isPressing)buttonPressed(butt);});
                butt.setStyle(switch (AIBoardView.getTile(x,y)){
                    case HIDDEN -> "-fx-border-color: #000000; -fx-background-color: #000000";
                    case EMPTY -> "-fx-border-color: #000000; -fx-background-color: #0000ff";
                    case SHIP -> "-fx-border-color: #000000; -fx-background-color: #00ff00";
                    case SHOT_SHIP -> "-fx-border-color: #000000; -fx-background-color: #ff0000";
                    case SHIPWRECK -> "-fx-border-color: #000000; -fx-background-color: #a0a0a0";
                });
                butt.setPrefHeight(Math.min(50,(int)(900/gameSettings.getMapWidth())));
                butt.setPrefWidth(Math.min(50,(int)(900/gameSettings.getMapWidth())));
                AIBoardGrid.add(butt,x,y);
            }
        }

        gameGrid = new GridPane();
        gameGrid.add(playerBoardGrid,0,0,gameSettings.getMapWidth(),gameSettings.getMapWidth());
        gameGrid.add(AIBoardGrid,gameSettings.getMapWidth() + 1,0,gameSettings.getMapWidth(),gameSettings.getMapWidth());

        scene = new Scene(gameGrid);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void AIMove(){
        if (playerStarts)
            turn += 1;
        Vector2d pos = AI.shot();
        AI.hit(pos,playerBoard.getTile(pos.getX(),pos.getY()));
        if(playerBoard.getTile(pos.getX(),pos.getY()) == Tile.SHIP){
            Ship ship = playerBoard.getShip(pos);
            ship.hit();
        }
        System.out.println("AI shot " + pos);
        playerMove();
    }
}
