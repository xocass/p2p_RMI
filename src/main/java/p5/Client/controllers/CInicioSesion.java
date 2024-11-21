package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import p5.Server.ServerInterface;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class CInicioSesion {
    @FXML
    private Label noExiste;
    @FXML
    private TextField nickField;
    @FXML
    private TextField pswField;
    @FXML
    private Label registrate;
    @FXML
    private Button login;

    private ServerInterface server;
    @FXML
    public void abrirRegistrar(){

    }
    @FXML
    public void clickLogin() throws SQLException, RemoteException {
        server.iniciarSesion(nickField.getText(),pswField.getText());
    }

    public void setServer(ServerInterface server){
        this.server=server;
    }
}
