package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import p5.Client.Client;
import p5.Server.ServerInterface;

import java.io.IOException;
import java.util.ArrayList;

public class CSolicitudes {
    @FXML
    private VBox boxSolicitudes;
    private ServerInterface server;
    private Client main;

    public void init(ServerInterface server, ArrayList<String> nicks, Client main) throws IOException {
        this.server=server;
        this.main=main;
        actualizarListaSolicitudes(nicks);
    }

    private void actualizarListaSolicitudes(ArrayList<String> nicks) throws IOException {
        boxSolicitudes.getChildren().clear();
        for(String nick:nicks){
            FXMLLoader loader = main.crearTemp("solicitudes");
            boxSolicitudes.getChildren().add(loader.load());
            CTemplateSolicitud controller = loader.getController();
            controller.setNick(nick);
        }
    }

    @FXML
    public void clickAtras() throws IOException {
        main.abrirPrincipal();
    }
}
