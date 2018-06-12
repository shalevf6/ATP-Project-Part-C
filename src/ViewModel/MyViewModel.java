package ViewModel;

import Model.IModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;

    private int characterPositionRowIndex;
    private int getCharacterPositionColumnIndex;

    public StringProperty characterPositionRow = new SimpleStringProperty("1");
    public StringProperty characterPositionColumn = new SimpleStringProperty("1");

    public MyViewModel(IModel model) {
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == model) {
            characterPositionRowIndex = model.getCharacterPositionRow();
            characterPositionRow.set(characterPositionRowIndex + "");
            getCharacterPositionColumnIndex = model.getCharacterPositionColumn();
            characterPositionColumn.set(getCharacterPositionColumnIndex + "");
            setChanged();
            notifyObservers();
        }
    }

    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }

    public int[][] getMaze() {
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return getCharacterPositionColumnIndex;
    }
}
