package p5.Server;

import p5.Client.ClientInterface;

import java.rmi.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface ServerInterface extends Remote{
    HashMap<String,ClientInterface> conectados = new HashMap<>();
    public int registrarUsuario(String name, String passwd) throws RemoteException, SQLException;

    public HashMap<String,ClientInterface> iniciarSesion (String name, String passwd) throws RemoteException, SQLException;
    public void notificarConexion(String name) throws RemoteException, SQLException;

    void registrarCliente(String nombre, ClientInterface referencia) throws RemoteException;
    ClientInterface obtenerCliente(String nombre) throws RemoteException;
    public int anhadirSolicitud(String solicitante, String solicitado) throws RemoteException, SQLException;
    public void aceptarSolicitud(String solicitante, String solicitado) throws RemoteException, SQLException;
    public void rechazarSolicitud(String solicitante, String solicitado) throws RemoteException, SQLException;
    public ArrayList<String> buscarSolicitudesUsuario(String name) throws RemoteException, SQLException;
}
