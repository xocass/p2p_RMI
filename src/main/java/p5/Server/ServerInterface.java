package p5.Server;

import java.rmi.*;
import java.sql.SQLException;

public interface ServerInterface extends Remote{
    public int registrarUsuario(String name, String passwd) throws RemoteException, SQLException;

    public int iniciarSesion (String name, String passwd) throws RemoteException, SQLException;

}
