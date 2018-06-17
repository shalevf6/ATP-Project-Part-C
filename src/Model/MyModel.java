package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import View.Main;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
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
        //threadPool.submit(() -> {
        generateRandomMaze(width, height);
        characterPositionRow = maze.getStartPosition().getRowIndex();
        characterPositionColumn = maze.getStartPosition().getColumnIndex();
        setChanged();
        notifyObservers("MazeDisplayer, PlayerDisplayer");
        //});
    }

    @Override
    public void solveMaze() {
        //threadPool.submit(() -> {
        didWeSolve = true;
        solveMazeSearchProblem();
        setChanged();
        notifyObservers("SolutionDisplayer");
        //});
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
                        solution = (Solution) fromServer.readObject();
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
            if (getCharacterPositionRow() == maze.getGoalPosition().getRowIndex() && getCharacterPositionColumn() == maze.getGoalPosition().getColumnIndex())
                didWeSolve = true;
            else {
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
        }
        setChanged();
        notifyObservers(didWeSolve ? "PlayerDisplayer, SUCCESS" : "PlayerDisplayer");
    }


    public boolean load()
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Game");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Game-Saved-Files", "*.SavedMaze"));
        fc.setInitialDirectory(new File(System.getProperty("user.dir")));

        File file = fc.showOpenDialog(Main.pStage);
        if (file != null){

            try {
                ObjectInputStream gameLoader = new ObjectInputStream(new FileInputStream(file));
                maze = (Maze) gameLoader.readObject();
                characterPositionRow = maze.getStartPosition().getRowIndex();
                characterPositionColumn = maze.getStartPosition().getColumnIndex();
                didWeSolve = false;

                setChanged();
                notifyObservers("MazeDisplayer, SolutionDisplayer, PlayerDisplayer");
                return true;

            }
            catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Could not load The game Ha Ha :(\n Please try again!");
                a.showAndWait();
                return false;
            }

        }
        return false;
    }

    public void exit() {
        stopServers();
        threadPool.shutdown();
    }

    @Override
    public void closeModel() {
        this.exit();
    }

    @Override
    public void saveGame() {


            FileChooser fc = new FileChooser();
            fc.setTitle(" It's Time To Save The Game");
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Game-Saved-Files", "*.SavedMaze"));
            fc.setInitialDirectory(new File(System.getProperty("user.dir")));

            File file_to_save = fc.showSaveDialog(Main.pStage);
            if (file_to_save != null){
                boolean if_OK = false;
                try{
                    ObjectOutputStream Saver = new ObjectOutputStream(new FileOutputStream(file_to_save));
                    Saver.flush();
                    Saver.writeObject(maze);
                    Saver.flush();
                    Saver.close();
                    if_OK = true;

                } catch (Exception e){
                    if_OK = false;
                }
                finally {
                    Alert status = new Alert(if_OK ? Alert.AlertType.CONFIRMATION : Alert.AlertType.ERROR);
                    status.setContentText(if_OK ? "Game Saved!" : " Don't Know why, But Could not save game :(\n Please try again!");
                    status.showAndWait();
                }

            }

        }

    @Override
    public void ChangeProperties(String chosenAlgo, String chosenMaze, String num_of_thredes) {
        int Thredes=Integer.parseInt(num_of_thredes);
        boolean GoodInput = true;
        if(Thredes<=0 || chosenAlgo==null||chosenMaze==null||num_of_thredes==null){
            GoodInput=false;
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Bad Input  :(\n Please try again!");
            a.showAndWait();
        }
        if(GoodInput) {
            try {

                File file = new File("./Resources/config.properties");
                String first = new String("searchingAlgorithm=" + chosenAlgo+"\n");
                String second = new String("mazeGenerator=" + chosenMaze+"\n");
                String third = new String("threadPoolSize=" + num_of_thredes);
                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                } else {

                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(first);
                bw.write(second);
                bw.write(third);
                bw.close();

                System.out.println("Done");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
