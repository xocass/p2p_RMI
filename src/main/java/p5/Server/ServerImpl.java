package p5.Server;

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


//Clase de implementación de las funciones de la interfaz del servidor
public class ServerImpl extends UnicastRemoteObject implements ServerInterface{
    private Connection connection;

    public ServerImpl() throws RemoteException {
    }

    //Función que conecta con la base de datos
    private void conexionBD(){
        String url = "jdbc:postgresql://aws-0-eu-west-3.pooler.supabase.com:6543/postgres?user=postgres.rmhynaxyudvptpoijyuy&password=¿amocomdis!";
        String user = "postgres.rmhynaxyudvptpoijyuy";
        String password = "¿amocomdis!";

        try {
            connection = DriverManager.getConnection(url, user, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Función que registra un usuario en la base de datos
    @Override
    public int registrarUsuario(String name, String passwd) throws RemoteException, SQLException {
        String hashedPasswd = hashPassword(passwd);
        if (hashedPasswd == null) {
            System.err.println("Error al hashear la contraseña.");
            return 0;
        }
        conexionBD();
        String insertQuery = "INSERT INTO usuario (nick, passwd) VALUES (?, ?)\n" +
                "ON CONFLICT (nick) DO NOTHING;\n";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setString(1, name);
            stmt.setString(2, hashedPasswd);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Usuario registrado correctamente: " + name);
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
                return 1;
            } else {
                System.out.println("No se pudo registrar el usuario.");
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al registrar el usuario: " + e.getMessage());
            throw e;
        }
    }

    //Función que actualiza la contraseña de un usuario en la base de datos
    public int actualizarContrasenha(String nick, String newPasswd) throws SQLException, RemoteException {
        conexionBD();
        String hashedPasswd = hashPassword(newPasswd);
        if (hashedPasswd == null) {
            System.err.println("Error al hashear la contraseña.");
            return 0;
        }
        String updateQuery = "UPDATE usuario SET passwd = ? WHERE nick = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.setString(1, hashedPasswd);
            stmt.setString(2, nick);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0 ? 1 : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al actualizar la contraseña: " + e.getMessage());
            return 0;
        } finally {
            // Cerrar la conexión
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }

    //Función que comprueba si el usuario y la contraseña insertados son una tupla de la tabla usuario
    @Override
    public HashMap<String,ClientInterface> iniciarSesion(String name, String passwd) throws RemoteException, SQLException {
        conexionBD();

        String query = "SELECT nick,passwd FROM usuario WHERE nick = ?";
        int resultado = 0;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);

            // Ejecutar la consulta
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashAlmacenado = rs.getString("passwd");

                    String hashIngresado = hashPassword(passwd);

                    if (hashAlmacenado.equals(hashIngresado)) {
                        resultado = 1;
                    }
                }
            }
            if (resultado==1){
                List<String> amigos = obtenerAmigos(name);

                HashMap<String,ClientInterface> amigosConectados = new HashMap<>();
                for (String amigo : amigos) {
                    if (conectados.containsKey(amigo)) {
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
            throw e;
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }

    //Función que registra a un cliente en el Hashmap de conectados
    @Override
    public void registrarCliente(String nombre, ClientInterface referencia) throws RemoteException {
        conectados.put(nombre, referencia);
        System.out.println("Cliente registrado: " + nombre);
    }

    //Función que devuelve la interfaz cliente del HashMap de conectados.
    @Override
    public ClientInterface obtenerCliente(String nombre) throws RemoteException {
        return conectados.get(nombre);
    }

    //Función que notifica a todos los amigos conectados de que te has conectado, añadiendote a sus arrays de amigos
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

    //Función que notifica a todos los amigos que están conectados de que te has desconectado, eliminándote de sus listas de amigos conectados
    @Override
    public void notificarDesconexion(String name) throws RemoteException, SQLException {
        List<String> amigos = obtenerAmigos(name);
        ClientInterface objDesconexion = conectados.get(name);
        for (String amigo : amigos) {
            if (conectados.containsKey(amigo)) {
                ClientInterface amigoConectado = conectados.get(amigo);
                try {
                    amigoConectado.actualizarListaAmigosConectados(name,objDesconexion,false);
                } catch (IOException e) {
                    System.err.println("Error notificando a " + amigo + ": " + e.getMessage());
                }
            }
        }
        conectados.remove(name);
    }

    //Notifica a un solo usuario, se llama al aceptar una solicitud de amistad
    @Override
    public void notificarUsuario(String user, String notificado) throws RemoteException{
        ClientInterface objUser = conectados.get(user);
        try {
            ClientInterface objNotificado = conectados.get(notificado);
            if(objNotificado==null){
                System.out.println(notificado + " esta desconectado");
                return;
            }
            objUser.actualizarListaAmigosConectados(notificado,objNotificado,true);
            objNotificado.actualizarListaAmigosConectados(user,objUser,true);
        } catch (IOException e) {
            System.err.println("Error notificando la aceptación de solicitud a " + user + " por " + notificado);
        }

    }

    //Función que devuelve todos los amigos de un usuario
    public List<String> obtenerAmigos(String amigo) throws SQLException {
        conexionBD();

        List<String> amigos = new ArrayList<>();
        String query = "SELECT usuario1, usuario2 FROM amigos WHERE usuario1 = ? OR usuario2 = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, amigo);
            stmt.setString(2, amigo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String usuario1 = rs.getString("usuario1");
                    String usuario2 = rs.getString("usuario2");

                    if (!usuario1.equals(amigo)) {
                        amigos.add(usuario1);
                    }
                    if (!usuario2.equals(amigo)) {
                        amigos.add(usuario2);
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

    //Método para hashear la contraseña (algoritmo SHA-256)
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Función que devuelve si un usuario existe en la base de datos
    private boolean existeUsuario(String nick) throws RemoteException, SQLException {
        conexionBD();

        String query = "SELECT 1 FROM usuario WHERE nick = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nick);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al comprobar si el usuario existe: " + e.getMessage());
            throw e;
        }
    }

    //Función que comprueba si dos personas son amigos
    private boolean existenAmigos(String usuario1, String usuario2) throws RemoteException, SQLException {

        String query = "SELECT 1 FROM amigos WHERE usuario1 = ? AND usuario2 = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, usuario1);
            stmt.setString(2, usuario2);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw e;
        }
    }

    //Función que comprueba si ya existe una solicitud en la base de datos
    private boolean existeSolicitud(String solicitante, String solicitado) throws SQLException {
        String query = "SELECT 1 FROM solicitudes WHERE solicitante = ? AND solicitado = ? OR solicitante = ? AND solicitado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, solicitante);
            stmt.setString(2, solicitado);
            stmt.setString(3, solicitado);
            stmt.setString(4, solicitante);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al comprobar si existe la solicitud: " + e.getMessage());
            throw e;
        }
    }

    //Función que añade una solicitud a la base de datos
    @Override
    public int anhadirSolicitud(String solicitante, String solicitado) throws RemoteException, SQLException {
        conexionBD();
        if(!existeUsuario(solicitado)){
            return 2;
        }
        if(existenAmigos(solicitado,solicitante)){
            return 3;
        }
        if (existeSolicitud(solicitante, solicitado)) {
            return 4;
        }
        String insertQuery = "INSERT INTO solicitudes (solicitante, solicitado) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setString(1, solicitante);
            stmt.setString(2, solicitado);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Solicitud añadida correctamente.");
                return 1;
            } else {
                System.out.println("No se pudo añadir la solicitud (0 filas afectadas).");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al añadir solicitud: " + e.getMessage());
            return 0;
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        }
    }

    //Función que borra la solicitud de la base de datos y añade la tupla de amigos
    @Override
    public void aceptarSolicitud(String solicitante, String solicitado) throws RemoteException, SQLException {
        conexionBD();
        quitarSolicitud(solicitante,solicitado);
        conexionBD();
        anhadirAmigos(solicitante,solicitado);
    }

    //Función que llama a la función que borra la solicitud de la base de datos
    @Override
    public void rechazarSolicitud(String solicitante, String solicitado) throws RemoteException, SQLException {
        conexionBD();
        quitarSolicitud(solicitante,solicitado);
    }

    //Función que quita la solicitud de la base de datos
    private void quitarSolicitud(String solicitante, String solicitado) throws SQLException {
        String deleteQuery = "DELETE FROM solicitudes WHERE solicitante = ? AND solicitado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setString(1, solicitante);
            stmt.setString(2, solicitado);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Solicitud eliminada correctamente.");
            } else {
                System.out.println("No se encontró ninguna solicitud para eliminar.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al eliminar la solicitud: " + e.getMessage());
            throw e;
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        }
    }

    //Función que añade una tupla de amigos a la base de datos
    private void anhadirAmigos(String usuario1, String usuario2) throws SQLException {
        String insertQuery = "INSERT INTO amigos (usuario1, usuario2) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
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
            throw e;
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexion cerrada.");
            }
        }
    }

    //Función que devuelve las solicitudes recibidas por un usuario
    @Override
    public ArrayList<String> buscarSolicitudesUsuario(String name) throws RemoteException, SQLException {
        conexionBD();

        ArrayList<String> solicitantes = new ArrayList<>();

        String query = "SELECT solicitante FROM solicitudes WHERE solicitado = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    solicitantes.add(rs.getString("solicitante"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al buscar las solicitudes para el usuario " + name + ": " + e.getMessage());
            throw e;
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        }
        return solicitantes;
    }



}
