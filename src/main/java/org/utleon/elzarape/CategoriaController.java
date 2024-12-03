package org.utleon.elzarape;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.utleon.elzarape.model.Categoria;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CategoriaController {
    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private VBox btnInicio;

    @FXML
    private TableColumn<Categoria, Integer> colIdCategoria;

    @FXML
    private TableColumn<Categoria, String> colDescripcion;

    @FXML
    private TableColumn<Categoria, String> colTipoCategoria;

    @FXML
    private TableColumn<Categoria, String> colEstatus;

    @FXML
    private TableView<Categoria> tblCategorias;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private CheckBox txtEstatus;

    @FXML
    private ComboBox<String> txtTipo;

    @FXML
    private TextField txtIdCategoria;

    private Categoria categoriaSelected = null;
    Globals globals = new Globals();
    ObservableList<Categoria> categoria;

    @FXML
    public void initialize() {
        txtEstatus.setSelected(true);
        initColumns();
        loadCategorias();
        txtTipo.getItems().addAll("Alimento", "Bebida");
        txtIdCategoria.setEditable(false);
        tblCategorias.setOnMouseClicked(event -> {
            if (tblCategorias.getSelectionModel().getSelectedItem() != null) {
                showCategoriaSelected();
            }
        });

        btnGuardar.setOnAction(event -> {
            if (isFormValid()) {
                if (categoriaSelected == null) {
                    enviarCategoria(createCategoriaFromForm());
                } else {
                    modificarCategoria(createCategoriaFromForm());
                }
                loadCategorias();
                cleanForm();
            } else {
                showAlert(Alert.AlertType.WARNING, "Advertencia", null, "Por favor, completa todos los campos.");
            }
        });

        btnCancelar.setOnAction(event -> cleanForm());

        btnInicio.setOnMouseClicked(event -> {
            try {
                cargarModuloPrincipal();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void cargarModuloPrincipal() throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(LoginController.class.getResource("inicio.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("El Zarape - Sistema de Gestión");
        stage.setMaximized(true);
        stage.show();
        stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    private void initColumns() {
        colIdCategoria.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getIdCategoria()));
        colDescripcion.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDescripcion()));
        colTipoCategoria.setCellValueFactory(cell -> {
            String tipo = cell.getValue().getTipo();
            return new SimpleObjectProperty<>(tipo.equals("A") ? "Alimento" : "Bebida");
        });
        colEstatus.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getActivo() ? "Activo" : "Inactivo"));
    }

    private void loadCategorias() {
        new Thread(() ->{
            HttpResponse<String> response = Unirest.get(globals.BASE_URL+"categoria/getAllCategoria").asString();
            Platform.runLater(() ->{
                Gson gson = new Gson();
                System.out.println(response.getBody());
                categoria = FXCollections.observableArrayList(List.of(gson.fromJson(response.getBody(),Categoria[].class)));
                tblCategorias.setItems(categoria);
                tblCategorias.refresh();
            });
        }).start();
    }


    private void showCategoriaSelected() {
        categoriaSelected = tblCategorias.getSelectionModel().getSelectedItem();
        txtIdCategoria.setText(String.valueOf(categoriaSelected.getIdCategoria()));
        txtDescripcion.setText(categoriaSelected.getDescripcion());
        String tipo = categoriaSelected.getTipo().equals("A") ? "Alimento" : "Bebida";
        txtTipo.getSelectionModel().select(tipo);
        txtEstatus.setSelected(categoriaSelected.getActivo());
        btnGuardar.setText("Modificar");

    }

    private Categoria createCategoriaFromForm() {
        Categoria categoria = new Categoria();
        if (categoriaSelected != null) {
            categoria.setIdCategoria(categoriaSelected.getIdCategoria());
        }
        categoria.setDescripcion(txtDescripcion.getText());
        String tipoSeleccionado = txtTipo.getSelectionModel().getSelectedItem();
        categoria.setTipo(tipoSeleccionado.equals("Alimento") ? "A" : "B");
        categoria.setActivo(txtEstatus.isSelected());

        return categoria;
    }

    private Object enviarCategoria(Categoria categoria) {
        try {
            Gson gson = new Gson();
            String categoriaJson = gson.toJson(categoria);
            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "categoria/insertCategoria")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("datosCategoria", categoriaJson)
                    .asString();
            return response.getStatus() == 200 || response.getStatus() == 201 ? response.getBody() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object modificarCategoria(Categoria categoria) {
        try {
            Gson gson = new Gson();
            String categoriaJson = gson.toJson(categoria);
            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "categoria/updateCategoria")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("datosCategoria", categoriaJson)
                    .asString();
            return response.getStatus() == 200 || response.getStatus() == 201 ? response.getBody() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleResponse(HttpResponse<String> response, String successMessage, String errorMessage) {
        Platform.runLater(() -> {
            if (response.getStatus() == 200 || response.getStatus() == 201) {
                showAlert(Alert.AlertType.INFORMATION, "Éxito", null, successMessage);
            } else {
                // Mostrar el cuerpo de la respuesta en caso de error
                showAlert(Alert.AlertType.ERROR, "Error", null, errorMessage + "\n" + response.getBody());
            }
        });
    }

    private void cleanForm() {
        txtDescripcion.clear();
        txtTipo.getSelectionModel().clearSelection();
        txtEstatus.setSelected(true);
        btnGuardar.setText("Guardar");
        categoriaSelected = null;
        txtIdCategoria.clear();
        txtEstatus.setSelected(true);
    }

    private boolean isFormValid() {
        return !txtDescripcion.getText().trim().isEmpty() && txtTipo.getSelectionModel().getSelectedItem() != null;
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
