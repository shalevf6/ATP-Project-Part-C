package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class MazeDisplayer extends Canvas implements Displayer {

    private int[][] maze;
    private Position goalPosition;
    private Position startPosition;
    //region Properties

    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameGoal = new SimpleStringProperty();
    private StringProperty ImageFileNameStart = new SimpleStringProperty();

    public void setMaze(int[][] maze, Position startPosition, Position goalPosition) {
        this.maze = maze;
        this.startPosition = startPosition;
        this.goalPosition = goalPosition;
        redraw();
    }

    public void redraw(Object... objects) {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.length;
            double cellWidth = canvasWidth / maze[0].length;

            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image goalImage = new Image(new FileInputStream(ImageFileNameGoal.get()));
                Image startImage = new Image(new FileInputStream(ImageFileNameStart.get()));

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < maze.length; i++) {
                    for (int j = 0; j < maze[i].length; j++) {
                        if (maze[i][j] == 1) {
                            // gc.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                            gc.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }
                // setting a frame for the canvas
                int frameThickness = 6;
                gc.setFill(Color.BLACK);
                gc.fillRect(0,0,frameThickness,getHeight());
                gc.fillRect(0,0,getWidth(),frameThickness);
                gc.fillRect(getWidth() - frameThickness,0,frameThickness,getHeight());
                gc.fillRect(0,getHeight() - frameThickness,getWidth(),frameThickness);
                //Draw Start & Goal Positions
                gc.drawImage(goalImage, goalPosition.getColumnIndex() * cellWidth, goalPosition.getRowIndex() * cellHeight, cellWidth, cellHeight);
                gc.drawImage(startImage, startPosition.getColumnIndex() * cellWidth, startPosition.getRowIndex() * cellHeight, cellWidth, cellHeight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void ResetZooming(double x,double y)
    {
        setScaleX(x);
        setScaleY(y);
        redraw();
    }
    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameGoal() {
        return ImageFileNameGoal.get();
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.ImageFileNameGoal.set(imageFileNameGoal);
    }

    public String getImageFileNameStart() {
        return ImageFileNameStart.get();
    }

    public void setImageFileNameStart(String imageFileNameStart) {
        this.ImageFileNameStart.set(imageFileNameStart);
    }

}
