package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import p5.Client.Client;
import p5.Server.ServerInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CPrincipal {
    @FXML
    private VBox amigos;
    @FXML
    private Button solicitudes;
    @FXML
    private VBox chat;
    @FXML
    private TextField msg;
    @FXML
    private Button enviar;
    private ServerInterface server;
    private Client client;

    @FXML
    public void init(ServerInterface server, Client client){
        this.server=server;
        this.client=client;
    }


}
