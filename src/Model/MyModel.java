package Model;

import Client.IClientStrategy;
import Client.Client;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyModel extends Observable implements IModel {

    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;
    private int characterPositionRow;
    private int characterPositionColumn;
    private boolean didWeSolved;

    private Maze maze;

    public MyModel() {
        // Raise the servers
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        didWeSolved = false;
    }

    public void startServers() {
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }

    public void stopServers() {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }

    @Override
    public void generateMaze(int width, int height) {
        //Generate maze
        didWeSolved = false;
        threadPool.execute(() -> {
            generateRandomMaze(width,height);
            characterPositionRow = maze.getStartPosition().getRowIndex();
            characterPositionColumn = maze.getStartPosition().getColumnIndex();
            setChanged();
            notifyObservers();
        });
    }

    @Override
    public void solveMaze() {
        threadPool.execute(() -> {
            solveMazeSearchProblem();
            setChanged();
            notifyObservers();
        });
    }

    private void generateRandomMaze(int width, int height) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeSize = new int[]{width, height};
                        toServer.writeObject(mazeSize);
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) (fromServer.readObject());
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[width * height + 6 + 24];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        }
            catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void solveMazeSearchProblem() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze);
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        if (maze != null) {
            switch (movement) {
                case NUMPAD8:
                    if (maze.isPositionInMaze(new Position(characterPositionRow - 1, characterPositionColumn)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow - 1, characterPositionColumn)))
                        characterPositionRow--;
                    break;
                case NUMPAD2:
                    if (maze.isPositionInMaze(new Position(characterPositionRow + 1, characterPositionColumn)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow + 1, characterPositionColumn)))
                        characterPositionRow++;
                    break;
                case NUMPAD6:
                    if (maze.isPositionInMaze(new Position(characterPositionRow, characterPositionColumn + 1)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow, characterPositionColumn + 1)))
                        characterPositionColumn++;
                    break;
                case NUMPAD4:
                    if (maze.isPositionInMaze(new Position(characterPositionRow, characterPositionColumn - 1)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow, characterPositionColumn - 1)))
                        characterPositionColumn--;
                    break;


                 case UP:
                    if (maze.isPositionInMaze(new Position(characterPositionRow - 1, characterPositionColumn)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow - 1, characterPositionColumn)))
                        characterPositionRow--;
                    break;
                case DOWN:
                    if (maze.isPositionInMaze(new Position(characterPositionRow + 1, characterPositionColumn)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow + 1, characterPositionColumn)))
                        characterPositionRow++;
                    break;
                case RIGHT:
                    if (maze.isPositionInMaze(new Position(characterPositionRow, characterPositionColumn + 1)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow, characterPositionColumn + 1)))
                        characterPositionColumn++;
                    break;
                case LEFT:
                    if (maze.isPositionInMaze(new Position(characterPositionRow, characterPositionColumn - 1)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow, characterPositionColumn - 1)))
                        characterPositionColumn--;
                    break;


                case NUMPAD7:
                    if (maze.isPositionInMaze(new Position(characterPositionRow - 1, characterPositionColumn - 1)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow - 1, characterPositionColumn - 1)) &&
                            (!maze.isPositionAWall(new Position(characterPositionRow - 1, characterPositionColumn)) ||
                                    !maze.isPositionAWall(new Position(characterPositionRow, characterPositionColumn - 1)))) {
                        characterPositionRow--;
                        characterPositionColumn--;
                    }
                    break;
                case NUMPAD9:
                    if (maze.isPositionInMaze(new Position(characterPositionRow - 1, characterPositionColumn + 1)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow - 1, characterPositionColumn + 1)) &&
                            (!maze.isPositionAWall(new Position(characterPositionRow - 1, characterPositionColumn)) ||
                                    !maze.isPositionAWall(new Position(characterPositionRow, characterPositionColumn + 1)))) {
                        characterPositionRow--;
                        characterPositionColumn++;
                    }
                    break;
                case NUMPAD3:
                    if (maze.isPositionInMaze(new Position(characterPositionRow + 1, characterPositionColumn + 1)) &&
                            !maze.isPositionAWall(new Position(characterPositionRow + 1, characterPositionColumn + 1)) &&
                            (!maze.isPositionAWall(new Position(characterPositionRow + 1, characterPositionColumn)) ||
                                    !maze.isPositionAWall(new Position(characterPositionRow, characterPositionColumn + 1)))) {
                        characterPositionRow++;
                        characterPositionColumn++;
                    }
                    break;
                case NUMPAD1:
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
        if (getCharacterPositionRow() == maze.getGoalPosition().getRowIndex() && getCharacterPositionColumn() == maze.getGoalPosition().getColumnIndex())
            didWeSolved = true;
        setChanged();
        notifyObservers();
    }

    public boolean getIfFinish()
    {
        return didWeSolved;
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
