package p5.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    void enviarMensaje(String mensaje) throws RemoteException;
}

