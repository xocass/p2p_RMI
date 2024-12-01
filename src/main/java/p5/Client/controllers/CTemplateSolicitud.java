package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.SQLException;

//Clase que controla el template de los elementos de la lista de solicitudes
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

    //Se acepta la solicitud de amistad, se almacena en la base de datos y se notifica al usuario que envió la solicitud
    @FXML
    public void aceptar() throws SQLException, IOException {
        parent.getServer().aceptarSolicitud(nick.getText(),parent.getMain().getcRemoto().getNombre());
        parent.getNicks().remove(nick.getText());
        parent.actualizarListaSolicitudes();
        parent.getMain().getServer().notificarUsuario(parent.getMain().getcRemoto().getNombre(),nick.getText());
    }

    //Se rechaza la solicitud de amistad y se elimina la solicitud de la base de datos
    @FXML
    public void rechazar() throws SQLException, IOException {
        parent.getServer().rechazarSolicitud(nick.getText(),parent.getMain().getcRemoto().getNombre());
        parent.getNicks().remove(nick.getText());
        parent.actualizarListaSolicitudes();
    }
}
