package p5.Client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Implementación de la interfaz del cliente
public class ClientImpl extends UnicastRemoteObject implements ClientInterface {
    private final String nombre;

    //Atributo HashMap que contiene los amigos conectados y sus referencias
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
    public void recibirMensaje(String mensaje, String name) throws IOException {
        main.getcPrincipal().recibirMensaje(mensaje,name);
    }

    @Override
    public void recibirImagen(byte[] imagen, String nombreArchivo, String remitente) throws IOException {
        main.getcPrincipal().recibirImagen(imagen,nombreArchivo,remitente);
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
        main.actualizarListaAmigos(amigo,conectado);
    }

    @Override
    public void nuevaSolicitudRecibida(String solicitante) throws IOException {
        main.nuevaSolicitudRecibida(solicitante);
    }

}

