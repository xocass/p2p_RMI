package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CTemplateMensaje {
    @FXML
    private Label mensaje;

    public void init(String mensaje){
        this.mensaje.setText(mensaje);
    }
}
