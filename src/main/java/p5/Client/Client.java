package p5.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import p5.Client.controllers.*;
import p5.Server.*;

import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
        //se inicia la ventana de iniciar sesión
        stage=primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VInicioSesion.fxml"));
        primaryStage.setTitle("iniciar sesion");
        Scene iniciar = new Scene(loader.load(), 681.4, 400);
        CInicioSesion controller = loader.getController();
        //se configura el cierre de ventanas y la acción de pulsar enter
        iniciar.setOnKeyPressed(event -> {
            if(event.getCode() == ENTER){
                    try {
                        controller.clickLogin();
                    } catch (Exception e) {
                        System.out.println("SALTO EXCEPCION PULSANDO ENTER AL INICIAR SESION");
                    }
            }
        });
        stage.setOnCloseRequest(event -> {
            manejarCierreVentana(event);
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
        try {/*
            String registryURL = "rmi://localhost/server";
            System.out.println(registryURL);
            // se castea el objeto remoto a la interfaz de servidor
            server = (ServerInterface) Naming.lookup(registryURL);
            System.out.println("Lookup completed");*/
            Registry reg = LocateRegistry.getRegistry("192.168.27.154",1099);
            server = (ServerInterface) reg.lookup("server");
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

    //función para iniciar diferentes templates
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
        Scene scene = new Scene(fxmlLoader.load(), 663, 418);
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
        Scene scene = new Scene(fxmlLoader.load(), 365, 162);
        CAnhadir controller = fxmlLoader.getController();
        controller.init(this,cRemoto.getNombre());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    //dependiendo de la ventana en la que esté se llama a una función manejadora distinta
    //(solo se maneja en ventana principal y solicitudes)
    public void nuevaSolicitudRecibida(String solicitante) {
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
                cPrincipal.nuevaSolicitudRecibida(solicitante,new Image(getClass().getResource("notificacion.png").toExternalForm()));
            } else {
                System.err.println("cPrincipal is null.");
            }
        }
    }

    private void manejarCierreVentana(WindowEvent event) {
        try {
            server.notificarDesconexion(cRemoto.getNombre());
            System.out.println("Cliente desconectado del servidor central.");
        } catch (RemoteException | SQLException e) {
            e.printStackTrace();
        }finally {
            cRemoto = null;
            System.gc();
            System.exit(1);
        }
    }


}
