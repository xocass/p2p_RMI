package p5.Server;

import p5.Client.Client;
import p5.Client.ClientInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        // Hashear la contraseña
        String hashedPasswd = hashPassword(passwd);
        if (hashedPasswd == null) {
            System.err.println("Error al hashear la contraseña.");
            return 0;
        }

        // Conectar a la base de datos
        conexionBD();

        // Consulta SQL para insertar un usuario
        String insertQuery = "INSERT INTO usuario (nick, passwd) VALUES (?, ?)\n" +
                "ON CONFLICT (nick) DO NOTHING;\n";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            // Configurar los parámetros de la consulta
            stmt.setString(1, name);
            stmt.setString(2, hashedPasswd);

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

        String query = "SELECT nick,passwd FROM usuario WHERE nick = ?";
        int resultado = 0;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Configurar los parámetros de la consulta
            stmt.setString(1, name);

            // Ejecutar la consulta
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { // Verifica si la contraseña es válida
                    String hashAlmacenado = rs.getString("passwd");

                    // Hashear la contraseña ingresada para comparar
                    String hashIngresado = hashPassword(passwd);

                    // Comparar los hashes
                    if (hashAlmacenado.equals(hashIngresado)) {
                        resultado = 1;
                    }
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
                } catch (IOException e) {
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

    // Método para hashear la contraseña (algoritmo SHA-256)
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b)); // Convertir byte a hexadecimal
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void anhadirSolicitud(String solicitante, String solicitado) throws RemoteException, SQLException {
        conexionBD();
        String insertQuery = "INSERT INTO solicitudes (solicitante, solicitado) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            // Configurar los parámetros de la consulta
            stmt.setString(1, solicitante);
            stmt.setString(2, solicitado);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al añadir amigos: " + e.getMessage());
            throw e; // Propagar la excepción si ocurre un error
        } finally {
            // Cerrar la conexión
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        }
    }
    @Override
    public void aceptarSolicitud(String solicitante, String solicitado) throws RemoteException, SQLException {
        conexionBD();
        quitarSolicitud(solicitante,solicitado);
        anhadirAmigos(solicitante,solicitado);
    }

    @Override
    public void rechazarSolicitud(String solicitante, String solicitado) throws RemoteException, SQLException {
        conexionBD();
        quitarSolicitud(solicitante,solicitado);
    }

    private void quitarSolicitud(String solicitante, String solicitado) throws SQLException {
        // Consulta SQL para eliminar la solicitud correspondiente
        String deleteQuery = "DELETE FROM solicitudes WHERE solicitante = ? AND solicitado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            // Configurar los parámetros de la consulta
            stmt.setString(1, solicitante);
            stmt.setString(2, solicitado);

            // Ejecutar la consulta
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Solicitud eliminada correctamente.");
            } else {
                System.out.println("No se encontró ninguna solicitud para eliminar.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al eliminar la solicitud: " + e.getMessage());
            throw e; // Propagar la excepción si ocurre un error
        } finally {
            // Cerrar la conexión
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        }
    }

    private void anhadirAmigos(String usuario1, String usuario2) throws SQLException {
        // Consulta SQL para insertar una nueva relación de amigos
        String insertQuery = "INSERT INTO amigos (usuario1, usuario2) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            // Configurar los parámetros de la consulta
            stmt.setString(1, usuario1);
            stmt.setString(2, usuario2);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Amistad añadida correctamente entre " + usuario1 + " y " + usuario2);
            } else {
                System.out.println("No se pudo añadir la amistad.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al añadir amigos: " + e.getMessage());
            throw e; // Propagar la excepción si ocurre un error
        } finally {
            // Cerrar la conexión
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        }
    }

    public ArrayList<String> buscarSolicitudesUsuario(String name) throws RemoteException, SQLException {
        // Conectar a la base de datos
        conexionBD();

        // Lista para almacenar los nombres de los solicitantes
        ArrayList<String> solicitantes = new ArrayList<>();

        // Consulta SQL para obtener los solicitantes donde "solicitado" es "name"
        String query = "SELECT solicitante FROM solicitudes WHERE solicitado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Configurar el parámetro de la consulta
            stmt.setString(1, name);

            // Ejecutar la consulta
            try (ResultSet rs = stmt.executeQuery()) {
                // Recorrer el resultado y agregar los nombres a la lista
                while (rs.next()) {
                    solicitantes.add(rs.getString("solicitante"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al buscar las solicitudes para el usuario " + name + ": " + e.getMessage());
            throw e; // Relanzar la excepción si ocurre un error
        } finally {
            // Cerrar la conexión
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        }

        return solicitantes;
    }



}
