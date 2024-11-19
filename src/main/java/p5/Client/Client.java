package p5.Client;
import javafx.application.Application;
import javafx.stage.Stage;
import p5.Server.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;

public class Client extends Application{
    ServerInterface server;
    public static void main(String args[]) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        registroRMI();
        server.registrarUsuario("mati","mati");
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
