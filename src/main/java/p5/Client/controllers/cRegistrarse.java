package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import p5.Server.ServerInterface;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class cRegistrarse {
    private ServerInterface server;
    @FXML
    private TextField nick;
    @FXML
    private PasswordField passwd;
    @FXML
    private Button registrar;

    public void setServer(ServerInterface server){
        this.server=server;
    }
    @FXML
    public void clickRegistrar() throws SQLException, RemoteException {
        server.registrarUsuario(nick.getText(),passwd.getText());
    }
}
