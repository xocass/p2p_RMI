package p5.Client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
    private HashMap<String, VBox> chatsAbiertos = new HashMap<>();


    public void init(ServerInterface server, ArrayList<String> amigos,Client main) throws IOException {
        this.server=server;
        this.main=main;
        for(String s:amigos){
            FXMLLoader loader = main.crearTemp();
            boxAmigos.getChildren().add(loader.load());
            CTemplateAmigo controller = loader.getController();
            controller.setNick(s);
        }
    }
    public void actualizarAmigos(ArrayList<String> amigos) throws IOException {
        boxAmigos.getChildren().clear();
        for(String s:amigos){
            FXMLLoader loader = main.crearTemp();
            boxAmigos.getChildren().add(loader.load());
            CTemplateAmigo controller = loader.getController();
            controller.setNick(s);
            // Obtener el botón del template y asignar la acción al pulsar
            controller.getNick().setOnMouseClicked(event -> {
                abrirChat(s);
            });
        }
    }

    private void abrirChat(String amigo) {
        // Comprobar si el chat ya está abierto, en caso contrario, crearlo
        if (!chatsAbiertos.containsKey(amigo)) {
            // Crear un VBox vacío para el chat
            VBox chatActual = new VBox();
            chatActual.setSpacing(10); // Espaciado entre los mensajes
            chatActual.setPadding(new Insets(10));

            // Guardar el chat en un mapa para poder gestionarlo posteriormente
            chatsAbiertos.put(amigo, chatActual);

            // Mostrar el chat en el contenedor principal
            chat.getChildren().add(chatActual);
        } else {
            // Si el chat ya está abierto, solo lo mostramos
            VBox chatExistente = chatsAbiertos.get(amigo);
            if (!chat.getChildren().contains(chatExistente)) {
                chat.getChildren().add(chatExistente);
            }
        }
    }




}
