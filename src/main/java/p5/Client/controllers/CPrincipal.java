package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import p5.Client.Client;
import p5.Client.ClientInterface;
import p5.Server.ServerInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.HashMap;

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
    @FXML
    private Button anhadirAmigo;
    @FXML
    private Label tituloChat;
    private ServerInterface server;
    private Client main;
    private HashMap<String, VBox> chatsAbiertos = new HashMap<>();
    private String userChatActual;


    public void init(ServerInterface server, ArrayList<String> amigos,Client main) throws IOException {
        this.server=server;
        this.main=main;
        actualizarAmigos(amigos);
    }
    public void actualizarAmigos(ArrayList<String> amigos) throws IOException {
        boxAmigos.getChildren().clear();
        for(String s:amigos){
            FXMLLoader loader = main.crearTemp("amigos");
            boxAmigos.getChildren().add(loader.load());
            CTemplateAmigo controller = loader.getController();
            controller.setNick(s);
            // Obtener el botón del template y asignar la acción al pulsar
            controller.getNick().setOnMouseClicked(event -> {
                abrirChat(s);
            });
        }
    }

    @FXML
    private void abrirChat(String amigo) {
        chat.getChildren().clear();
        userChatActual = amigo;
        tituloChat.setText("Chat con " + userChatActual);
        // Comprobar si el chat ya está abierto, en caso contrario, crearlo
        if (!chatsAbiertos.containsKey(amigo)) {
            // Crear un VBox vacío para el chat
            VBox chatActual = new VBox();
            chatActual.setSpacing(10); // Espaciado entre los mensajes
            chatActual.setPadding(new Insets(10));

            chatsAbiertos.put(amigo, chatActual);

            // Mostrar el chat
            chat.getChildren().add(chatActual);
            System.out.println("creando chat con " + amigo);
        } else {
            // Si ya está abierto, mostrar el chat
            System.out.println("recuperando chat con " + amigo);
            VBox chatExistente = chatsAbiertos.get(amigo);
            chat.getChildren().add(chatExistente);

        }
    }

    @FXML
    public void clickSolicitudes() throws SQLException, IOException {
        main.abrirSolicitudes();
    }

    public void enviarMensaje() throws RemoteException {
        if(msg.getText().isEmpty() || userChatActual == null){
            return;
        }
        else{
            chatsAbiertos.get(userChatActual).getChildren().add(new Label(msg.getText()));

            ClientInterface remoto = main.getcRemoto().getAmigosConectadosHM().get(userChatActual);
            remoto.enviarMensaje(msg.getText(), main.getcRemoto().getNombre());
            msg.setText("");
        }
    }
    @FXML
    public void nuevoAmigo() throws SQLException, IOException {
        main.abrirNuevoAmigo();
    }
}
