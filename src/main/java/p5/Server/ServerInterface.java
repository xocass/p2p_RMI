package p5.Server;

import java.rmi.*;
import java.sql.SQLException;

public interface ServerInterface extends Remote{
    public void registrarUsuario(String name, String passwd) throws RemoteException, SQLException;

}
