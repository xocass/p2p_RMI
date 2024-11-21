package p5.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
            System.out.println("Conexion exitosa a Supabase");

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
                    System.out.println("Conexion cerrada.");
                }
                return 1;
            } else {
                System.out.println("No se pudo registrar el usuario.");
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Conexion cerrada.");
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

        String query = "SELECT nick,passwd FROM usuario WHERE nick = ? AND passwd = ?";
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
            if (resultado==1){
                HashMap<String,String> con = conectados;
                con.put("2","2");
                conectados.put(name,"no se");
                return con;
            }else{
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al iniciar sesión: " + e.getMessage());
            throw e; // Lanzar la excepción si ocurre un error
        } finally {
            // Cerrar conexión
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexion cerrada.");
            }
        }
    }

    public List<String> amigosConectados(String amigo) throws SQLException {
        // Asegurarse de que la conexión está establecida antes de realizar la consulta
        conexionBD();

        List<String> amigos = new ArrayList<>();
        // Consulta SQL para obtener todos los amigos del usuario
        String query = "SELECT usuario1, usuario2 FROM amigos WHERE usuario1 = ? OR usuario2 = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Configurar los parámetros de la consulta
            stmt.setString(1, amigo);
            stmt.setString(2, amigo);

            // Ejecutar la consulta
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String usuario1 = rs.getString("usuario1");
                    String usuario2 = rs.getString("usuario2");

                    // Verificar quién es el amigo, ya que uno de ellos será el usuario
                    if (!usuario1.equals(amigo)) {
                        amigos.add(usuario1);  // Usuario1 es amigo de 'amigo'
                    }
                    if (!usuario2.equals(amigo)) {
                        amigos.add(usuario2);  // Usuario2 es amigo de 'amigo'
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener los amigos de " + amigo + ": " + e.getMessage());
            throw e;
        }

        return amigos;
    }



}
