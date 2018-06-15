package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.*;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements Observer, IView {

    private MyViewModel viewModel;
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
    public javafx.scene.control.Button btn_save_comfermed;
    private TextField textField_to_save;


    @FXML
    private BorderPane root;
    private double mazeDispX;
    private double mazeDispY;

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
        Platform.runLater(() -> {
            String displayer = (String) arg;
            if (o == viewModel && displayer.contains("MazeDisplayer")) {
                displayMaze(viewModel.getMaze());
                btn_generateMaze.setDisable(true);
                btn_solveMaze.setDisable(false);
            }
            if (o == viewModel && displayer.contains("PlayerDisplayer")) {
                displayPlayer(viewModel.getMaze());
            }
            if (o == viewModel && displayer.contains("SolutionDisplayer")) {
                displaySolution(viewModel.getMaze());
            }
            if (o == viewModel && displayer.contains("SUCCESS")) {
                // implement success scenario
            }
        });
    }

    @Override
    public void displaySolution(int[][] maze) {
        viewModel.solveMaze();
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



    private void showAlert(String title, String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                root.setPrefWidth(newSceneWidth.doubleValue());
                mazeDispX = newSceneWidth.doubleValue();
                mazeDisplayer.setWidth(mazeDispX);
                mazeDisplayer.redraw();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                root.setPrefHeight(newSceneHeight.doubleValue());
                mazeDispY = newSceneHeight.doubleValue();
                mazeDisplayer.setHeight(mazeDispY);
                mazeDisplayer.redraw();
            }
        });
    }

    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("AboutController");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 380, 130);

            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void GameRules(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("GameRulesController");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Game rules.fxml").openStream());
            Scene scene = new Scene(root, 340, 100);
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
        try {
            Stage stage1 = new Stage();
            Button click=new Button ();
            click.setText("Load");
            textField_to_save=new TextField();
            textField_to_save.setLayoutX(7);
            click.setOnAction(event ->{if(!textField_to_save.toString().equals(""))
                stage1.close();
            else
            {
                showAlert("Wrong input you slimy fuck!", "the input is empty");
            }
            });
            StackPane layout =new StackPane();
            layout.getChildren().add(textField_to_save);
            layout.getChildren().add(click);
            stage1.setTitle("Load Maze");
            Scene scene = new Scene(layout, 170, 170);
            stage1.setScene(scene);
            stage1.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes

            stage1.showAndWait();
            while(textField_to_save.getText().equals("") )
                stage1.showAndWait();
            String ans =textField_to_save.getText();
            viewModel.load(ans);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public void solveMaze(ActionEvent actionEvent) {
    }

    public void SaveGame(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            Button click=new Button ();
            click.setText("Save");
            textField_to_save=new TextField();
            textField_to_save.setLayoutX(7);
            click.setOnAction(event ->{if(!textField_to_save.toString().equals(""))
                stage.close();
            else
                {
                    showAlert("Wrong input you slimy fuck!", "the input is empty");
                }
                });
            StackPane layout =new StackPane();
            layout.getChildren().add(textField_to_save);
            layout.getChildren().add(click);
            stage.setTitle("Save Maze");
            Scene scene = new Scene(layout, 170, 170);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes

            stage.showAndWait();
            while(textField_to_save.getText().equals("") )
                stage.showAndWait();
            String ans =textField_to_save.getText();
            viewModel.save(ans);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void zoomInOut(ScrollEvent scrollEvent) {
        try {
            viewModel.getMaze();
            AnimatedZoomOperator zoomOp = new AnimatedZoomOperator();
            double zoomFa;
            if (scrollEvent.isControlDown()) {
                zoomFa = 1.3;
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

}
