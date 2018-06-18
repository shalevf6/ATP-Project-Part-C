package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SolutionDisplayer extends Canvas implements Displayer {

    private ArrayList<AState> solutionPath;

    private StringProperty ImageFileNameSolutionPath = new SimpleStringProperty();

    @Override
    public void redraw(Object... objects) {
        if (objects.length == 2 && objects[0] instanceof int[][] && objects[1] instanceof Solution) {
            try {
                Image solutionPathImage = new Image(new FileInputStream(ImageFileNameSolutionPath.get()));
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());
                int[][] maze = (int[][]) objects[0];
                double canvasHeight = getHeight();
                double canvasWidth = getWidth();
                double cellHeight = canvasHeight / maze.length;
                double cellWidth = canvasWidth / maze[0].length;
                for (AState state : solutionPath) {
                    MazeState mazeState = (MazeState) state;
                    gc.drawImage(solutionPathImage, mazeState.getPosition().getColumnIndex() * cellWidth,
                            mazeState.getPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);
                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSolution(int[][] maze,Solution solution) {
        this.solutionPath = solution.getSolutionPath();
        redraw(maze, solution);
    }

    public void ResetZooming(double x,double y)
    {
        setScaleX(x);
        setScaleY(y);
        redraw();
    }

    public String getImageFileNameSolutionPath() {
        return ImageFileNameSolutionPath.get();
    }

    public void setImageFileNameSolutionPath(String imageFileNameCharacter) {
        this.ImageFileNameSolutionPath.set(imageFileNameCharacter);
    }
}
