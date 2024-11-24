package org.utleon.elzarape;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;


public class InicioController {

    @FXML
    private VBox alimentoItemBar;

    @FXML
    private VBox bebidaItemBar;

    @FXML
    private VBox categoriaItemBar;

    @FXML
    private VBox clienteItemBar;

    @FXML
    private VBox comboItemBar;

    @FXML
    private VBox empleadoItemBar;

    @FXML
    private VBox salirItemBar;

    @FXML
    private VBox sucursalItemBar;

    Alert alert = null;
    @FXML
    public void initialize() {
        sucursalItemBar.setOnMouseClicked(event -> {
            try {
                cargarModulo("sucursales", "Sucursales Gestión");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        clienteItemBar.setOnMouseClicked(event -> {
            try {
                cargarModulo("clientes", "Clientes Gestión");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        empleadoItemBar.setOnMouseClicked(event -> {
            try {
                cargarModulo("empleados", "Empleados Gestión");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        categoriaItemBar.setOnMouseClicked(event -> {
            try {
                cargarModulo("categorias", "Categorias Gestión");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        alimentoItemBar.setOnMouseClicked(event -> {
            try {
                cargarModulo("alimentos", "Alimentos Gestión");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        bebidaItemBar.setOnMouseClicked(event -> {
            try {
                cargarModulo("bebidas", "Bebidas Gestión");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        comboItemBar.setOnMouseClicked(event -> {
            alert = new Alert(Alert.AlertType.WARNING, "Por el momento no astá disponible.");
            alert.showAndWait();
        });
        salirItemBar.setOnMouseClicked(event -> {
            alert = new Alert(Alert.AlertType.WARNING, "Saliendo Del Sistema");
            alert.showAndWait();
            System.exit(0);
        });
    }

    public void cargarModulo(String fxml, String nombre) throws IOException{
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(InicioController.class.getResource(fxml+".fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("El Zarape: "+nombre);
        stage.setMaximized(true);
        stage.show();
        stage = (Stage) alimentoItemBar.getScene().getWindow();
        stage.close();
    }

}
