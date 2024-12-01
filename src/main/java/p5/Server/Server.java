package p5.Server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server{

    //Main del server, crea registro rmi para que se conecten los clientes
    public static void main(String args[]) {
        System.setProperty("java.rmi.server.hostname", "192.168.27.154");
        try{
            String name = "server";
            ServerImpl exportedObj = new ServerImpl();
            Registry registro = LocateRegistry.createRegistry(1099);
            System.out.println("RMI registry created at port " + 1099);
            registro.rebind("server", exportedObj);
            System.out.println(name + " ready.");
        }
        catch (Exception re) {
            System.out.println("Exception in Server.main: " + re);
        }
    }


}