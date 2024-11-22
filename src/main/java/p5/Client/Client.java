package p5.Client;
import javafx.application.Application;
import javafx.stage.Stage;
import p5.Client.controllers.*;
import p5.Server.*;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class Client extends Application{
    Stage stage;
    ServerInterface server;

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
    public void abrirPrincipal() throws IOException {
        stage.setTitle("");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VPrincipal.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600.4, 400);
        CPrincipal controller = fxmlLoader.getController();
        controller.init(server,this);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void crearCliente(String nick,HashMap<String,ClientInterface> amigosCon) throws RemoteException, SQLException {
        ClientImpl cRemoto = new ClientImpl(nick,amigosCon);
        server.registrarCliente(nick, cRemoto);
        server.notificarConexion(nick);
        System.out.println("Cliente registrado en el servidor central.");
    }
}
