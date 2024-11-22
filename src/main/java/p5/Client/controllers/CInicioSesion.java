package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import p5.Client.Client;
import p5.Client.ClientImpl;
import p5.Client.ClientInterface;
import p5.Server.ServerInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

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

    private Client main;
    private ServerInterface server;
    @FXML
    public void abrirRegistrar() throws IOException {
        main.abrirRegistrar();
    }
    @FXML
    public void clickLogin() throws SQLException, IOException {
        String nick = nickField.getText();
        List<String> amigosCon = server.iniciarSesion(nickField.getText(),pswField.getText());
        if(amigosCon == null){
            noExiste.setVisible(true);
        }else{
            System.out.println(amigosCon);
            ClientImpl cRemoto = new ClientImpl(nick);
            server.registrarCliente(nick, cRemoto);
            System.out.println("Cliente registrado en el servidor central.");

            main.abrirPrincipal(amigosCon);
        }
    }

    public void init(ServerInterface server, Client main){
        this.server=server;
        this.main=main;
    }
}
