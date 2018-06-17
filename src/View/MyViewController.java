package View;

import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Random;

public class MyViewController implements Observer, IView {

    private MyViewModel viewModel;
    private boolean solved = false;
    public MazeDisplayer mazeDisplayer;
    public SolutionDisplayer solutionDisplayer;
    public PlayerDisplayer playerDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.TextField saveGame;
    public javafx.scene.control.Button btn_ResetZoom;
    public javafx.scene.control.Button btn_save_comfermed;
    private TextField textField_to_save;
    public BorderPane BRG;
    private double mazeDispX;
    private double mazeDispY;
    private double mazeOrX;
    private double mazeOry;

    public StringProperty characterPositionRow = new SimpleStringProperty();
    public StringProperty characterPositionColumn = new SimpleStringProperty();

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
            GraphicsContext gc = playerDisplayer.getGraphicsContext2D();
            gc.clearRect(0,0,playerDisplayer.getWidth(),playerDisplayer.getHeight());
            displaySuccess();
            // implement success scenario
        }
        //});
    }

    private void displaySuccess() {
        Canvas successDisplay = new Canvas();
        GraphicsContext gc = successDisplay.getGraphicsContext2D();
        gc.clearRect(0,0, successDisplay.getWidth(), successDisplay.getHeight());

        try {
            Random rand = new Random();
            int num = rand.nextInt(3);

            String path =System.getProperty("user.dir") + "\\Resources\\images\\" + "success.gif";

            Image win = new Image(new FileInputStream(path));
            ImageView winGif =new ImageView( );
            winGif.setImage(win);
            winGif.setFitHeight(successDisplay.getHeight());
            winGif.setFitWidth(successDisplay.getWidth());



            Pane pane = new Pane();
            Scene scene = new Scene(pane, successDisplay.getWidth(),successDisplay.getHeight());
            Stage newStage = new Stage();
            newStage.setTitle("You did it!");
            newStage.setScene(scene);

            Button button = new Button();
            button.setText("Let me play again!");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    newStage.close();
                    event.consume();
                }
            });

            winGif.setImage(win);
            pane.getChildren().addAll(winGif, button);
            newStage.initOwner(Main.pStage);

            newStage.showAndWait();;

        } catch (Exception e) {
            e.printStackTrace();
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
        playerDisplayer.setPlayer(maze,characterPositionRow,characterPositionColumn);
        this.characterPositionRow.set(String.valueOf(characterPositionRow));
        this.characterPositionColumn.set(String.valueOf(characterPositionColumn));
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
            showAlert("Wrong input you slimy fuck!", "Generating a 10X10 maze as default instead..");
            viewModel.generateMaze(10,10);
        }
        else
            viewModel.generateMaze(width, height);
        btn_generateMaze.setDisable(false);
        btn_solveMaze.setDisable(false);
    }

    public void ResetZoom (){
        mazeDisplayer.ResetZooming(mazeOrX,mazeOry);
    }

    private void showAlert(String title, String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(alertMessage);
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
                BRG.setPrefWidth(newSceneWidth.doubleValue());
                mazeDispX = newSceneWidth.doubleValue();
                mazeDisplayer.setWidth(mazeDispX - 190);
                mazeDisplayer.redraw();

            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                BRG.setPrefHeight(newSceneHeight.doubleValue());
                mazeDispY = newSceneHeight.doubleValue();
                mazeDisplayer.setHeight(mazeDispY - 90);
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

    public void GameRules1(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("GameRulesController");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Game rules.fxml").openStream());
            Scene scene = new Scene(root, 360, 100);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Exit(ActionEvent actionEvent) {
        exitProject();
    }

    public void SetStageNewEvent(ActionEvent actionEvent) {
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
            viewModel.closeModel();
            Platform.exit();
        } else {
            alertExit.close();
        }

    }

    public void mouseDragging (MouseEvent me)
    {
        System.out.println("mouse move");

        if (mazeDisplayer != null)
        {
            int xMousePos = (int) (((me.getX()) / (mazeDisplayer.getWidth() / viewModel.getMaze()[0].length)));
            int yMousePos = (int) (((me.getY()) / (mazeDisplayer.getHeight() / viewModel.getMaze().length)));
            System.out.println(me.getSceneX());
            System.out.println(me.getSceneY());

            if (!viewModel.didFinished())
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
        if (viewModel.load())
            btn_solveMaze.setDisable(false);
        actionEvent.consume();
    }

    public void solveMaze(ActionEvent actionEvent) {
        solved = true;
        viewModel.solveMaze();
    }

    public void SaveGame(){
        viewModel.saveGame();
    }

    public void zoomInOut(ScrollEvent scrollEvent) {
        try {
            viewModel.getMaze();
            AnimatedZoomOperator zoomOp = new AnimatedZoomOperator();
            double zoomFa;
            if (scrollEvent.isControlDown()) {
                zoomFa = 1.4;
                double deltaY = scrollEvent.getDeltaY();
                if (deltaY < 0) {
                    zoomFa = 1 / zoomFa;
                }
                zoomOp.zoom(mazeDisplayer, zoomFa, scrollEvent.getSceneX(), scrollEvent.getSceneY());
                scrollEvent.consume();
            }
        } catch (NullPointerException e) {
            scrollEvent.consume();
        }
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

}
