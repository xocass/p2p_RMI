package p5.Client;

import java.io.IOException;
import java.rmi.Remote;

public interface ClientInterface extends Remote {
    //Función que recibe un mensaje de un amigo y lo muestra en el chat correspondiente
    void recibirMensaje(String mensaje, String name) throws IOException;

    //Función que recibe una imagen de un amigo y la muestra en el chat correspondiente
    void recibirImagen(byte[] imagen, String nombreArchivo,String remitente) throws IOException;

    //Función que actualiza los amigos contectados de un usuario en caso de que alguno se conecte o desconecte
    void actualizarListaAmigosConectados(String amigo,ClientInterface objeto,boolean conectado) throws IOException;

    //Función que añade una nueva solicitud de amistad a la pestaña de solicitudes pendientes de un usuario cuando alguien le envía una
    public void nuevaSolicitudRecibida(String solicitante) throws IOException;
}

