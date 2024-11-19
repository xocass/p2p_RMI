package p5.Client;
import javafx.application.Application;
import javafx.stage.Stage;
import p5.Client.controllers.cRegistrarse;
import p5.Server.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application{

    ServerInterface server;
    public static void main(String args[]) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VRegistrarse.fxml"));
        primaryStage.setTitle("prueba");
        primaryStage.setScene(new Scene(loader.load(), 385, 216));
        primaryStage.show();
        cRegistrarse controller = loader.getController();
        registroRMI();
        controller.setServer(server);
        //server.registrarUsuario("mati","mati");
    }




    private void registroRMI(){
        try {
            String registryURL = "rmi://localhost:1099/server";
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
