package View;

/**
 * An interface to represent the View in the game
 */
public interface IView {

    /**
     * Displays the game's Maze
     * @param maze - the game's maze
     */
    void displayMaze(int[][] maze);

    /**
     * Displays the game's player
     * @param maze - the game's maze
     */
    void displayPlayer(int[][] maze);

    /**
     * Displays the Maze's solution
     * @param maze - the game's maze
     */
    void displaySolution(int[][] maze);
}
