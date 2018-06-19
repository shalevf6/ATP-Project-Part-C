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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements Observer, IView {

    public StackPane pane;
    private MyViewModel viewModel;
    private boolean solved = false;
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
    private double originalMazeScaleX;
    private double originalMazeScaleY;
    private double mazeDisX;
    private double mazeDisY;
    public static MediaPlayer player;
    public static Media song;

    private StringProperty characterPositionRow = new SimpleStringProperty();
    private StringProperty characterPositionColumn = new SimpleStringProperty();


    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
    }

    private void bindProperties(MyViewModel viewModel) {
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);

    }

    @Override
    public void update(Observable o, Object arg) {
        String displayer = (String) arg;
        //Platform.runLater(() -> {
        if (o == viewModel && displayer.contains("MazeDisplayer")) {
            displayMaze(viewModel.getMaze());
            btn_generateMaze.setDisable(true);
            btn_solveMaze.setDisable(false);
            btn_Pause.setDisable(false);
            btn_Play.setDisable(true);
            setSong("./Resources/music/startSong.mp3","start");
        }

        if (o == viewModel && displayer.contains("SolutionDisplayer") && !displayer.contains("SUCCESS")) {
            GraphicsContext gc = playerDisplayer.getGraphicsContext2D();
            gc.clearRect(0,0,playerDisplayer.getWidth(),playerDisplayer.getHeight());
            displaySolution(viewModel.getMaze());
        }
        else
        if (o == viewModel && displayer.contains("PlayerDisplayer"))
            displayPlayer(viewModel.getMaze());

        if (o == viewModel && displayer.contains("SUCCESS")) {
            // implement success scenario
            solved = true;
            GraphicsContext gc = playerDisplayer.getGraphicsContext2D();
            player.pause();
            gc.clearRect(0,0,playerDisplayer.getWidth(),playerDisplayer.getHeight());
            setSong("./Resources/music/successSong.mp3","success");
            successDisplayer.redraw(this);
        }
        //});
    }

    private void setSong(String path, String songPhase) {
        String absolutePath = new File(path).getAbsolutePath();
        if(songPhase.equals("success")) {
            song = new Media(new File(absolutePath).toURI().toString());
            player = new MediaPlayer(song);
            player.setVolume(0.7);
            player.play();
        }
        if(songPhase.equals("start")) {
            song = new Media(new File(absolutePath).toURI().toString());
            player = new MediaPlayer(song);
            player.setVolume(0.5);
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

    public void generateMaze() {

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

    private void showWrongInputAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Wrong input you slimy fuck!");
        alert.setContentText("Generating a 10X10 maze as default instead..");
        alert.show();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        if (!solved) {
            viewModel.moveCharacter(keyEvent.getCode());
            keyEvent.consume();
        }
    }

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

    public void About(ActionEvent actionEvent) {
        Stage stage = new Stage();
        stage.setTitle("About this Game");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        Label firstLine = new Label("This game created by the three magnificent individuals that tried to");
        GridPane.setConstraints(firstLine, 0, 0);
        Label secondLine = new Label("find out the purpose of life and found it by solving this game at the");
        GridPane.setConstraints(secondLine, 0, 1);
        Label thiredLable = new Label("best time they could find! now is you're time");
        GridPane.setConstraints(thiredLable, 0, 2);
        Label forthLable = new Label("to shine and solve it as well, Good luck!");
        GridPane.setConstraints(forthLable, 0, 3);
        grid.getChildren().addAll(firstLine, secondLine, thiredLable, forthLable);
        Scene scene = new Scene(grid, 445, 130);
        scene.getStylesheets().add(getClass().getResource("IdanView.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    public void GameRules(ActionEvent actionEvent){
        Stage stage = new Stage();
        stage.setTitle("Game Rules");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        Label firstLine = new Label("In this game,the player Starts from the Start Point");
        GridPane.setConstraints(firstLine, 0, 0);
        Label secondLine = new Label("and his goal is to get to Goal Point as quick as he");
        GridPane.setConstraints(secondLine, 0, 1);
        Label thiredLable = new Label("can...Good luck!");
        GridPane.setConstraints(thiredLable, 0, 2);
        grid.getChildren().addAll(firstLine, secondLine, thiredLable);
        Scene scene = new Scene(grid, 335, 115);
        scene.getStylesheets().add(getClass().getResource("IdanView.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }


    public void Exit(ActionEvent actionEvent) {
        MyViewController.player.stop();
        exitProject();
    }

    public void SetStageNewEvent(ActionEvent actionEvent) {
        if (solved) {
            solved = false;
            if (player != null)
                player.pause();
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

    public void exitProject() {
        Alert alertExit = new Alert(Alert.AlertType.NONE);
        ButtonType Exitbtn = new ButtonType("Exit For Life", ButtonBar.ButtonData.YES);
        ButtonType NoExitbtn = new ButtonType("Stay Here Forever", ButtonBar.ButtonData.NO);

        alertExit.getButtonTypes().setAll(NoExitbtn,Exitbtn);
        alertExit.setContentText("Are you really really really sure you want to exit??");
        Optional<ButtonType> result = alertExit.showAndWait();
        if (result.get() == Exitbtn){
            MyViewController.player.stop();
            viewModel.closeModel();
            Platform.exit();
        } else {
            alertExit.close();
        }
    }

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

    public void loadGame(ActionEvent actionEvent) {
        if (viewModel.load()) {
            btn_solveMaze.setDisable(false);
            if (solved)
                solved = false;
            if (player != null)
                player.pause();
        }
        actionEvent.consume();
    }

    public void solveMaze(ActionEvent actionEvent) {
        showSolveAlert();
        solved = true;
        viewModel.solveMaze();
    }

    private void showSolveAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Please wait patiently");
        alert.setContentText("Solving maze...");
        alert.show();
    }

    public void SaveGame(){
        viewModel.saveGame();
    }

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

    public void ResetZoom (){
        mazeDisplayer.ResetZooming(originalMazeScaleX,originalMazeScaleY);
        if(solved)
            solutionDisplayer.ResetZooming(originalMazeScaleX,originalMazeScaleY);
        playerDisplayer.ResetZooming(originalMazeScaleX,originalMazeScaleY);
    }

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

    private void getChoice(ChoiceBox<String> algo, ChoiceBox<String> mazeType, TextField num_of_thredes ,Stage stage) {
        String ChosenAlgo = algo.getValue();
        String ChosenMaze = mazeType.getValue();
        String Num_Of_thredes = num_of_thredes.getText();
        viewModel.ChangeProperties(ChosenAlgo,ChosenMaze,Num_Of_thredes);
        stage.close();
    }

    public void PlayMusic(ActionEvent actionEvent) {
        player.play();
        btn_Play.setDisable(true);
        btn_Pause.setDisable(false);
    }

    public void Pause(ActionEvent actionEvent) {

        player.pause();
        btn_Play.setDisable(false);
        btn_Pause.setDisable(true);
    }
}
