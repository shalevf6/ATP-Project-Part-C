package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.scene.input.KeyCode;

public interface IModel {
    void generateMaze(int width, int height);
    void solveMaze();
    void moveCharacter(KeyCode movement);
    int[][] getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    boolean getIfFinish();
    Position getGoalPosition();
    Position getStartPosition();
}
