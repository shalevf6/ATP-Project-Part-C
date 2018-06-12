package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import javafx.scene.input.KeyCode;

import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyModel extends Observable implements IModel {

    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private int characterPositionRow;
    private int characterPositionColumn;

    private Maze maze;

    public MyModel() {
        // Raise the servers
    }

    public void startServers() {

    }

    public void stopServers() {

    }

    @Override
    public void generateMaze(int width, int height) {
        //Generate maze
        threadPool.execute(() -> {
            generateRandomMaze(width,height);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers();
        });
    }

    private void generateRandomMaze(int width, int height) {
        MyMazeGenerator mg = new MyMazeGenerator();
        maze = mg.generate(width,height);
        characterPositionRow = maze.getStartPosition().getRowIndex();
        characterPositionColumn = maze.getStartPosition().getColumnIndex();
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        if (maze != null) {
            switch (movement) {
                case DIGIT8:
                    if (maze.isPositionInMaze(new Position(characterPositionRow - 1, characterPositionColumn)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow - 1, characterPositionColumn)))
                        characterPositionRow--;
                    break;
                case DIGIT2:
                    if (maze.isPositionInMaze(new Position(characterPositionRow + 1, characterPositionColumn)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow + 1, characterPositionColumn)))
                        characterPositionRow++;
                    break;
                case DIGIT6:
                    if (maze.isPositionInMaze(new Position(characterPositionRow, characterPositionColumn + 1)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow, characterPositionColumn + 1)))
                        characterPositionColumn++;
                    break;
                case DIGIT4:
                    if (maze.isPositionInMaze(new Position(characterPositionRow, characterPositionColumn - 1)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow, characterPositionColumn - 1)))
                        characterPositionColumn--;
                    break;
                case DIGIT7:
                    if (maze.isPositionInMaze(new Position(characterPositionRow - 1, characterPositionColumn - 1)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow - 1, characterPositionColumn - 1)) &&
                            (!maze.isPositionAWall(new Position(characterPositionRow - 1, characterPositionColumn)) ||
                                    !maze.isPositionAWall(new Position(characterPositionRow, characterPositionColumn - 1)))) {
                        characterPositionRow--;
                        characterPositionColumn--;
                    }
                    break;
                case DIGIT9:
                    if (maze.isPositionInMaze(new Position(characterPositionRow - 1, characterPositionColumn + 1)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow - 1, characterPositionColumn + 1)) &&
                            (!maze.isPositionAWall(new Position(characterPositionRow - 1, characterPositionColumn)) ||
                                    !maze.isPositionAWall(new Position(characterPositionRow, characterPositionColumn + 1)))) {
                        characterPositionRow--;
                        characterPositionColumn++;
                    }
                    break;
                case DIGIT3:
                    if (maze.isPositionInMaze(new Position(characterPositionRow + 1, characterPositionColumn + 1)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow + 1, characterPositionColumn + 1)) &&
                            (!maze.isPositionAWall(new Position(characterPositionRow + 1, characterPositionColumn)) ||
                                    !maze.isPositionAWall(new Position(characterPositionRow, characterPositionColumn + 1)))) {
                        characterPositionRow++;
                        characterPositionColumn++;
                    }
                    break;
                case DIGIT1:
                    if (maze.isPositionInMaze(new Position(characterPositionRow + 1, characterPositionColumn - 1)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow + 1, characterPositionColumn - 1)) &&
                            (!maze.isPositionAWall(new Position(characterPositionRow + 1, characterPositionColumn)) ||
                                    !maze.isPositionAWall(new Position(characterPositionRow, characterPositionColumn - 1)))) {
                        characterPositionRow++;
                        characterPositionColumn--;
                    }
                    break;
            }
        }
        setChanged();
        notifyObservers();
    }

    @Override
    public int[][] getMaze() {
        return maze.getMaze();
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

}
