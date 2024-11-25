package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import p5.Server.ServerInterface;

import java.io.IOException;
import java.util.ArrayList;

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

    @FXML
    public void init(ArrayList<String> amigos) throws IOException {
        for(String s:amigos){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("p5.client.VTemplateAmigo.fxml"));
            boxAmigos.getChildren().add(loader.load());
            CTemplateAmigo controller = loader.getController();
            controller.setNick(s);
        }
    }

}
