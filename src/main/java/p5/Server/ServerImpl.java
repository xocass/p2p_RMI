package p5.Server;

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
    public int registrarUsuario(String name, String passwd) throws RemoteException, SQLException {
        // Conectar a la base de datos
        conexionBD();

        // Consulta SQL para insertar un usuario
        String insertQuery = "INSERT INTO usuario (nick, passwd) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            // Configurar los parámetros de la consulta
            stmt.setString(1, name);
            stmt.setString(2, passwd);

            // Ejecutar la consulta
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Usuario registrado correctamente: " + name);
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Conexión cerrada.");
                }
                return 1;
            } else {
                System.out.println("No se pudo registrar el usuario.");
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Conexión cerrada.");
                }
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al registrar el usuario: " + e.getMessage());
            throw e; // Lanzar la excepción si ocurre un error
        }
    }

}
