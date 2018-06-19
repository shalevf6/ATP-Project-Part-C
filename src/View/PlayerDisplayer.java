package View;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * This class represents a Player displayer canvas
 */
public class PlayerDisplayer extends Canvas implements Displayer {

    private int characterPositionRow = 0;
    private int characterPositionColumn = 0;

    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();

    @Override
    public void redraw(Object... objects) {
        if (objects.length == 3 && objects[0] instanceof int[][] && objects[1] instanceof Integer && objects[2] instanceof Integer) {
            try {
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());
                int[][] maze = (int[][]) objects[0];
                double canvasHeight = getHeight();
                double canvasWidth = getWidth();
                double cellHeight = canvasHeight / maze.length;
                double cellWidth = canvasWidth / maze[0].length;
                gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
            }
            catch (FileNotFoundException e) {
                    e.printStackTrace();
            }
        }
    }

    /**
     * Sets the array representing the maze, the character's row Position and the character's column Position
     * @param maze - the array representing the maze
     * @param characterPositionRow - the character's row Position
     * @param characterPositionColumn - the character's column Position
     */
    public void setPlayer(int[][] maze, int characterPositionRow, int characterPositionColumn) {
        this.characterPositionRow = characterPositionRow;
        this.characterPositionColumn = characterPositionColumn;
        redraw(maze, characterPositionRow, characterPositionColumn);
    }

    @Override
    public void ResetZooming(double x,double y)
    {
        setScaleX(x);
        setScaleY(y);
        redraw();
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }
}
