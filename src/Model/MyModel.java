package Model;

import Client.IClientStrategy;
import Client.Client;
import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyModel extends Observable implements IModel {

    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;
    private int characterPositionRow;
    private int characterPositionColumn;
    private boolean didWeSolve;
    private Solution solution;

    private Maze maze;

    public MyModel() {
        // Raise the servers
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        didWeSolve = false;
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
        didWeSolve = false;
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
            didWeSolve = true;
        setChanged();
        notifyObservers();
    }
    public void load(File chosen)
    {
        try {
            ObjectInputStream readFromFile = new ObjectInputStream(new FileInputStream(chosen));
            maze = new Maze((byte[])(readFromFile.readObject()));
            setChanged();
            notifyObservers();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save(String name)
    {

        File savedMaze = new File("C:\\Users\\עידן\\IdeaProjects\\ATP-Project-Part-C\\src\\savedMazes",name);
        boolean fileExists = savedMaze.exists();
        if(savedMaze.exists()){
            Alert alert = new Alert(Alert.AlertType.WARNING,"", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Warning!");  //warning box title
            alert.setHeaderText("WARNING!!!");// Header
            alert.setContentText("File already exists. Overwrite?"); //Discription of warning
            alert.getDialogPane().setPrefSize(200, 100); //sets size of alert box
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.YES){
                fileExists=false;
            } else {
             return;
            }

        }

        if(!fileExists) {
            try {
                ObjectOutputStream writeToFile = new ObjectOutputStream(new FileOutputStream(savedMaze));
                writeToFile.writeObject(maze.toByteArray());
                writeToFile.flush();
                writeToFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exit() {
        stopServers();
        threadPool.shutdown();
    }

    @Override
    public boolean getIfFinish()
    {
        return didWeSolve;
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

    public Position getGoalPosition() {
        return maze.getGoalPosition();
    }

    @Override
    public Position getStartPosition() {
        return maze.getStartPosition();
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

}
