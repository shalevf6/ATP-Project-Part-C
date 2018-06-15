package View;

import javafx.scene.control.Alert;

import java.awt.event.ActionEvent;

public class SaveGame {
    public javafx.scene.control.TextField saveGame;
    public javafx.scene.control.Button btn_save_comfermed;

    public String SaveGame(javafx.event.ActionEvent actionEvent) {
        if (saveGame.getText()!="")
            return saveGame.getText();

        else
            showAlert("Wrong input you slimy fuck!", "the input is empty");
        return saveGame.getText();
    }
    private void showAlert(String title, String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(alertMessage);
        alert.show();
    }
}
