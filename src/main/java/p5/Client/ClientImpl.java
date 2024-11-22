package p5.Client;

import java.rmi.RemoteException;
import java.util.HashMap;

public class ClientImpl implements ClientInterface {
    private final String nombre;
    private HashMap<String,ClientImpl> amigosConectados;

    public ClientImpl(String nombre) throws RemoteException {
        this.nombre = nombre;
        amigosConectados = new HashMap<>();
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public void enviarMensaje(String mensaje) throws RemoteException {
        System.out.println(mensaje);
    }

    @Override
    public void actualizarListaAmigosConectados(String amigo,boolean conectado) throws RemoteException {
        if (conectado) {
            if (!amigosConectados.contains(amigo)) {
                amigosConectados.add(amigo);
                System.out.println("Amigo conectado: " + amigo);
            }
        } else {
            listaAmigosConectados.remove(amigo);
            System.out.println("Amigo desconectado: " + amigo);
        }

    }


}
