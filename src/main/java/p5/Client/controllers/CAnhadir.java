package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;
import p5.Client.Client;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class CAnhadir {
    @FXML
    private Label atras;
    @FXML
    private Label solicitudEnviada;
    @FXML
    private TextField field;
    @FXML
    private Button enviar;
    private Client main;
    private String nick;

    public void init(Client main,String nick){
        this.main=main;
        this.nick=nick;
    }

    public void enviarSoli() throws SQLException, RemoteException {

        if(field.getText().isEmpty()){
            return;
        }
        int val = main.getServer().anhadirSolicitud(nick,field.getText());
        if(val==1){
            solicitudEnviada.setText("La solicitud fue enviada correctamente");
        }else if(val==2){
            solicitudEnviada.setText("El nombre de usuario no existe");
        }
        else if(val==3){
            solicitudEnviada.setText("El usuario " + field.getText() + " ya es tu amigo");
        }
    }

}
