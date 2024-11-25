package p5.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import p5.Client.controllers.*;
import p5.Server.*;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class Client extends Application{
    private Stage stage;
    private ServerInterface server;
    private CPrincipal cPrincipal;
    private ClientImpl cRemoto;

    public static void main(String args[]) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        /*FXMLLoader loader = new FXMLLoader(getClass().getResource("VRegistrarse.fxml"));
        primaryStage.setTitle("prueba");
        primaryStage.setScene(new Scene(loader.load(), 385, 216));
        primaryStage.show();
        cRegistrarse controller = loader.getController();
        registroRMI();
        controller.setServer(server);*/
        stage=primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VInicioSesion.fxml"));
        primaryStage.setTitle("iniciar sesion");
        primaryStage.setScene(new Scene(loader.load(), 681.4, 400));
        primaryStage.setResizable(false);
        primaryStage.show();
        CInicioSesion controller = loader.getController();
        registroRMI();
        controller.init(server,this);
    }

    public ServerInterface getServer() {
        return server;
    }

    public ClientImpl getcRemoto() {
        return cRemoto;
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



    public void abrirRegistrar() throws IOException {
        stage.setTitle("registrate");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VRegistrarse.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),385,216);
        cRegistrarse controller = fxmlLoader.getController();
        controller.init(server,this);
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
            default:
                System.out.println("MAL PASADO EL ARGUMENTO A CREARTEMP");
                break;
        }
        return null;
    }

    public void abrirPrincipal() throws IOException {
        stage.setTitle(cRemoto.getNombre());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VPrincipal.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600.4, 400);
        cPrincipal = fxmlLoader.getController();
        cPrincipal.init(server,(ArrayList<String>)cRemoto.getNombresAmigos(),this);
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

    public void nuevoMensaje(String mensaje, String name){
        //cPrincipal.nuevoMensaje(mensaje,name);
    }

    public void actualizarListaAmigos(ArrayList<String> amigos) throws IOException {
        Platform.runLater(() -> {
            try {
                cPrincipal.actualizarAmigos(amigos);
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
        cSolicitudes.init(server,(ArrayList<String>)server.buscarSolicitudesUsuario(cRemoto.getNombre()),this);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

}
