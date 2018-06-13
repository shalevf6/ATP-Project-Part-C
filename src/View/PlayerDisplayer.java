package View;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;

public class PlayerDisplayer extends Canvas implements Displayer {

    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    @Override
    public void redraw() {

    }
}
