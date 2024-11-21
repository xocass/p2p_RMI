package p5.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;

public class ServerImpl extends UnicastRemoteObject implements ServerInterface{
    private Connection connection;
    private HashMap<String,String> conectados = new HashMap<>();
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
        String insertQuery = "INSERT INTO usuario (nick, passwd) VALUES (?, ?)\n" +
                "ON CONFLICT (nick) DO NOTHING;\n";

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

    @Override
    public HashMap<String,String> iniciarSesion(String name, String passwd) throws RemoteException, SQLException {
        conexionBD(); // Establecer conexión a la base de datos

        String query = "SELECT COUNT(*) FROM usuarios WHERE nick = ? AND passwd = ?";
        int resultado = 0;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Configurar los parámetros de la consulta
            stmt.setString(1, name);
            stmt.setString(2, passwd);

            // Ejecutar la consulta
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { // Verifica si hay resultados
                    resultado = 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al iniciar sesión: " + e.getMessage());
            throw e; // Lanzar la excepción si ocurre un error
        } finally {
            // Cerrar conexión
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        }

        if (resultado==1){
            //conectados.put(name,ip);
        }
        return null;
    }



}
