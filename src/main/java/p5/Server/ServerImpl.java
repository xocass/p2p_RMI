package p5.Server;

import p5.Client.Client;
import p5.Client.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
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
    public HashMap<String,ClientInterface> iniciarSesion(String name, String passwd) throws RemoteException, SQLException {
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
                List<String> amigos = obtenerAmigos(name);

                HashMap<String,ClientInterface> amigosConectados = new HashMap<>();
                for (String amigo : amigos) {
                    if (conectados.containsKey(amigo)) { // Verificar si el amigo está conectado
                        amigosConectados.put(amigo,conectados.get(amigo));
                    }
                }
                return amigosConectados;

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

    @Override
    public void registrarCliente(String nombre, ClientInterface referencia) throws RemoteException {
        conectados.put(nombre, referencia);
        System.out.println("Cliente registrado: " + nombre);
    }

    @Override
    public ClientInterface obtenerCliente(String nombre) throws RemoteException {
        return conectados.get(nombre);
    }

    @Override
    public void notificarConexion(String name) throws RemoteException, SQLException {
        // Obtener la lista de amigos conectados del cliente que se conecta
        List<String> amigos = obtenerAmigos(name);
        ClientInterface objNuevaConexion = conectados.get(name);
        for (String amigo : amigos) {
            if (conectados.containsKey(amigo)) {
                ClientInterface amigoConectado = conectados.get(amigo);

                // Notificar al amigo conectado que este cliente se ha conectado
                try {
                    amigoConectado.actualizarListaAmigosConectados(name,objNuevaConexion,true);
                } catch (RemoteException e) {
                    System.err.println("Error notificando a " + amigo + ": " + e.getMessage());
                }
            }
        }
    }


    public List<String> obtenerAmigos(String amigo) throws SQLException {
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
