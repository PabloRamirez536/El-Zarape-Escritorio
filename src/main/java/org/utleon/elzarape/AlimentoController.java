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
import javafx.util.StringConverter;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.utleon.elzarape.model.Alimento;
import org.utleon.elzarape.model.Bebida;
import org.utleon.elzarape.model.Categoria;
import org.utleon.elzarape.model.Producto;

import java.io.IOException;
import java.util.List;

public class AlimentoController {

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private VBox btnInicio;

    @FXML
    private TableColumn<Alimento, Integer> colIdAlimento;

    @FXML
    private TableColumn<Alimento, String> colNombre;

    @FXML
    private TableColumn<Alimento, String> colDescripcion;

    @FXML
    private TableColumn<Alimento, String> colFoto;

    @FXML
    private TableColumn<Alimento, Double> colPrecio;

    @FXML
    private TableColumn<Alimento, String> colEstatus;

    @FXML
    private TableColumn<Alimento, String> colTipoCategoria;

    @FXML
    private TableView<Alimento> tblAlimentos;

    @FXML
    private ComboBox<Categoria> txtCategoria;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private CheckBox txtEstatus;

    @FXML
    private TextField txtFoto;

    @FXML
    private TextField txtIdAlimento;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecio;

    Globals globals = new Globals();
    ObservableList<Alimento> alimento;
    ObservableList<Categoria> categoria;
    Alimento AlimentoSelected = null;
    private String imagen = "../../recursos/media/alimentos.png";

    @FXML
    public void initialize() {
        txtIdAlimento.setEditable(false);
        txtEstatus.setSelected(true);
        txtFoto.setText(imagen);
        txtFoto.setEditable(false);
        initColumns();
        setupCategoryComboBox();
        loadAlimento();  // Cargar alimentos al iniciar
        loadCategoria();  // Cargar categorías al iniciar
        setupTableClickEvent();
        setupButtonActions();

        btnInicio.setOnMouseClicked(event -> {
            try {
                cargarModuloPrincipal();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void initColumns() {
        colIdAlimento.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getIdAlimento()));
        colNombre.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getProducto().getNombre()));
        colDescripcion.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getProducto().getDescripcion()));
        colFoto.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getProducto().getFoto()));
        colPrecio.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getProducto().getPrecio()));
        colEstatus.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getProducto().getActivo() ? "Activo" : "Inactivo"));
        colTipoCategoria.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getCategoria().getDescripcion()));
    }

    private void setupCategoryComboBox() {
        txtCategoria.setConverter(new StringConverter<>() {
            public String toString(Categoria categoria) {
                return categoria != null ? categoria.getDescripcion() : "";
            }

            @Override
            public Categoria fromString(String nombre) {
                return null; // Adaptar según el caso de uso
            }
        });
    }

    private void setupTableClickEvent() {
        tblAlimentos.setOnMouseClicked(event -> {
            if (tblAlimentos.getSelectionModel().getSelectedItem() != null) {
                showAlimentoSelected();
            }
        });
    }

    private void setupButtonActions() {
        btnGuardar.setOnAction(event -> handleSaveAction());
        btnCancelar.setOnAction(actionEvent -> cleanForm());
        btnInicio.setOnMouseClicked(event -> {
            try {
                cargarModuloPrincipal();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleSaveAction() {
        boolean isModifying = btnGuardar.getText().equals("Modificar");

        if (isModifying && AlimentoSelected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "No hay alimento seleccionado", "Por favor, selecciona un alimento para modificar.");
            return;
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText());
            if (precio < 0) throw new NumberFormatException("El precio no puede ser negativo");

            if (isFormValid()) {
                Producto producto = createProducto(precio);
                Alimento alimento = createAlimento(isModifying, producto);

                String salida = isModifying ? modificarProducto(alimento) : enviarProducto(alimento);
                handleResponse(isModifying, salida);
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", "Precio Inválido", "Por favor, ingrese un precio válido.");
        }
    }

    private Producto createProducto(double precio) {
        Producto producto = new Producto();
        producto.setNombre(txtNombre.getText());
        producto.setDescripcion(txtDescripcion.getText());
        producto.setFoto(imagen);
        producto.setPrecio(precio);
        producto.setActivo(txtEstatus.isSelected());
        return producto;
    }

    private Alimento createAlimento(boolean isModifying, Producto producto) {
        Alimento alimento = new Alimento();
        if (isModifying) {
            alimento.setIdAlimento(AlimentoSelected.getIdAlimento());
            alimento.setIdProducto(AlimentoSelected.getProducto().getIdProducto());
        }
        alimento.setProducto(producto);
        alimento.setCategoria(txtCategoria.getSelectionModel().getSelectedItem());
        return alimento;
    }

    private void handleResponse(boolean isModifying, String salida) {
        if (salida != null) {
            loadAlimento();
            cleanForm();
            showAlert(Alert.AlertType.INFORMATION, isModifying ? "Modificar Alimento" : "Agregar Alimento", null, "Alimento " + (isModifying ? "modificado" : "agregado") + " correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Ocurrió un error al " + (isModifying ? "modificar" : "agregar") + " el alimento.");
        }
    }

    private void loadAlimento() {
        new Thread(() ->{
            HttpResponse<String> response = Unirest.get(globals.BASE_URL+"alimento/getAllAlimento").asString();
            Platform.runLater(() ->{
                Gson gson = new Gson();
                System.out.println(response.getBody());
                alimento = FXCollections.observableArrayList(List.of(gson.fromJson(response.getBody(), Alimento[].class)));
                alimento.forEach(Alimento -> System.out.println("ID "+Alimento.getProducto().getIdProducto()));
                tblAlimentos.setItems(alimento);
                tblAlimentos.refresh();
            });
        }).start();
    }


    private void cleanForm() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtIdAlimento.setText("");
        btnGuardar.setDisable(false);
        btnGuardar.setText("Guardar");
        txtEstatus.setSelected(true);
        txtCategoria.getSelectionModel().clearSelection();
        txtEstatus.setSelected(true);
    }

    private void loadCategoria() {
        new Thread(() ->{
            HttpResponse<String> response = Unirest.get(globals.BASE_URL+"alimento/getAllCategoriaAlimento").asString();
            Platform.runLater(() ->{
                Gson gson = new Gson();
                categoria = FXCollections.observableArrayList(List.of(gson.fromJson(response.getBody(), Categoria[].class)));
                txtCategoria.setItems(categoria);
            });
        }).start();
    }

    public void showAlimentoSelected() {
        AlimentoSelected = tblAlimentos.getSelectionModel().getSelectedItem();
        txtIdAlimento.setText(String.valueOf(AlimentoSelected.getIdAlimento()));
        txtNombre.setText(AlimentoSelected.getProducto().getNombre());
        txtDescripcion.setText(AlimentoSelected.getProducto().getDescripcion());
        txtFoto.setText(AlimentoSelected.getProducto().getFoto());
        txtPrecio.setText(String.valueOf(AlimentoSelected.getProducto().getPrecio()));
        txtCategoria.getSelectionModel().select(findCategoriaById(AlimentoSelected.getProducto().getIdCategoria()));
        txtEstatus.setSelected(AlimentoSelected.getProducto().getActivo());
        btnGuardar.setText("Modificar");
    }

    public Categoria findCategoriaById(int id) {
        for (Categoria item : categoria) {
            if (item.getIdCategoria() == id) return item;
        }
        return null;
    }

    public void cargarModuloPrincipal() throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(LoginController.class.getResource("inicio.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("El Zarape - Sistema de Gestión");
        stage.setMaximized(true);
        stage.show();
        ((Stage) btnGuardar.getScene().getWindow()).close();
    }

    public String enviarProducto(Alimento alimento) {
        try {
            Gson gson = new Gson();
            String alimentoJson = gson.toJson(alimento);
            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "alimento/insertAlimento")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("datosAlimento", alimentoJson)
                    .asString();
            return response.getStatus() == 200 || response.getStatus() == 201 ? response.getBody() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String modificarProducto(Alimento alimento) {
        try {
            Gson gson = new Gson();
            String alimentoJson = gson.toJson(alimento);
            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "alimento/updateAlimento")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("datosAlimento", alimentoJson)
                    .asString();
            return response.getStatus() == 200 || response.getStatus() == 201 ? response.getBody() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isFormValid() {
        return !txtNombre.getText().trim().isEmpty() &&
                !txtDescripcion.getText().trim().isEmpty() &&
                !txtPrecio.getText().trim().isEmpty() &&
                txtCategoria.getSelectionModel().getSelectedItem() != null;
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
