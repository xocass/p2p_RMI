package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import p5.Client.Client;
import p5.Server.ServerInterface;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

//Ventana donde se listan las solicitudes de amistad
public class CSolicitudes {
    @FXML
    private VBox boxSolicitudes;

    private ServerInterface server;
    private Client main;
    ArrayList<String> nicks;

    //Iniciación de variables
    public void init(ServerInterface server, ArrayList<String> nicks, Client main) throws IOException {
        this.server=server;
        this.main=main;
        this.nicks=nicks;
        actualizarListaSolicitudes();
    }

    //Getters
    public ServerInterface getServer() {
        return server;
    }

    public ArrayList<String> getNicks() {
        return nicks;
    }

    public Client getMain() {
        return main;
    }

    //Actualiza con VBox de la lista de solicitudes
    public void actualizarListaSolicitudes() throws IOException {
        boxSolicitudes.getChildren().clear();
        for (String nick : nicks) {
            FXMLLoader loader = main.crearTemp("solicitudes");
            boxSolicitudes.getChildren().add(loader.load());
            CTemplateSolicitud controller = loader.getController();
            controller.init(nick, this);
        }
    }

    //Función para volver a la ventana principal
    @FXML
    public void clickAtras() throws IOException, SQLException {
        main.abrirPrincipal();
    }

}
