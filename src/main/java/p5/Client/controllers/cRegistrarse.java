package p5.Client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import p5.Client.Client;
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
    @FXML
    private Label nodisponible;
    private Client main;

    public void setServer(ServerInterface server,Client client){
        this.main = client;
        this.server=server;
    }

    @FXML
    public void clickRegistrar(ActionEvent event) throws SQLException, RemoteException {
        int val = server.registrarUsuario(nick.getText(),passwd.getText());
        System.out.println(val);
        if (val==1){
            try {
                //main
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (val==0){
            nodisponible.setVisible(true);
        }

    }
}
