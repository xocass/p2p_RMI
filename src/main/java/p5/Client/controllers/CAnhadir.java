package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.w3c.dom.Text;
import p5.Client.Client;
import p5.Client.ClientInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class CAnhadir {
    @FXML
    private ImageView atras;
    @FXML
    private Label solicitudEnviada;
    @FXML
    private TextField field;
    @FXML
    private ImageView enviar;
    private Client main;
    private String nick;

    public void init(Client main,String nick){
        this.main=main;
        this.nick=nick;
    }

    public void enviarSoli() throws SQLException, IOException {

        if(field.getText().isEmpty()){
            return;
        }
        if(field.getText().equals(nick)){
            solicitudEnviada.setText("No puedes enviarte una solicitud a ti mismo");
            return;
        }
        int val = main.getServer().anhadirSolicitud(nick,field.getText());
        if(val==1){
            solicitudEnviada.setText("La solicitud fue enviada correctamente");
            //haz que llame al controlador de la ventana principal y actualice la lista de solicitudes de la persona a la que se le envió la solicitud
            ClientInterface amigo = main.getServer().obtenerCliente(field.getText());
            amigo.nuevaSolicitudRecibida(nick);
        }else if(val==2){
            solicitudEnviada.setText("El nombre de usuario no existe");
        }
        else if(val==3){
            solicitudEnviada.setText("El usuario " + field.getText() + " ya es tu amigo");
        }
        else if(val==4){
            solicitudEnviada.setText("Ya has enviado una solicitud a " + field.getText());
        }
    }

    public void atras() throws IOException, SQLException {
        main.abrirPrincipal();
    }

}