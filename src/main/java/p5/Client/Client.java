package p5.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import p5.Client.controllers.*;
import p5.Server.*;

import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import static javafx.scene.input.KeyCode.ENTER;

public class Client extends Application{
    private Stage stage;
    private ServerInterface server;
    private CPrincipal cPrincipal;
    private CSolicitudes cSolicitudes;
    private ClientImpl cRemoto;

    public static void main(String args[]) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage=primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VInicioSesion.fxml"));
        primaryStage.setTitle("iniciar sesion");
        Scene iniciar = new Scene(loader.load(), 681.4, 400);
        CInicioSesion controller = loader.getController();
        iniciar.setOnKeyPressed(event -> {
            if(event.getCode() == ENTER){
                    try {
                        controller.clickLogin();
                    } catch (Exception e) {
                        System.out.println("SALTO EXCEPCION PULSANDO ENTER AL INICIAR SESION");
                    }
            }
        });
        primaryStage.setScene(iniciar);
        primaryStage.setResizable(false);
        primaryStage.show();
        registroRMI();
        controller.init(server,this);
    }

    public ServerInterface getServer() {
        return server;
    }

    public ClientImpl getcRemoto() {
        return cRemoto;
    }

    public CPrincipal getcPrincipal() {
        return cPrincipal;
    }

    private void registroRMI(){
        try {
            String registryURL = "rmi://localhost/server";
            System.out.println(registryURL);
            // find the remote object and cast it to an interface object
            server = (ServerInterface) Naming.lookup(registryURL);
            System.out.println("Lookup completed");


        } // end try
        catch (Exception e) {
            System.out.println("Exception in Client: " + e);
        }
    }



    public void abrirRegistrar(int op) throws IOException {
        stage.setTitle("registrate");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VRegistrarse.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),385,326);
        CRegistrarse controller = fxmlLoader.getController();
        controller.init(server,this,op);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    public void abrirInicioSesion() throws IOException {
        stage.setTitle("inicio sesion");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VInicioSesion.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 681.4, 400);
        CInicioSesion controller = fxmlLoader.getController();
        controller.init(server,this);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public FXMLLoader crearTemp(String opcion){
        switch(opcion){
            case "amigos":
                return new FXMLLoader(getClass().getResource("VTemplateAmigo.fxml"));
            case "solicitudes":
                return new FXMLLoader(getClass().getResource("VTemplateSolicitud.fxml"));
            case "mensajes":
                return new FXMLLoader(getClass().getResource("VTemplateMensaje.fxml"));
            default:
                System.out.println("MAL PASADO EL ARGUMENTO A CREARTEMP");
                break;
        }
        return null;
    }
    public Image getImageNoti(){
        return new Image(getClass().getResource("notificacion.png").toExternalForm());
    }
    public void abrirPrincipal() throws IOException, SQLException {
        stage.setTitle(cRemoto.getNombre());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VPrincipal.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 676, 418);
        cPrincipal = fxmlLoader.getController();
        scene.setOnKeyPressed(event -> {
            if(event.getCode() == ENTER){
                try {
                    cPrincipal.enviarMensaje();
                } catch (Exception e) {
                    System.out.println("SALTO EXCEPCION PULSANDO ENTER EN LA VENTANA PRINCIPAL");
                }
            }
        });
        cPrincipal.init(server,cRemoto.getNombresAmigos(),this);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void crearCliente(String nick,HashMap<String,ClientInterface> amigosCon) throws RemoteException, SQLException {
        cRemoto = new ClientImpl(nick,amigosCon,this);
        server.registrarCliente(nick, cRemoto);
        server.notificarConexion(nick);
        System.out.println("Cliente registrado en el servidor central.");
    }

    public void actualizarListaAmigos(String amigo,boolean conectado) throws IOException {
        Platform.runLater(() -> {
            try {
                cPrincipal.actualizarListaAmigos(amigo,conectado);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void abrirSolicitudes() throws IOException, SQLException {
        stage.setTitle("solicitudes");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VSolicitudes.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 300);
        CSolicitudes cSolicitudes = fxmlLoader.getController();
        cSolicitudes.init(server,server.buscarSolicitudesUsuario(cRemoto.getNombre()),this);
        this.cSolicitudes=cSolicitudes;
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    public void abrirNuevoAmigo() throws IOException, SQLException {
        stage.setTitle("añadir nuevos amigos");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VAnhadir.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 365, 221);
        CAnhadir controller = fxmlLoader.getController();
        controller.init(this,cRemoto.getNombre());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void nuevaSolicitudRecibida(String solicitante) {
        cPrincipal.setSolImage(new Image(getClass().getResource("notificacion.png").toExternalForm()));
        if (stage.getTitle().equals("solicitudes")) {
            if (cSolicitudes != null) {
                cSolicitudes.getNicks().add(solicitante);
                Platform.runLater(() -> {
                    try {
                        cSolicitudes.actualizarListaSolicitudes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                System.err.println("UserData for 'solicitudes' is null.");
            }
        }
        if (stage.getTitle().equals(cRemoto.getNombre())) {
            if (cPrincipal != null) {
                cPrincipal.nuevaSolicitudRecibida(solicitante);
            } else {
                System.err.println("cPrincipal is null.");
            }
        }
    }
}
