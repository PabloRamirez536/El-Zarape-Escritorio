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
import org.utleon.elzarape.model.Bebida;
import org.utleon.elzarape.model.Categoria;
import org.utleon.elzarape.model.Producto;

import java.io.IOException;
import java.util.List;

public class BebidaController {

    @FXML
    private TextField txtIdBebida;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private TableColumn<Bebida, String> colDescripcion;

    @FXML
    private TableColumn<Bebida, String> colEstatus;

    @FXML
    private TableColumn<Bebida, String> colFoto;

    @FXML
    private TableColumn<Bebida, Integer> colIdBebida;

    @FXML
    private TableColumn<Bebida, String> colNombre;

    @FXML
    private TableColumn<Bebida, Double> colPrecio;

    @FXML
    private TableView<Bebida> tblBebidas;

    @FXML
    private ComboBox<Categoria> txtCategoria;

    @FXML
    private TableColumn<Bebida, String> colTipoCategoria;

    @FXML
    private VBox btnInicio;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TextField txtFoto;

    @FXML
    private CheckBox txtEstatus;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecio;

    Globals globals = new Globals();
    ObservableList<Bebida> bebida;
    ObservableList<Categoria> categoria;
    Bebida Bebidaelected = null;
    private String imagen = "../../recursos/media/refresco.png";

    @FXML
    public void initialize() {
        txtIdBebida.setEditable(false);
        txtEstatus.setSelected(true);
        txtFoto.setText(imagen);
        initColumns();
        txtCategoria.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria Categoria) {
                return Categoria != null ? Categoria.getDescripcion() : "";
            }

            @Override
            public Categoria fromString(String nombre) {
                // Este método no se usará en este caso
                return null;
            }
        });
        loadBebida();
        loadCategoria("");
        tblBebidas.setItems(bebida);
        txtCategoria.setItems(categoria);

        tblBebidas.setOnMouseClicked(event -> {
            if (tblBebidas.getSelectionModel().getSelectedItem() != null) {
                showBebidaelected();
            }
        });

        btnGuardar.setOnAction(event -> {
            boolean isModifying = btnGuardar.getText().equals("Modificar");

            if (isModifying && Bebidaelected == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText("No hay bebida seleccionada");
                alert.setContentText("Por favor, selecciona una bebida para modificar.");
                alert.showAndWait();
                return;
            }

            try {
                double precio = Double.parseDouble(txtPrecio.getText());
                if (precio < 0) {
                    throw new NumberFormatException("El precio no puede ser negativo");
                }

                Producto producto = new Producto();
                if (isModifying) {
                    producto.setIdProducto(Bebidaelected.getProducto().getIdProducto());
                }
                if(txtNombre.getText().isEmpty() || txtDescripcion.getText().isEmpty() || txtPrecio.getText().isEmpty() || txtCategoria.getSelectionModel().isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error de Incersión");
                    alert.setHeaderText("Campos vacios");
                    alert.setContentText("Algunos campos se encuentran vacios");
                    alert.showAndWait();
                }else {
                    producto.setNombre(txtNombre.getText());
                    producto.setDescripcion(txtDescripcion.getText());
                    producto.setFoto(imagen);
                    producto.setPrecio(precio);
                    producto.setActivo(txtEstatus.isSelected());

                    Categoria Categoria = new Categoria();
                    Categoria.setIdCategoria(txtCategoria.getSelectionModel().getSelectedItem().getIdCategoria());

                    Bebida Bebida = new Bebida();
                    if (isModifying) {
                        Bebida.setIdBebida(Bebidaelected.getIdBebida());
                        Bebida.setIdProducto(Bebidaelected.getIdProducto());
                    }
                    Bebida.setProducto(producto);
                    Bebida.setCategoria(Categoria);

                    String salida;
                    if (isModifying) {
                        salida = modificarProducto(Bebida);
                    } else {
                        salida = enviarProducto(Bebida);
                    }

                    if (salida != null) {
                        loadBebida();
                        cleanForm();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(isModifying ? "Modificar Bebida" : "Agregar Bebida");
                        alert.setContentText(isModifying ? "Bebida modificada correctamente." : "Bebida agregada correctamente.");
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("Ocurrió un error al " + (isModifying ? "modificar" : "agregar") + " la bebida.");
                        alert.showAndWait();
                    }
                }

            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de Validación");
                alert.setHeaderText("Precio Inválido");
                alert.setContentText("Por favor, ingrese un precio válido.");
                alert.showAndWait();
            }
        });
        btnCancelar.setOnAction( actionEvent -> {
            cleanForm();
        });

        btnInicio.setOnMouseClicked(event -> {
            try {
                cargarModuloPrincipal();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
    private void initColumns() {
        colIdBebida.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getIdBebida()));
        colNombre.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getProducto().getNombre()));
        colDescripcion.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getProducto().getDescripcion()));
        colFoto.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getProducto().getFoto()));
        colPrecio.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getProducto().getPrecio()));
        colEstatus.setCellValueFactory(col -> new SimpleObjectProperty<> (col.getValue().getProducto().getActivo() ? "Activo" : "Inactivo"));
        colTipoCategoria.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getCategoria().getDescripcion()));
    }
    private void loadBebida() {
        new Thread(() ->{
            HttpResponse<String> response = Unirest.get(globals.BASE_URL+"bebida/getAllBebida").asString();
            Platform.runLater(() ->{
                Gson gson = new Gson();
                System.out.println(response.getBody());
                bebida = FXCollections.observableArrayList(List.of(gson.fromJson(response.getBody(),Bebida[].class)));
                bebida.forEach(Bebida -> System.out.println("ID "+Bebida.getProducto().getIdProducto()));
                tblBebidas.setItems(bebida);
                tblBebidas.refresh();
            });
        }).start();
    }
    private void cleanForm(){
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtIdBebida.setText("");
        btnGuardar.setDisable(false);
        btnGuardar.setText("Guardar");
        txtEstatus.setSelected(true);
    }
    private void loadCategoria(String filtro) {
        new Thread(() ->{
            HttpResponse<String> response = Unirest.get(globals.BASE_URL+"bebida/getAllCategoriaBebida").asString();
            Platform.runLater(() ->{
                Gson gson = new Gson();
                categoria = FXCollections.observableArrayList(List.of(gson.fromJson(response.getBody(), Categoria[].class)));
                txtCategoria.setItems(categoria);
            });
        }).start();
    }
    public void showBebidaelected(){
        Bebidaelected = tblBebidas.getSelectionModel().getSelectedItem();
        txtIdBebida.setText(String.valueOf(Bebidaelected.getIdBebida()));
        txtNombre.setText(Bebidaelected.getProducto().getNombre());
        txtDescripcion.setText(Bebidaelected.getProducto().getDescripcion());
        txtFoto.setText(Bebidaelected.getProducto().getFoto());
        txtPrecio.setText(String.valueOf(Bebidaelected.getProducto().getPrecio()));
        txtCategoria.getSelectionModel().select(findCategoriaById(Bebidaelected.getProducto().getIdCategoria()));
        txtEstatus.setSelected(Bebidaelected.getProducto().getActivo());
        btnGuardar.setText("Modificar");
    }

    public Categoria findCategoriaById(int id) {
        for (Categoria item : categoria) {
            if(item.getIdCategoria() == id) {
                return item;
            }
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
        stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    public String enviarProducto(Bebida Bebida) {
        try {
            Gson gson = new Gson();
            String BebidaJson = gson.toJson(Bebida);

            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "bebida/insertBebida")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("datosBebida",BebidaJson)
                    .asString();


            if (response.getStatus() == 200 || response.getStatus() == 201){
                System.out.println("Bebida enviada exitosamente.");
                cleanForm();
                return response.getBody();
            } else {
                System.err.println("Error al enviar bebida: " + response.getStatus() + " - " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String modificarProducto(Bebida Bebida) {
        try {
            Gson gson = new Gson();
            String BebidaJson = gson.toJson(Bebida);

            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "bebida/updateBebida")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("datosBebida",BebidaJson) // Enviar el JSON por el body de la petición
                    .asString();

            // Validar la respuesta
            if (response.getStatus() == 200 || response.getStatus() == 201){
                System.out.println("Bebida actualizada exitosamente.");
                cleanForm();
                return response.getBody(); // El servidor responde con algún cuerpo JSON vacío
            } else {
                System.err.println("Error al actualizar bebida: " + response.getStatus() + " - " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

