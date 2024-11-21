package p5.Client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import p5.Server.ServerInterface;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class cRegistrarse {
    private ServerInterface server;
    @FXML
    private TextField nick;
    @FXML
    private PasswordField passwd;
    @FXML
    private Button registrar;
    @FXML
    private Label nodisponible;

    public void setServer(ServerInterface server){
        this.server=server;
    }

    @FXML
    public void clickRegistrar(ActionEvent event) throws SQLException, RemoteException {
        int val = server.registrarUsuario(nick.getText(),passwd.getText());
        System.out.println(val);
        if (val==1){
            try {
                // Cargar la nueva ventana
                FXMLLoader loader = new FXMLLoader(getClass().getResource("VInicioSesion.fxml"));
                Parent root = loader.load();

                // Crear un nuevo Stage
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Inicio Sesión");
                stage.show();

                // Cerrar la ventana actual
                Node source = (Node) event.getSource();
                Stage currentStage = (Stage) source.getScene().getWindow();
                currentStage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (val==0){
            nodisponible.setVisible(true);
        }

    }
}
