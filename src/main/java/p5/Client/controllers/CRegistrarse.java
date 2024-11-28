package p5.Client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import p5.Client.Client;
import p5.Server.ServerInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class CRegistrarse {
    private ServerInterface server;
    @FXML
    private TextField nick;
    @FXML
    private PasswordField passwd;
    @FXML
    private PasswordField passwd2;
    @FXML
    private Button registrar;
    @FXML
    private Label nodisponible;
    private Client main;
    private int op;

    public void init(ServerInterface server, Client client,int op){
        this.main = client;
        this.server=server;
        this.op=op;
    }

    @FXML
    public void clickRegistrar(ActionEvent event) throws SQLException, RemoteException {
        if(!passwd.getText().equals(passwd2.getText())){
            nodisponible.setVisible(true);
            nodisponible.setText("La contraseña no coincide");
        }
        else if(passwd.getText().isEmpty()){
            nodisponible.setVisible(true);
            nodisponible.setText("Ingrese una contraseña");
        }
        else{
            if(op==1){
                int val = server.registrarUsuario(nick.getText(),passwd.getText());
                System.out.println(val);
                if (val==1){
                    try {
                        main.abrirInicioSesion();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (val==0){
                    nodisponible.setVisible(true);
                    nodisponible.setText("El nombre de usuario no está disponible");
                }
            }else if(op==0){
                int val = server.actualizarContrasenha(nick.getText(),passwd.getText());
                if (val==1){
                    try {
                        main.abrirInicioSesion();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (val==0){
                    nodisponible.setVisible(true);
                    nodisponible.setText("El nombre de usuario no existe");
                }
            }

        }
    }
    @FXML
    public void atras() throws IOException {
        main.abrirInicioSesion();
    }
}
