package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import p5.Client.Client;
import p5.Client.ClientInterface;
import java.io.IOException;
import java.sql.SQLException;

//Controlador de la ventana de añadir amigos
public class CAnhadir {

    @FXML
    private Label solicitudEnviada;
    @FXML
    private TextField field;
    private Client main;
    private String nick;

    //Inciación de los atributos
    public void init(Client main,String nick){
        this.main=main;
        this.nick=nick;
    }

    //Función de enviar solicitud. Coge el valor del TextField y dependiendo del resultado imprime un texto
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
            solicitudEnviada.setStyle("-fx-text-fill: white;");
            solicitudEnviada.setText("La solicitud fue enviada correctamente");
            //haz que llame al controlador de la ventana principal y actualice la lista de solicitudes de la persona a la que se le envió la solicitud
            ClientInterface amigo = main.getServer().obtenerCliente(field.getText());
            if(amigo!=null){
                amigo.nuevaSolicitudRecibida(nick);
            }

        }else if(val==2){
            solicitudEnviada.setStyle("-fx-text-fill: red;");
            solicitudEnviada.setText("El nombre de usuario no existe");
        }
        else if(val==3){
            solicitudEnviada.setStyle("-fx-text-fill: red;");
            solicitudEnviada.setText("El usuario " + field.getText() + " ya es tu amigo");
        }
        else if(val==4){
            solicitudEnviada.setStyle("-fx-text-fill: red;");
            solicitudEnviada.setText("Ya has enviado una solicitud a " + field.getText());
        }
    }

    //Pulsar botón página anterior
    public void atras() throws IOException, SQLException {
        main.abrirPrincipal();
    }

}
