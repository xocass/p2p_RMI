package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import p5.Client.Client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class CTemplateSolicitud {
    @FXML
    private Label nick;
    private CSolicitudes parent;

    public void init(String nick, CSolicitudes parent){
        this.nick.setText(nick);
        this.parent=parent;
    }

    public Label getNick() {
        return nick;
    }

    @FXML
    public void aceptar() throws SQLException, IOException {
        parent.getServer().aceptarSolicitud(nick.getText(),parent.getMain().getcRemoto().getNombre());
        parent.getNicks().remove(nick.getText());
        parent.actualizarListaSolicitudes();
        parent.getMain().getServer().notificarConexion(nick.getText());
        parent.getMain().getServer().notificarConexion(parent.getMain().getcRemoto().getNombre());
    }
    @FXML
    public void rechazar() throws SQLException, IOException {
        parent.getServer().rechazarSolicitud(nick.getText(),parent.getMain().getcRemoto().getNombre());
        parent.getNicks().remove(nick.getText());
        parent.actualizarListaSolicitudes();
    }
}
