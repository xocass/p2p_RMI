package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CTemplateAmigo {
    @FXML
    private Label nick;

    @FXML
    public void setNick(String nick){
        this.nick.setText(nick);
    }

    public Label getNick() {
        return nick;
    }
}
