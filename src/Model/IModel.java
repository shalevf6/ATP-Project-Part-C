package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;

public interface IModel {
    void generateMaze(int width, int height);
    void solveMaze();
    void moveCharacter(KeyCode movement);
    int[][] getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    boolean load();
    boolean getIfFinish();
    Position getGoalPosition();
    Position getStartPosition();
    Solution getSolution();
    void exit();

    void closeModel();

    void saveGame();

}
