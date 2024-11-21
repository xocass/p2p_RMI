package p5.Server;

import java.rmi.*;
import java.sql.SQLException;
import java.util.HashMap;

public interface ServerInterface extends Remote{
    HashMap<String,String> conectados = new HashMap<>();
    public int registrarUsuario(String name, String passwd) throws RemoteException, SQLException;

    public HashMap<String,String> iniciarSesion (String name, String passwd) throws RemoteException, SQLException;

}
