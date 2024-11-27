package p5.Client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import p5.Client.Client;
import p5.Client.ClientInterface;
import p5.Server.ServerInterface;

import java.io.IOException;
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
    private static int numSolis = 0;
    private static ArrayList<String> solicitudesPendientes = new ArrayList<>();
    private static final HashMap<String, VBox> chatsAbiertos = new HashMap<>();
    private String userChatActual;


    public void init(ServerInterface server, ArrayList<String> amigos,Client main) throws IOException, SQLException {
        this.server=server;
        this.main=main;
        solicitudesPendientes = main.getServer().buscarSolicitudesUsuario(main.getcRemoto().getNombre());
        numSolis = solicitudesPendientes.size();
        solicitudes.setText("Solicitudes (" + numSolis + ")");
        crearListaAmigos(amigos);
    }
    public void crearListaAmigos(ArrayList<String> amigos) throws IOException {
        boxAmigos.getChildren().clear();
        for(String s:amigos){
            // Crear un VBox vacío para el chat
            VBox chatActual = new VBox();
            chatActual.setSpacing(10); // Espaciado entre los mensajes
            chatActual.setPadding(new Insets(10));

            chatsAbiertos.put(s, chatActual);
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

    public void actualizarListaAmigos(String nombre, boolean conectado) throws IOException {
        if(conectado){
            VBox chatActual = new VBox();
            chatActual.setSpacing(10); // Espaciado entre los mensajes
            chatActual.setPadding(new Insets(10));

            chatsAbiertos.put(nombre, chatActual);
        }else{
            chatsAbiertos.remove(nombre);

        }
        boxAmigos.getChildren().clear();
        for(String s: chatsAbiertos.keySet()){
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
            VBox chatActual = chatsAbiertos.get(amigo);
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

    @FXML
    public void enviarMensaje() throws IOException {
        if (msg.getText().isEmpty() || userChatActual == null) {
        return;
    } else {
        // Crear un HBox para contener el mensaje y alinearlo a la derecha
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_RIGHT); // Alinear el contenido a la derecha

        // Cargar el template del mensaje
        FXMLLoader loader = main.crearTemp("mensajes");
        hbox.getChildren().add(loader.load());
        CTemplateMensaje controller = loader.getController();
        controller.init(msg.getText());

        // Añadir el HBox al chat
        chatsAbiertos.get(userChatActual).getChildren().add(hbox);

        // Enviar el mensaje al cliente remoto
        ClientInterface remoto = main.getcRemoto().getAmigosConectadosHM().get(userChatActual);
        remoto.enviarMensaje(msg.getText(), main.getcRemoto().getNombre());
        msg.setText("");
    }
    }

    public void recibirMensaje(String mensaje, String remitente) throws IOException {
        // Crear un HBox para contener el mensaje y alinearlo a la izquierda
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);

        // Cargar el template del mensaje
        FXMLLoader loader = main.crearTemp("mensajes");
        hbox.getChildren().add(loader.load());
        CTemplateMensaje controller = loader.getController();
        controller.init(mensaje);

        // Añadir el HBox al chat
        Platform.runLater(() -> {
            chatsAbiertos.get(remitente).getChildren().add(hbox);
        });
    }

    @FXML
    public void nuevoAmigo() throws SQLException, IOException {
        main.abrirNuevoAmigo();
    }

    public void nuevaSolicitudRecibida(String solicitante) throws IOException {
        solicitudesPendientes.add(solicitante);
        numSolis++;
        Platform.runLater(() -> {
            solicitudes.setText("Solicitudes (" + numSolis + ")");
        });
    }
}
