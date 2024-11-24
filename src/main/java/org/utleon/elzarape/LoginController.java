package org.utleon.elzarape;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private ImageView btnLogin;

    @FXML
    private ImageView btnSalir;

    @FXML
    private PasswordField txtContrsenia;

    @FXML
    private TextField txtUsuario;

    public void initialize(){
        btnLogin.setOnMouseClicked(event ->{
            try {
                validarLogin();
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        });
        btnSalir.setOnMouseClicked(event ->{
            System.exit(0);
        });
    }

    public void validarLogin() throws IOException {
        String usuario = txtUsuario.getText();
        String contrasenia = txtContrsenia.getText();
        Alert alert = null;
        if(usuario.isEmpty() || contrasenia.isEmpty()){
            alert = new Alert(Alert.AlertType.ERROR, "Debes colocar tus credenciales.");
            alert.showAndWait();
        } else if (usuario.equals("admin") && contrasenia.equals("1234")) {
            /*alert = new Alert(Alert.AlertType.CONFIRMATION, "Datos correctos.");
            alert.showAndWait();*/
            cargarModuloPrincipal();
        }else {
            alert = new Alert(Alert.AlertType.ERROR, "Datos incorrectos.");
            alert.showAndWait();
        }
    }

    public void cargarModuloPrincipal() throws IOException{
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(LoginController.class.getResource("inicio.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("El Zarape - Sistema de Gestión");
        stage.setMaximized(true);
        stage.show();
        stage = (Stage) btnLogin.getScene().getWindow();
        stage.close();
    }

}
