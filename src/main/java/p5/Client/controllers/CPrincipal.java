package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import p5.Client.Client;
import p5.Server.ServerInterface;

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

public class CPrincipal {
    @FXML
    private VBox boxAmigos;
    @FXML
    private Button solicitudes;
    @FXML
    private VBox chat;
    @FXML
    private TextField msg;
    @FXML
    private Button enviar;
    private ServerInterface server;
    private Client main;

    @FXML
    public void init(ServerInterface server, Client client, ArrayList<String> amigos,Client main) throws IOException {
        this.server=server;
        this.main=main;
        for(String s:amigos){
            FXMLLoader loader = main.crearTemp();
            boxAmigos.getChildren().add(loader.load());
            CTemplateAmigo controller = loader.getController();
            controller.setNick(s);
        }
    }




}
