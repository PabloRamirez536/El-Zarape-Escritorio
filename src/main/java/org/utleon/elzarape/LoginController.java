package org.utleon.elzarape;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import com.google.gson.JsonObject;

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

    Globals globals = new Globals();

    public void initialize() {
        btnLogin.setOnMouseClicked(event -> {
            validarLogin();
        });
        btnSalir.setOnMouseClicked(event -> System.exit(0));
    }

    public void validarLogin() {
        String usuario = txtUsuario.getText();
        String contrasenia = txtContrsenia.getText();

        if (usuario.isEmpty() || contrasenia.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Credenciales vacías", "Debes colocar tus credenciales.");
            return;
        }

        try {
            // Envía los datos como x-www-form-urlencoded
            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "usuario/loginEmpelado")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("nombre", usuario)  // Campo para el nombre de usuario
                    .field("contrasenia", contrasenia)  // Campo para la contraseña
                    .asString();

            // Maneja la respuesta del servidor
            if (response.getStatus() == 200 || response.getStatus() == 201) {
                JsonObject responseBody = new Gson().fromJson(response.getBody(), JsonObject.class);
                if ("success".equals(responseBody.get("status").getAsString())) {
                    cargarModuloPrincipal();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Inicio de Sesión", responseBody.get("message").getAsString());
                }
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error en el Servidor", "Código: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Excepción", "Ocurrió un error al procesar la solicitud.");
        }

    }


    public void cargarModuloPrincipal() throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(LoginController.class.getResource("inicio.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("El Zarape - Sistema de Gestión");
        stage.setMaximized(true);
        stage.show();

        // Cierra la ventana de login
        Stage loginStage = (Stage) btnLogin.getScene().getWindow();
        loginStage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);//
        alert.showAndWait();
    }

}
