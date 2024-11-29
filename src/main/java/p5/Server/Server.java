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
        System.setProperty("java.rmi.server.hostname", "192.168.27.154");
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        String portNum, registryURL;
        try{
            String name = "server";
            portNum = "1099";
            int RMIPortNum = Integer.parseInt(portNum);
            startRegistry(1099);
            ServerImpl exportedObj = new ServerImpl();
            //registryURL = "rmi://192.168.56.1/server";
            Naming.rebind("server", exportedObj);
            System.out.println("Server registered.  Registry currently contains:");
            //listRegistry(registryURL);
            System.out.println(name + " ready.");
        }// end try
        catch (Exception re) {
            System.out.println("Exception in HelloServer.main: " + re);
        } // end catch
    } // end main

    // This method starts an RMI registry on the local host, if it
    // does not already exist at the specified port number.
    private static void startRegistry(int RMIPortNum)
            throws RemoteException {
        /*try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list( );  // This call will throw an exception
            // if the registry does not already exist
        }
        catch (RemoteException e) {
            // No valid registry at that port.
            System.out.println("RMI registry cannot be located at port " + RMIPortNum);*/
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
            System.out.println("RMI registry created at port " + RMIPortNum);
        //}
    } // end startRegistry

    // This method lists the names registered with a Registry object
    private static void listRegistry(String registryURL)
            throws RemoteException, MalformedURLException {
        System.out.println("Registry " + registryURL + " contains: ");
        String [ ] names = Naming.list(registryURL);
        for (int i=0; i < names.length; i++)
            System.out.println(names[i]);
    } //end listRegistry


}