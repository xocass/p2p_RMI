package p5.Client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import p5.Client.Client;
import p5.Client.ClientInterface;
import p5.Server.ServerInterface;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.HashMap;

public class CPrincipal {
    @FXML
    private VBox boxAmigos;
    @FXML
    private ImageView solicitudes;
    @FXML
    private VBox chat;
    @FXML
    private TextField msg;
    @FXML
    private ImageView enviar;
    @FXML
    private ImageView anhadirAmigo;
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
        //if(numSolis>0) solicitudes.setImage(new Image(main.getImageNoti()));
        //solicitudes.setText("Solicitudes (" + numSolis + ")");
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
            configurarDragAndDrop(chatActual);

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
            configurarDragAndDrop(chatActual);
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
        tituloChat.setText(userChatActual);
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
        controller.setColorRec();
        controller.init(mensaje);

        // Añadir el HBox al chat
        Platform.runLater(() -> {
            chatsAbiertos.get(remitente).getChildren().add(hbox);
        });
    }

    public void enviarImagen(File archivo) {
        ClientInterface receptor = main.getcRemoto().getAmigosConectadosHM().get(userChatActual);
        try (FileInputStream fis = new FileInputStream(archivo);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            byte[] imagenBytes = bos.toByteArray();
            receptor.recibirImagen(imagenBytes, archivo.getName(),main.getcRemoto().getNombre());
            //crea un hbox y añade la imagen al chat
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER_RIGHT);
            ByteArrayInputStream bis = new ByteArrayInputStream(imagenBytes);
            Image image = new Image(bis);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            hbox.getChildren().add(imageView);

            Platform.runLater(() -> {
                chatsAbiertos.get(userChatActual).getChildren().add(hbox);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recibirImagen(byte[] imagen, String nombreArchivo,String remitente) throws IOException {
        // Crear un HBox para contener la imagen y alinearlo a la izquierda
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);

        //quiero hacer un imageView para meter en el hbox
        ByteArrayInputStream bis = new ByteArrayInputStream(imagen);
        Image image = new Image(bis);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        hbox.getChildren().add(imageView);

        // Añadir el HBox al chat
        Platform.runLater(() -> {
            chatsAbiertos.get(remitente).getChildren().add(hbox);
        });
    }



    @FXML
    public void nuevoAmigo() throws SQLException, IOException {
        main.abrirNuevoAmigo();
    }

    public void nuevaSolicitudRecibida(String solicitante) {
        solicitudesPendientes.add(solicitante);
        numSolis++;
        Platform.runLater(() -> {
            //solicitudes.setText("Solicitudes (" + numSolis + ")");
        });
    }

    private void configurarDragAndDrop(VBox target) {
        target.setOnDragOver(event -> {
            if (event.getGestureSource() != target && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        target.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                File archivo = db.getFiles().get(0); // Solo tomamos el primer archivo
                enviarImagen(archivo);
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }
}
