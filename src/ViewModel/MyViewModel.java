package ViewModel;

import Model.IModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class        MyViewModel extends Observable implements Observer {

    private IModel model;

    private int characterPositionRowIndex;
    private int CharacterPositionColumnIndex;

    public StringProperty characterPositionRow = new SimpleStringProperty();
    public StringProperty characterPositionColumn = new SimpleStringProperty();


    public MyViewModel(IModel model) {
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == model) {
            characterPositionRowIndex = model.getCharacterPositionRow();
            characterPositionRow.set(String.valueOf(characterPositionRowIndex));
            CharacterPositionColumnIndex = model.getCharacterPositionColumn();
            characterPositionColumn.set(String.valueOf(CharacterPositionColumnIndex));
            setChanged();
            notifyObservers();
        }
    }

    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }

    public void solveMaze() {
        model.solveMaze();
    }

    public void load(File chosen) {
        model.load(chosen);
    }

    public void save(String chosen) {
        model.save(chosen);
    }
    public int[][] getMaze() {
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return CharacterPositionColumnIndex;
    }

    public void moveCharacter(KeyCode movement) {
        model.moveCharacter(movement);
    }
}
