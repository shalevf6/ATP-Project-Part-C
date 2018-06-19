package Model;

import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

/**
 * This is an interface to represent the Model
 */
public interface IModel {

    /**
     * Generates a new Maze
     * @param width - the width of the maze
     * @param height - the height of the maze
     */
    void generateMaze(int width, int height);

    /**
     * Solving the current maze's problem
     */
    void solveMaze();

    /**
     * Moves the character on the Maze
     * @param movement - the KeyCode pressed for the movement
     */
    void moveCharacter(KeyCode movement);

    /**
     * Gets the current Maze
     * @return - the current maze
     */
    int[][] getMaze();

    /**
     * Gets the character's current row's position
     * @return - the character's current row's position
     */
    int getCharacterPositionRow();

    /**
     * Gets the character's current column's position
     * @return - the character's current column's position
     */
    int getCharacterPositionColumn();

    /**
     * Loads a new Maze for the Disk
     * @return - true if the load was successful. else, false
     */
    boolean load();

    /**
     * Gets the current Maze's goal Position
     * @return - he current Maze's goal Position
     */
    Position getGoalPosition();

    /**
     * Gets the current Maze's start Position
     * @return - he current Maze's start Position
     */
    Position getStartPosition();

    /**
     * Gets the current Maze's Solution
     * @return - he current Maze's Solution
     */
    Solution getSolution();

    /**
     * Exits from the game. Stops the servers
     */
    void exit();

    /**
     * Closes the model
     */
    void closeModel();

    /**
     * Saves the current Maze problem
     */
    void saveGame();

    /**
     * Changes the properties
     * @param chosenAlgo - the new chosen search algorithm's name
     * @param chosenMaze - the new chosen Maze generating algorithm's name
     * @param num_of_threads - the new number of threads
     */
    void ChangeProperties(String chosenAlgo, String chosenMaze, String num_of_threads);
}
