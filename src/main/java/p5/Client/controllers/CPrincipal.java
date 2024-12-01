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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import p5.Client.Client;
import p5.Client.ClientInterface;

import java.io.*;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.HashMap;

//Ventana principal
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
    private Label tituloChat;
    @FXML
    private ScrollPane scrollPaneChats;
    @FXML
    private ScrollPane ScrollpaneAmigos;

    private Client main;
    private static int numSolis = 0;
    private static ArrayList<String> solicitudesPendientes = new ArrayList<>();
    private static final HashMap<String, VBox> chatsAbiertos = new HashMap<>();
    private String userChatActual;

    //Inicialización de los atributos
    public void init( ArrayList<String> amigos,Client main) throws IOException, SQLException {
        this.main=main;
        solicitudesPendientes = main.getServer().buscarSolicitudesUsuario(main.getcRemoto().getNombre());
        numSolis = solicitudesPendientes.size();
        if(numSolis>0) solicitudes.setImage(main.getImageNoti());
        crearListaAmigos(amigos);
        scrollPaneChats.setFitToWidth(true);
        scrollPaneChats.setFitToHeight(true);
        ScrollpaneAmigos.setFitToWidth(true);
        ScrollpaneAmigos.setFitToHeight(true);
        VBox.setVgrow(boxAmigos, Priority.ALWAYS);
        configurarDragAndDrop(scrollPaneChats);
    }

    //Recorre la lista de amigos y coloca en el VBox de amigos los que están conectados
    public void crearListaAmigos(ArrayList<String> amigos) throws IOException {
        boxAmigos.getChildren().clear();
        for(String s:amigos){
            if(!chatsAbiertos.containsKey(s)){
                VBox chatActual = new VBox();
                chatActual.setSpacing(10); // Espaciado entre los mensajes
                chatActual.setPadding(new Insets(10));
                VBox.setVgrow(chatActual, Priority.ALWAYS);

                chatsAbiertos.put(s, chatActual);
            }

            FXMLLoader loader = main.crearTemp("amigos");
            boxAmigos.getChildren().add(loader.load());
            CTemplateAmigo controller = loader.getController();
            controller.setNick(s);
            controller.getNick().setOnMouseClicked(event -> {
                abrirChat(s);
            });
        }
    }

    //Actualiza el VBox de amigos conectados. Se llama cada vez que un amigo se conecta, se desconecta o se añade/elimina un amigo.
    public void actualizarListaAmigos(String nombre, boolean conectado) throws IOException {
        if(conectado){
            VBox chatActual = new VBox();
            chatActual.setSpacing(10); // Espaciado entre los mensajes
            chatActual.setPadding(new Insets(10));
            VBox.setVgrow(chatActual, Priority.ALWAYS);
            chatsAbiertos.put(nombre, chatActual);
        }else{
            chatsAbiertos.remove(nombre);
            if(userChatActual != null && userChatActual.equals(nombre)){
                scrollPaneChats.setContent(null);
                userChatActual=null;
                tituloChat.setText("");
            }
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

    //Cambia el contenido del ScrollPane del chat. Para ello se guardan todos los VBoxes con los chats abiertos,
    //y se cambia el que está asignado al ScrollPane por el del amigo seleccionado, si no se abrió aún se crea y se guarda.
    @FXML
    private void abrirChat(String amigo) {
        chat.getChildren().clear();
        userChatActual = amigo;
        tituloChat.setText(userChatActual);
        if (!chatsAbiertos.containsKey(amigo)) {
            VBox chatActual = chatsAbiertos.get(amigo);
            chat.getChildren().add(chatActual);
        } else {
            System.out.println("recuperando chat con " + amigo);
            VBox chatExistente = chatsAbiertos.get(amigo);
            scrollPaneChats.setContent(chatExistente);
            scrollPaneChats.layout();
            scrollPaneChats.setVvalue(1.0);
        }
    }

    //Llama a la función que abre la ventana de solicitudes.
    @FXML
    public void clickSolicitudes() throws SQLException, IOException {
        main.abrirSolicitudes();
    }

    //Función para envíar un mensaje, se activa cuando se clica sobre la imagen de enviar. Envía el mensaje al otro
    //cliente y coloca el mensaje en el chat.
    @FXML
    public void enviarMensaje() throws IOException {
        if (msg.getText().isEmpty() || userChatActual == null){
            return;
        }
        else {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_RIGHT); // Alinear el contenido a la derecha

        FXMLLoader loader = main.crearTemp("mensajes");
        hbox.getChildren().add(loader.load());
        CTemplateMensaje controller = loader.getController();
        controller.init(msg.getText(),getTiempoFormateado());

        chatsAbiertos.get(userChatActual).getChildren().add(hbox);

        ClientInterface remoto = main.getcRemoto().getAmigosConectadosHM().get(userChatActual);
        remoto.recibirMensaje(msg.getText(), main.getcRemoto().getNombre());
        msg.setText("");

        Platform.runLater(() -> {
            scrollPaneChats.layout();
            scrollPaneChats.setVvalue(1.0);
        });
    }
    }

    //Función que es llamada por otro cliente para que recibas el mensaje. Añade un mensaje al chat.
    public void recibirMensaje(String mensaje, String remitente) throws IOException {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);

        FXMLLoader loader = main.crearTemp("mensajes");
        hbox.getChildren().add(loader.load());
        CTemplateMensaje controller = loader.getController();
        controller.setColorRec();
        controller.init(mensaje,getTiempoFormateado());

        Platform.runLater(() -> {
            chatsAbiertos.get(remitente).getChildren().add(hbox);
        });
        Platform.runLater(() -> {
            scrollPaneChats.layout();
            scrollPaneChats.setVvalue(1.0);
        });
    }

    //Función para enviar imagenes. Es llamada cuando se arrastra una imagen al chat. Llama a recibir imagen en el cliente
    //al que se le envía y coloca la imagen en el chat.
    public void enviarImagen(File archivo) {
        if(userChatActual == null) {
            return;
        }
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

            initImagen(imagenBytes, "right",userChatActual);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Función para recibir imagen, llamada por el cliente que la envía. Llama a la función que pone la imagen en el chat.
    public void recibirImagen(byte[] imagen, String nombreArchivo,String remitente) throws IOException {
        initImagen(imagen, "left",remitente);
    }

    //Función para colocar la imagen en el chat.
    private void initImagen(byte[] imagen, String op,String chat){
        HBox hbox = new HBox();
        if(op.equals("left")){
            hbox.setAlignment(Pos.CENTER_LEFT);
        }else if(op.equals("right")){
            hbox.setAlignment(Pos.CENTER_RIGHT);
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(imagen);
        Image image = new Image(bis);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);
        hbox.getChildren().add(imageView);

        Platform.runLater(() -> {
            chatsAbiertos.get(chat).getChildren().add(hbox);
        });
        Platform.runLater(() -> {
            scrollPaneChats.layout();
            scrollPaneChats.setVvalue(1.0);
        });
    }

    //Función para abrir la ventana de añadir amigo.
    @FXML
    public void nuevoAmigo() throws SQLException, IOException {
        main.abrirNuevoAmigo();
    }

    //Función que llama el cliente que envía solicitud a tu cuenta. Cambia la imagen a la campana sonando
    //y añade la solicitud a las solicitudes pendientes.
    public void nuevaSolicitudRecibida(String solicitante, Image image) {
        solicitudes.setImage(image);
        solicitudesPendientes.add(solicitante);
        numSolis++;
    }

    //Función que permite arrastrar las imagenes al chat.
    private void configurarDragAndDrop(ScrollPane target) {
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

    //Función que devuelve la hora y el minuto en formato HH:mm para poder colocarlos en el mensaje.
    private String getTiempoFormateado(){
        LocalTime now = LocalTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
    }
}
