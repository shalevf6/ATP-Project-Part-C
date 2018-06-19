package View;

import Model.*;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.Optional;

public class Main extends Application {

    public static Window pStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        MyModel model = new MyModel();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);

        primaryStage.setTitle("God Of War Maze");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
        Scene scene = new Scene(root, 1350, 900);
        scene.getStylesheets().add(getClass().getResource("MyViewStyle.css").toExternalForm());
        primaryStage.setScene(scene);

        MyViewController view = fxmlLoader.getController();
        view.setResizeEvent(scene);
        view.setViewModel(viewModel);
        viewModel.addObserver(view);

        SetStageCloseEvent(primaryStage);
        primaryStage.show();
    }

    private void SetStageCloseEvent(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alertExit = new Alert(Alert.AlertType.NONE);
                ButtonType Exitbtn = new ButtonType("Exit For Life", ButtonBar.ButtonData.YES);
                ButtonType NoExitbtn = new ButtonType("Stay Here Forever", ButtonBar.ButtonData.NO);
                alertExit.getButtonTypes().setAll(NoExitbtn, Exitbtn);
                alertExit.setContentText("Are you really really really sure you want to exit??");
                Optional<ButtonType> result = alertExit.showAndWait();
                if (result.get() == Exitbtn) {
                    // ... user chose OK
                    // Close program
                } else
                    windowEvent.consume();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
