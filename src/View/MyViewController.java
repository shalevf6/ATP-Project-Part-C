package View;

import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

/**
 * Represents the View of the Game
 */
public class MyViewController implements Observer, IView {

    public StackPane pane;
    public MazeDisplayer mazeDisplayer;
    public SolutionDisplayer solutionDisplayer;
    public PlayerDisplayer playerDisplayer;
    public SuccessDisplayer successDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_Play;
    public javafx.scene.control.Button btn_Pause;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.Button btn_ResetZoom;
    public BorderPane borP;
    public StackPane ST;
    public VBox leftM;

    private MyViewModel viewModel;
    private boolean solved = false;
    private boolean firstMazeCreated = false;
    private double originalMazeScaleX;
    private double originalMazeScaleY;
    private double mazeDisX;
    private double mazeDisY;
    private static Media gameSong = new Media(new File("./Resources/music/gameSong.mpeg").toURI().toString());
    private static Media successSong = new Media(new File("./Resources/music/successSong.mpeg").toURI().toString());
    private static MediaPlayer gamePlayer = new MediaPlayer(gameSong);
    public static MediaPlayer successPlayer = new MediaPlayer(successSong);

    private StringProperty characterPositionRow = new SimpleStringProperty();
    private StringProperty characterPositionColumn = new SimpleStringProperty();

    /**
     * Sets a given ViewModel to this View, binds the properties of the Character's position labels to the actual position
     * on the maze and sets the volume of the success and inGame players
     * @param viewModel - a given ViewModel
     */
    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
        gamePlayer.setVolume(1);
        successPlayer.setVolume(1);
    }

    /**
     * Binds the properties of the Character's position labels to the actual position on the maze
     * @param viewModel - a given ViewModel
     */
    private void bindProperties(MyViewModel viewModel) {
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);

    }

    @Override
    public void update(Observable o, Object arg) {
        String displayer = (String) arg;
        // Displays the Maze
        if (o == viewModel && displayer.contains("MazeDisplayer")) {
            displayMaze(viewModel.getMaze());
            btn_solveMaze.setDisable(false);
            btn_Pause.setDisable(false);
            btn_Play.setDisable(true);
        }

        // Displays the Solution
        if (o == viewModel && displayer.contains("SolutionDisplayer") && !displayer.contains("SUCCESS")) {
            GraphicsContext gc = playerDisplayer.getGraphicsContext2D();
            gc.clearRect(0,0,playerDisplayer.getWidth(),playerDisplayer.getHeight());
            displaySolution(viewModel.getMaze());
        }
        else
            // Displays the player
            if (o == viewModel && displayer.contains("PlayerDisplayer"))
                displayPlayer(viewModel.getMaze());

        // Displays the Success window
        if (o == viewModel && displayer.contains("SUCCESS")) {
            // implement success scenario
            solved = true;
            GraphicsContext gc = playerDisplayer.getGraphicsContext2D();
            gc.clearRect(0,0,playerDisplayer.getWidth(),playerDisplayer.getHeight());
            gamePlayer.pause();
            successPlayer.play();
            successDisplayer.redraw(this);
        }
    }

    @Override
    public void displaySolution(int[][] maze) {
        mazeDisplayer.setMaze(maze,viewModel.getStartPosition(),viewModel.getGoalPosition());
        solutionDisplayer.setSolution(maze, viewModel.getSolution());
    }

    @Override
    public void displayPlayer(int[][] maze) {
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        this.characterPositionRow.set(String.valueOf(characterPositionRow));
        this.characterPositionColumn.set(String.valueOf(characterPositionColumn));
        playerDisplayer.setPlayer(maze,characterPositionRow,characterPositionColumn);
    }

    @Override
    public void displayMaze(int[][] maze) {
        mazeDisplayer.setMaze(maze, viewModel.getStartPosition(), viewModel.getGoalPosition());
    }

    /**
     * Generates a new Maze. if the given row and column numbers are ilegal, generates a 10X10 maze instead
     */
    public void generateMaze() {
        if(!firstMazeCreated)
            firstMazeCreated = true;
        solved = false;
        GraphicsContext gc = solutionDisplayer.getGraphicsContext2D();
        gc.clearRect(0,0,solutionDisplayer.getWidth(),solutionDisplayer.getHeight());
        int height = Integer.valueOf(txtfld_rowsNum.getText());
        int width = Integer.valueOf(txtfld_columnsNum.getText());
        btn_generateMaze.setDisable(true);
        if (height <= 0 || width <= 0 || (height == 1 && width == 1)) {
            showWrongInputAlert();
            viewModel.generateMaze(10,10);
        }
        else
            viewModel.generateMaze(width, height);
        btn_generateMaze.setDisable(false);
        btn_solveMaze.setDisable(false);
        originalMazeScaleX = mazeDisplayer.getScaleX();
        originalMazeScaleY = mazeDisplayer.getScaleY();
        btn_Play.setDisable(false);
        btn_Pause.setDisable(true);
    }

    /**
     * Raising a wrong input alert
     */
    private void showWrongInputAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Wrong input you slimy fuck!");
        alert.setContentText("Generating a 10X10 maze as default instead..");
        alert.show();
    }

    /**
     * Handles a KeyEvent by moving it to the ViewModel
     * @param keyEvent - a given KeyEvent
     */
    public void KeyPressed(KeyEvent keyEvent) {
        if (!solved) {
            viewModel.moveCharacter(keyEvent.getCode());
            keyEvent.consume();
        }
    }

    /**
     * Handles Resizing event to a given scene
     * @param scene - a given scene
     */
    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mazeDisX = newSceneWidth.doubleValue() - borP.getLeft().getLayoutBounds().getWidth();
                borP.setPrefWidth(newSceneWidth.doubleValue());
                pane.setMaxSize(mazeDisX,mazeDisY);
                mazeDisplayer.redraw();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mazeDisY = newSceneHeight.doubleValue();
                borP.setPrefHeight(mazeDisY - borP.getTop().getLayoutBounds().getHeight());
                mazeDisplayer.setLayoutY(mazeDisY - pane.getScaleY());
                mazeDisplayer.redraw();
            }
        });
    }

    /**
     * Raising the about window when "about" is pushed
     * @param actionEvent - the event raised when "about" is pushed
     */
    public void About(ActionEvent actionEvent) {
        Stage stage = new Stage();
        stage.setWidth(435);
        stage.setHeight(165);
        stage.setTitle("About this Game");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        Label firstLine = new Label("This game was created by three magnificent individuals that");
        GridPane.setConstraints(firstLine, 0, 0);
        Label secondLine = new Label(" tried to find out the purpose of life and found it by solving");
        GridPane.setConstraints(secondLine, 0, 1);
        Label thiredLable = new Label(" this maze! now it's you're time to shine and solve it as well.");
        GridPane.setConstraints(thiredLable, 0, 2);
        Label forthLable = new Label("Good luck!");
        GridPane.setConstraints(forthLable, 0, 3);
        grid.getChildren().addAll(firstLine, secondLine, thiredLable, forthLable);
        Scene scene = new Scene(grid, 445, 130);
        scene.getStylesheets().add(getClass().getResource("IdanView.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Raising the game rules window when "game rules" is pushed
     * @param actionEvent - the event raised when "game rules" is pushed
     */
    public void GameRules(ActionEvent actionEvent){
        Stage stage = new Stage();
        stage.setWidth(490);
        stage.setHeight(195);
        stage.setTitle("Game Rules");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        Label firstLine = new Label("You are Kratos, a respected soldier and General that would ascend");
        GridPane.setConstraints(firstLine, 0, 0);
        Label secondLine = new Label(" to Godhood before exacting revenge against the Olympians who");
        GridPane.setConstraints(secondLine, 0, 1);
        Label thiredLable = new Label(" betrayed him. In order to achieve that, you must reach Zeus while");
        GridPane.setConstraints(thiredLable, 0, 2);
        Label fourthLabel = new Label("avoiding Meduza, Queen of the Gorgons. Can you help Kratos achieve");
        GridPane.setConstraints(fourthLabel, 0, 3);
        Label fifthhLabel = new Label("his goal?");
        GridPane.setConstraints(fifthhLabel, 0, 4);
        grid.getChildren().addAll(firstLine, secondLine, thiredLable, fourthLabel, fifthhLabel);
        Scene scene = new Scene(grid, 335, 140);
        scene.getStylesheets().add(getClass().getResource("IdanView.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Exits the game window when "exit" is pushed
     * @param actionEvent - the event raised when "exit" is pushed
     */
    public void Exit(ActionEvent actionEvent) {
        successPlayer.stop();
        gamePlayer.pause();
        exitProject();
    }

    /**
     * Pops a Window which asks if you want to start a new game when "new" is pushed
     * @param actionEvent - the event raised when "new" is pushed
     */
    public void SetStageNewEvent(ActionEvent actionEvent) {
        if (solved) {
            solved = false;
            successPlayer.stop();
        }
        Alert alertExit = new Alert(Alert.AlertType.NONE);
        ButtonType newGame = new ButtonType(" Hell yeah! Start fresh", ButtonBar.ButtonData.YES);
        ButtonType NoExitbtn = new ButtonType("No No I'm going to win this one! ", ButtonBar.ButtonData.NO);
        alertExit.getButtonTypes().setAll(NoExitbtn,newGame);
        alertExit.setContentText("Are you really really really sure you want to Start a New Game??");
        Optional<ButtonType> result = alertExit.showAndWait();
        if (result.get() == newGame){
            this.generateMaze();
        } else {
            alertExit.close();
            exitProject();
        }
    }

    /**
     * Makes sure the user realy wants to exit and exits if that's the case
     */
    private void exitProject() {
        Alert alertExit = new Alert(Alert.AlertType.NONE);
        ButtonType Exitbtn = new ButtonType("Exit For Life", ButtonBar.ButtonData.YES);
        ButtonType NoExitbtn = new ButtonType("Stay Here Forever", ButtonBar.ButtonData.NO);

        alertExit.getButtonTypes().setAll(NoExitbtn,Exitbtn);
        alertExit.setContentText("Are you really really really sure you want to exit??");
        Optional<ButtonType> result = alertExit.showAndWait();
        if (result.get() == Exitbtn){
            viewModel.closeModel();
            Platform.exit();
        } else {
            alertExit.close();
        }
    }

    /**
     * Moving the player through mouse dragging
     * @param me - the event raised when the player is being dragged by the mouse
     */
    public void mouseDragging (MouseEvent me)
    {
        if (mazeDisplayer != null)
        {
            double xMousePos = (me.getX() / (mazeDisplayer.getWidth() / viewModel.getMaze()[0].length));
            double yMousePos = (me.getY() / (mazeDisplayer.getHeight() / viewModel.getMaze().length));

            if (!solved)
            {
                if (yMousePos < viewModel.getCharacterPositionRow())
                    viewModel.moveCharacter(KeyCode.UP);
                if (yMousePos > viewModel.getCharacterPositionRow())
                    viewModel.moveCharacter(KeyCode.DOWN);
                if (xMousePos < viewModel.getCharacterPositionColumn())
                    viewModel.moveCharacter(KeyCode.LEFT);
                if (xMousePos > viewModel.getCharacterPositionColumn())
                    viewModel.moveCharacter(KeyCode.RIGHT);
            }
        }
    }

    /**
     * Loads a new game when "load" is pushed
     * @param actionEvent - the event raised when "load" is pushed
     */
    public void loadGame(ActionEvent actionEvent) {
        if (viewModel.load()) {
            btn_solveMaze.setDisable(false);
            if (solved) {
                solved = false;
                successPlayer.stop();
            }
        }
        actionEvent.consume();
    }

    /**
     * Solves the current maze when "sSolve Maze" is pushed
     * @param actionEvent - the event raised when "Solve Maze" is pushed
     */
    public void solveMaze(ActionEvent actionEvent) {
        showSolveAlert();
        solved = true;
        viewModel.solveMaze();
    }

    /**
     * Pops up an alert when the user clicks on "Solve Maze"
     */
    private void showSolveAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Please wait patiently");
        alert.setContentText("Solving maze...");
        alert.show();
    }

    /**
     * Saves the current Maze when the user clicks on "save"
     */
    public void SaveGame(){
        if(firstMazeCreated)
            viewModel.saveGame();
        else {
            showSaveAlert();
        }
    }

    /**
     * Pops up an alert when the user clicks on "save" and there was no maze generated beforehand
     */
    private void showSaveAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pay Attention");
        alert.setContentText("You haven't generated a maze to save yet!");
        alert.show();
    }

    /**
     * Handles Zooming in and out of the game's window
     * @param scrollEvent - the event raised when the user is Zooming in and out of the game's window
     */
    public void zoomInOut(ScrollEvent scrollEvent) {
        try {
            double zoomFa;
            if (scrollEvent.isControlDown()) {
                zoomFa = 1.1;
                double deltaY = scrollEvent.getDeltaY();
                if (deltaY < 0) {
                    zoomFa = 2.0 - zoomFa;
                }
                mazeDisplayer.setScaleX(mazeDisplayer.getScaleX()*zoomFa);
                mazeDisplayer.setScaleY(mazeDisplayer.getScaleY()*zoomFa);
                playerDisplayer.setScaleX(playerDisplayer.getScaleX()*zoomFa);
                playerDisplayer.setScaleY(playerDisplayer.getScaleY()*zoomFa);
                solutionDisplayer.setScaleX(solutionDisplayer.getScaleX()*zoomFa);
                solutionDisplayer.setScaleY(solutionDisplayer.getScaleY()*zoomFa);
                scrollEvent.consume();
            }
        } catch (NullPointerException e) {
            scrollEvent.consume();
        }
    }

    /**
     * Resets the zoom to the initial default when the user clicks on "reset zoom"
     */
    public void ResetZoom (){
        mazeDisplayer.ResetZooming(originalMazeScaleX,originalMazeScaleY);
        if(solved)
            solutionDisplayer.ResetZooming(originalMazeScaleX,originalMazeScaleY);
        playerDisplayer.ResetZooming(originalMazeScaleX,originalMazeScaleY);
    }

    /**
     * Opens and changes the properties when the user clicks on "properties"
     * @param actionEvent - the event raised when the user clicks on "properties"
     */
    public void Properties(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Properties");
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10, 10, 10, 10));
            grid.setVgap(8);
            grid.setHgap(10);
            Label firstLine = new Label("Algorithm");
            GridPane.setConstraints(firstLine, 0, 0);
            ChoiceBox<String> Algo =new ChoiceBox<>();
            Algo.getItems().addAll("BFS","DFS","BestFirstSearch");
            GridPane.setConstraints(Algo,0,1);
            ChoiceBox<String> MazeType = new ChoiceBox<>();
            MazeType.getItems().addAll("myMazeGenerator","simpleMazeGenerator");
            TextField NUM_OF_THREDES= new TextField("4");
            Label label2 = new Label("MAZE TYPE");
            GridPane.setConstraints(label2,1,0);
            GridPane.setConstraints(MazeType,1,1);
            Label label3 = new Label("NUMBER OF THREADS");
            GridPane.setConstraints(label3,2,0);
            GridPane.setConstraints(NUM_OF_THREDES,2,1);
            Button Save = new Button("Save");
            GridPane.setConstraints(Save,1,4);
            Save.setOnAction(event -> getChoice(Algo,MazeType,NUM_OF_THREDES,stage));
            grid.getChildren().addAll(firstLine,Algo,label2,MazeType,label3,NUM_OF_THREDES,Save);
            Scene scene = new Scene(grid,370,150);
            scene.getStylesheets().add(getClass().getResource("IdanView.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (NullPointerException e) {
            actionEvent.consume();
        }
    }

    /**
     * Gets the user's choices of properties after he clicked on "properties"
     * @param algo - the user's choice for a searching problem solving algorithm
     * @param mazeType - the user's choice for a maze generating algorithm
     * @param num_of_thredes - the user's choice for the number of thread the Servers will run with
     * @param stage - the properties' window
     */
    private void getChoice(ChoiceBox<String> algo, ChoiceBox<String> mazeType, TextField num_of_thredes ,Stage stage) {
        String ChosenAlgo = algo.getValue();
        String ChosenMaze = mazeType.getValue();
        String Num_Of_thredes = num_of_thredes.getText();
        viewModel.ChangeProperties(ChosenAlgo,ChosenMaze,Num_Of_thredes);
        stage.close();
    }

    /**
     * Plays the inGame's music when "Play Music" is pushed
     * @param actionEvent - the event raised when "Play Music" is pushed
     */
    public void PlayMusic(ActionEvent actionEvent) {
        successPlayer.stop();
        gamePlayer.play();
        btn_Play.setDisable(true);
        btn_Pause.setDisable(false);
    }

    /**
     * Pauses the inGame's music when "Pause" is pushed
     * @param actionEvent - the event raised when "Pause" is pushed
     */
    public void Pause(ActionEvent actionEvent) {
        gamePlayer.pause();
        btn_Play.setDisable(false);
        btn_Pause.setDisable(true);
    }

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public StringProperty getCharacterPositionRowProperty() {
        return characterPositionRow;
    }

    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }

    public StringProperty getCharacterPositionColumnProperty() {
        return characterPositionColumn;
    }
}
