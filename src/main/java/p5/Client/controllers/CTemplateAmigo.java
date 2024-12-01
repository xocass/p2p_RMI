package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

//Clase que controla el template de los elementos de la lista de amigos
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
