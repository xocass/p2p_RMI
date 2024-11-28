package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class CTemplateMensaje {
    @FXML
    private Label mensaje;
    @FXML
    private AnchorPane fondomsg;
    @FXML
    private Label time;

    public void init(String mensaje){
        this.mensaje.setText(mensaje);
    }

    public void setColorRec(){
        fondomsg.setStyle("-fx-border-radius: 15; -fx-background-radius: 15; -fx-border-color: #DFDFDF; -fx-background-color: #4c1e5a;");
    }
    public void setTime(String hora){
        time.setText(hora);
    }
}
