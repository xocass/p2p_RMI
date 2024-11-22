package p5.Client.controllers;

import p5.Client.ClientInterface;
import java.rmi.RemoteException;

public class ClientImpl implements ClientInterface {
    @Override
    public void enviarMensaje(String mensaje) throws RemoteException {
        System.out.println(mensaje);
    }
}
