package View;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class SuccessDisplayer extends Canvas implements Displayer {

    public void redraw(Object... objects) {
        if (objects.length ==1 && objects[0] instanceof MyViewController) {
            MyViewController mvc = (MyViewController)objects[0];

            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());

            try {
                String path = System.getProperty("user.dir") + "\\Resources\\images\\" + "success.jpg";

                Image win = new Image(new FileInputStream(path));
                ImageView winGif = new ImageView();
                winGif.setImage(win);
                winGif.setFitHeight(getHeight());
                winGif.setFitWidth(getWidth());

                Pane pane = new Pane();
                Scene scene = new Scene(pane, getWidth(), getHeight());
                scene.getStylesheets().add(getClass().getResource("IdanView.css").toExternalForm());
                Stage newStage = new Stage();
                newStage.setTitle("Success!");
                newStage.setScene(scene);

                Button button = new Button();
                button.setText("New Game");
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        newStage.close();
                        mvc.SetStageNewEvent(event);

                    }
                });

                winGif.setImage(win);
                pane.getChildren().addAll(winGif, button);
                newStage.initOwner(Main.pStage);

                newStage.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ResetZooming(double x,double y)
    {
        setScaleX(x);
        setScaleY(y);
    }
}
