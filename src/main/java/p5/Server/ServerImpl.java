package p5.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class ServerImpl extends UnicastRemoteObject implements ServerInterface{
    private Connection connection;
    public ServerImpl() throws RemoteException {
    }

    private void conexionBD(){
        // Datos de conexión
        String url = "jdbc:postgresql://aws-0-eu-west-3.pooler.supabase.com:6543/postgres?user=postgres.rmhynaxyudvptpoijyuy&password=¿amocomdis!";
        String user = "postgres.rmhynaxyudvptpoijyuy";
        String password = "¿amocomdis!";

        try {
            // Establecer conexión
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a Supabase");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registrarUsuario(String name, String passwd) throws RemoteException, SQLException {
        conexionBD();

        // Crear consulta
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM usuario");

        // Leer resultados
        while (rs.next()) {
            System.out.println("Columna: " + rs.getString("nick"));
        }

        // Cerrar conexión
        connection.close();
    }
}
