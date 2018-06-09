package View;

import ViewModel.MyViewModel;
import javafx.scene.Scene;

import java.util.Observable;
import java.util.Observer;

public class MyViewController implements Observer, IView {

    private MyViewModel viewModel;
    public MazeDisplayer mazeDisplayer;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
    }

    private void bindProperties(MyViewModel viewModel) {
    }

    @Override
    public void displayMaze(int[][] maze) {

    }

    @Override
    public void update(Observable o, Object arg) {

    }

    public void setResizeEvent(Scene scene) {
    }

}
