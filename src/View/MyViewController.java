package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.*;
import javafx.scene.control.Button;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
    TextField textField_to_save;


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
        if (o == viewModel){
            displayMaze(viewModel.getMaze());
            btn_generateMaze.setDisable(true);
        }
    }

    @Override
    public void displayMaze(int[][] maze) {
        mazeDisplayer.setGoalPosition(viewModel.getGoalPosition());
        mazeDisplayer.setStartPosition(viewModel.getStartPosition());
        mazeDisplayer.setMaze(maze);
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow,characterPositionColumn);
        this.characterPositionRow.set(String.valueOf(characterPositionRow));
        this.characterPositionColumn.set(String.valueOf(characterPositionColumn));
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



    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
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
                System.out.println("Width: " + newSceneWidth);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
            }
        });
    }

    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("AboutController");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 400, 350);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mouseDragging (MouseEvent me)
    {
        System.out.println("mouse move");

        if (mazeDisplayer != null)
        {
            int xMousePos = (int) ((me.getX()) / (mazeDisplayer.getWidth() / (viewModel.getMaze()[0].length)));
            int yMousePos = (int) ((me.getY()) / (mazeDisplayer.getHeight() / (viewModel.getMaze().length)));
            System.out.println(me.getSceneX());
            System.out.println(me.getSceneY());

            if (!viewModel.didFinished())
            {
                if (yMousePos < viewModel.getCharacterPositionRow())
                    viewModel.moveCharacter(KeyCode.NUMPAD8);
                if (yMousePos > viewModel.getCharacterPositionRow())
                    viewModel.moveCharacter(KeyCode.NUMPAD2);
                if (xMousePos < viewModel.getCharacterPositionColumn())
                    viewModel.moveCharacter(KeyCode.NUMPAD4);
                if (xMousePos > viewModel.getCharacterPositionColumn())
                    viewModel.moveCharacter(KeyCode.NUMPAD6);
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

    public void SetStageNewEvent(ActionEvent actionEvent) {

    }





    public void loadGame(ActionEvent actionEvent) {




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
            click.setOnAction(event ->{if(!textField_to_save.equals(""))
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
            Scene scene = new Scene(layout, 400, 350);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes

            stage.showAndWait();
            while(textField_to_save.getText().equals(""))
            stage.showAndWait();
            String ans =textField_to_save.getText();

            viewModel.save(ans);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handleTosave(ActionEvent actionEvent){
        String res="";
        res=textField_to_save.getText();
        if(res.equals(""))
            showAlert("Wrong input you slimy fuck!", "the input is empty");
        else {
        }
        return ;

    }

}
