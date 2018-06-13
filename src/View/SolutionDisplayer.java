package View;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;

public class SolutionDisplayer extends Canvas implements Displayer {

    private StringProperty ImageFileNameSolutionPath = new SimpleStringProperty();

    public String getImageFileNameSolutionPath() {
        return ImageFileNameSolutionPath.get();
    }

    public void setImageFileNameSolutionPath(String imageFileNameCharacter) {
        this.ImageFileNameSolutionPath.set(imageFileNameCharacter);
    }

    @Override
    public void redraw() {

    }
}
