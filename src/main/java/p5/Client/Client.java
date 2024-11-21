package p5.Client;
import javafx.application.Application;
import javafx.stage.Stage;
import p5.Client.controllers.*;
import p5.Server.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Client extends Application{

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VInicioSesion.fxml"));
        primaryStage.setTitle("iniciar sesion");
        primaryStage.setScene(new Scene(loader.load(), 606, 322));
        primaryStage.show();
        CInicioSesion controller = loader.getController();
        registroRMI();
        controller.setServer(server);
    }




    private void registroRMI(){
        try {
            String registryURL = "rmi://192.168.56.1:1099/server";
            System.out.println(registryURL);
            // find the remote object and cast it to an interface object
            server = (ServerInterface) Naming.lookup(registryURL);
            System.out.println("Lookup completed");


        } // end try
        catch (Exception e) {
            System.out.println("Exception in Client: " + e);
        }
    }

}
