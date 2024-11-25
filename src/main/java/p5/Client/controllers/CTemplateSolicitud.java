package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class CTemplateSolicitud {
    @FXML
    private Label nick;

    public void setNick(String nick){
        this.nick.setText(nick);
    }

    public Label getNick() {
        return nick;
    }
}
