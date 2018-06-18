package Model;

import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

public interface IModel {
    void generateMaze(int width, int height);
    void solveMaze();
    void moveCharacter(KeyCode movement);
    int[][] getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    boolean load();
    Position getGoalPosition();
    Position getStartPosition();
    Solution getSolution();
    void exit();
    void closeModel();
    void saveGame();
    void ChangeProperties(String chosenAlgo, String chosenMaze, String num_of_thredes);
}
