package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

//Template de mensaje del chat
public class CTemplateMensaje {
    @FXML
    private Label mensaje;
    @FXML
    private AnchorPane fondomsg;
    @FXML
    private Label time;

    //Inicialización de atributos
    public void init(String mensaje,String hora){
        this.mensaje.setText(mensaje);
        time.setText(hora);
    }

    //Cambia el color del mensaje cuando es recibido en vez de enviado
    public void setColorRec(){
        fondomsg.setStyle("-fx-border-radius: 15; -fx-background-radius: 15; -fx-border-color: #DFDFDF; -fx-background-color: #4c1e5a;");
    }
}
