package p5.Client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientImpl extends UnicastRemoteObject implements ClientInterface {
    private final String nombre;
    private HashMap<String,ClientInterface> amigosConectados;
    private Client main;

    public ClientImpl(String nombre,HashMap<String,ClientInterface> amigosConectados,Client main) throws RemoteException {
        this.nombre = nombre;
        this.amigosConectados=amigosConectados;
        this.main = main;
    }

    public HashMap<String, ClientInterface> getAmigosConectadosHM() {
        return amigosConectados;
    }

    public ArrayList<String> getNombresAmigos(){
        return new ArrayList<>(amigosConectados.keySet());
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public void enviarMensaje(String mensaje,String name) throws RemoteException {
        System.out.println(name +": "+mensaje);
    }

    @Override
    public void actualizarListaAmigosConectados(String amigo,ClientInterface objeto,boolean conectado) throws IOException {
        if(conectado){
            if (!amigosConectados.containsKey(amigo)) {
                amigosConectados.put(amigo, objeto);
                System.out.println("Amigo conectado: " + amigo);
            } else {
                System.out.println("No se pudo obtener la referencia del amigo: " + amigo);
            }
        }else{
            amigosConectados.remove(amigo, objeto);
            System.out.println("Amigo desconectado: " + amigo);
        }
        main.actualizarListaAmigos(getNombresAmigos());
    }


    //onAction boton de enviar -> main.nuevoMensaje(str,nombre);

}

