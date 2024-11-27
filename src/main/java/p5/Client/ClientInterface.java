package p5.Client;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    void enviarMensaje(String mensaje,String name) throws IOException;
    void actualizarListaAmigosConectados(String amigo,ClientInterface objeto,boolean conectado) throws IOException;
}

