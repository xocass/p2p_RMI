package p5.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server{

    public static void main(String args[]) {
        //System.setProperty("java.rmi.server.hostname", "192.168.27.154");
        try{
            String name = "server";
            ServerImpl exportedObj = new ServerImpl();
            Registry registro = LocateRegistry.createRegistry(1099);
            System.out.println("RMI registry created at port " + 1099);
            //listRegistry(registro);
            registro.rebind("server", exportedObj);
            System.out.println(name + " ready.");
        }
        catch (Exception re) {
            System.out.println("Exception in Server.main: " + re);
        }
    }


}