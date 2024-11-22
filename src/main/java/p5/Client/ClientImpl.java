package p5.Client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ClientImpl extends UnicastRemoteObject implements ClientInterface {
    private final String nombre;
    private HashMap<String,ClientInterface> amigosConectados;

    public ClientImpl(String nombre,HashMap<String,ClientInterface> amigosConectados) throws RemoteException {
        this.nombre = nombre;
        this.amigosConectados=amigosConectados;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public void enviarMensaje(String mensaje,String name) throws RemoteException {
        System.out.println(mensaje);
    }

    @Override
    public void actualizarListaAmigosConectados(String amigo,ClientInterface objeto,boolean conectado) throws RemoteException {
        if(conectado){
            if (!amigosConectados.containsKey(amigo)) {
                amigosConectados.put(amigo, objeto);
                //main.actualizarVista -> acutaliza lista de chats
                System.out.println("Amigo conectado: " + amigo);
            } else {
                System.out.println("No se pudo obtener la referencia del amigo: " + amigo);
            }
        }else{
            amigosConectados.remove(amigo, objeto);
            //main.actualizarVista -> acutaliza lista de chats
            System.out.println("Amigo desconectado: " + amigo);
        }
    }


    //onAction boton de enviar -> main.nuevoMensaje(str,nombre);

}

