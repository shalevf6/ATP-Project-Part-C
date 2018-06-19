package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;
import java.util.Observable;
import java.util.Observer;

/**
 * Represents the ViewModel of the game
 */
public class MyViewModel extends Observable implements Observer {

    private IModel model;
    private int characterPositionRowIndex;
    private int CharacterPositionColumnIndex;

    public StringProperty characterPositionRow = new SimpleStringProperty();
    public StringProperty characterPositionColumn = new SimpleStringProperty();

    /**
     * The constructor for MyViewModel
     * Sets a given model as its model
     * @param model
     */
    public MyViewModel(IModel model) {
        this.model = model;
    }

    /**
     * Saves the current Maze
     */
    public  void saveGame() {
        model.saveGame();
    }

    /**
     * Closes the model
     */
    public void closeModel() {
        model.closeModel();
    }

    @Override
    public void update(Observable o, Object arg) {
        // Basically passes the arguments given by MyModel to MyViewController
        if (o == model) {
            characterPositionRowIndex = model.getCharacterPositionRow();
            characterPositionRow.set(String.valueOf(characterPositionRowIndex));
            CharacterPositionColumnIndex = model.getCharacterPositionColumn();
            characterPositionColumn.set(String.valueOf(CharacterPositionColumnIndex));
            setChanged();
            notifyObservers(arg);
        }
    }

    /**
     * Generates a new Maze
     * @param width - the maze's given width
     * @param height - the maze's given height
     */
    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }

    /**
     * Solves the current Maze problem
     */
    public void solveMaze() {
        model.solveMaze();
    }

    /**
     * Loads an existing Maze problem
     * @return - true if the load was successful. else, not
     */
    public boolean load() {
       return model.load();
    }

    /**
     * Gets the array that represents the current Maze
     * @return - the array that represents the current Maze
     */
    public int[][] getMaze() {
        return model.getMaze();
    }

    /**
     * Gets the Solution of the current Maze
     * @return - the Solution of the current Maze
     */
    public Solution getSolution() {
        return model.getSolution();
    }

    /**
     * Gets the character's current row position
     * @return - the character's current row position
     */
    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    /**
     * Gets the character's current column position
     * @return - the character's current column position
     */
    public int getCharacterPositionColumn() {
        return CharacterPositionColumnIndex;
    }

    /**
     * Moves the character
     * @param movement - the event raised by pressing the keyboard
     */
    public void moveCharacter(KeyCode movement) {
        model.moveCharacter(movement);
    }

    /**
     * Gets the Maze's goal position
     * @return - the Maze's goal position
     */
    public Position getGoalPosition() {
        return model.getGoalPosition();
    }

    /**
     * Gets the Maze's start position
     * @return - the Maze's start position
     */
    public Position getStartPosition() {
        return model.getStartPosition();
    }

    /**
     * Changes the properties for the servers
     * @param chosenAlgo - a given search solving algorithm's name
     * @param chosenMaze - a given maze generating algorithm's name
     * @param num_of_thredes - given number of thread to run the servers with
     */
    public void ChangeProperties(String chosenAlgo, String chosenMaze, String num_of_thredes) {
        model.ChangeProperties(chosenAlgo,chosenMaze,num_of_thredes);
    }
}
