package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import p5.Client.Client;
import p5.Client.ClientInterface;
import p5.Server.ServerInterface;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class CInicioSesion {
    @FXML
    private Label noExiste;
    @FXML
    private TextField nickField;
    @FXML
    private TextField pswField;

    private Client main;
    private ServerInterface server;
    @FXML
    public void abrirRegistrar() throws IOException {
        main.abrirRegistrar(1);
    }
    @FXML
    public void cambiarPasswd() throws IOException {
        main.abrirRegistrar(0);
    }

    //Función que verifica las credenciales de un usuario y lo conecta al servidor
    @FXML
    public void clickLogin() throws SQLException, IOException {
        String nick = nickField.getText();
        HashMap<String,ClientInterface> amigosCon = server.iniciarSesion(nickField.getText(),pswField.getText());
        if(amigosCon == null){
            noExiste.setVisible(true);
        }else{
            System.out.println(amigosCon);
            main.crearCliente(nick,amigosCon);
            main.abrirPrincipal();
        }
    }

    public void init(ServerInterface server, Client main){
        this.server=server;
        this.main=main;
    }
}
