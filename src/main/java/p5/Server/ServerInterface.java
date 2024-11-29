package p5.Server;

import p5.Client.ClientInterface;

import java.rmi.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface ServerInterface extends Remote{
    //HasMap que contiene el nombre y el objeto remoto (ClientInterface) de todos los usuarios conectados
    HashMap<String,ClientInterface> conectados = new HashMap<>();

    //Registra un usuario en la base de datos de la aplicación con nombre de usuario y contraseña
    int registrarUsuario(String name, String passwd) throws RemoteException, SQLException;

    //Actualiza la contraseña de un usuario existente
    int actualizarContrasenha(String nick, String newPasswd) throws SQLException, RemoteException;

    //Comprueba que el usuario y la contraseña introducidos son correctos y en caso de que lo sea devuelve un HashMap con los amigos conectados de ese usuario
    HashMap<String,ClientInterface> iniciarSesion (String name, String passwd) throws RemoteException, SQLException;

    //Notifica a los amigos de un usuario que se ha conectado, para que estos puedan actualizar su lista de amigos conectados
    void notificarConexion(String name) throws RemoteException, SQLException;

    //Notifica a los amigos de un usuario que se ha desconectado, para que estos puedan actualizar su lista de amigos conectados
    void notificarDesconexion(String name) throws RemoteException, SQLException;

    //Registra un cliente que se conecta en el servidor, con nombre de usuario y su referencia remota
    void registrarCliente(String nombre, ClientInterface referencia) throws RemoteException;

    //Devuelve la referencia remota de un cliente conectado a partir de su nombre
    ClientInterface obtenerCliente(String nombre) throws RemoteException;

    //Añade una solicitud de amistad a la base de datos
    int anhadirSolicitud(String solicitante, String solicitado) throws RemoteException, SQLException;
    void aceptarSolicitud(String solicitante, String solicitado) throws RemoteException, SQLException;
    void rechazarSolicitud(String solicitante, String solicitado) throws RemoteException, SQLException;
    ArrayList<String> buscarSolicitudesUsuario(String name) throws RemoteException, SQLException;
    void notificarUsuario(String user, String notificado) throws RemoteException;
}
