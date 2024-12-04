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
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.utleon.elzarape.model.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SucursalController {
    @FXML
    private Button btnGuardar;

    @FXML
    private VBox btnInicio;

    @FXML
    private Button btnLimpiar;

    @FXML
    private TableColumn<Sucursal, String> colCIudad;

    @FXML
    private TableColumn<Sucursal, String> colDireccion;

    @FXML
    private TableColumn<Sucursal, String> colEstado;

    @FXML
    private TableColumn<Sucursal, String> colEstatus;

    @FXML
    private TableColumn<Sucursal, String> colFoto;

    @FXML
    private TableColumn<Sucursal, String> colHorarios;

    @FXML
    private TableColumn<Sucursal, Integer> colId;

    @FXML
    private TableColumn<Sucursal, String> colLatitud;

    @FXML
    private TableColumn<Sucursal, String> colLongitud;

    @FXML
    private TableColumn<Sucursal, String> colNombre;

    @FXML
    private TableColumn<Sucursal, String> colURL;

    @FXML
    private TableView<Sucursal> tblSucursales;

    @FXML
    private TextField txtCalle;

    @FXML
    private ComboBox<Ciudad> txtCiudad;

    @FXML
    private TextField txtColonia;

    @FXML
    private ComboBox<Estado> txtEstado;

    @FXML
    private CheckBox txtEstatus;

    @FXML
    private TextField txtFoto;

    @FXML
    private TextField txtHorarios;

    @FXML
    private TextField txtIdSucursal;

    @FXML
    private TextField txtLatitud;

    @FXML
    private TextField txtLongitud;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtNumCalle;

    @FXML
    private TextField txtUrl;

    Globals globals = new Globals();
    ObservableList<Sucursal> sucursales;
    ObservableList<Estado> estados;
    ObservableList<Ciudad> ciudades;
    Sucursal sucursalSelected = null;
    private String imagen = "../../recursos/media/sucursal.jpg";

    public void initialize() {
        initColumns();
        txtIdSucursal.setEditable(false);
        txtEstatus.setSelected(true);
        txtFoto.setText(imagen);

        // Permitir selección de una sola fila en la tabla
        tblSucursales.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        txtEstado.setConverter(new StringConverter<>() {
            @Override
            public String toString(Estado estado) {
                return estado != null ? estado.getNombre() : "";
            }

            @Override
            public Estado fromString(String string) {
                return null; // No se usa
            }
        });
        txtCiudad.setConverter(new StringConverter<>() {
            @Override
            public String toString(Ciudad ciudad) {
                return ciudad != null ? ciudad.getNombre() : "";
            }

            @Override
            public Ciudad fromString(String string) {
                return null; // No se usa
            }
        });
        // Carga los estados al inicio
        txtEstado.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadCiudades(newValue.getIdEstado()); // Filtrar ciudades según el estado seleccionado
            }
        });
        btnLimpiar.setOnAction(event -> cleanForm());
        loadSucursales();
        loadEstados();
        tblSucursales.setItems(sucursales);
        txtEstado.setItems(estados);
        txtCiudad.setItems(ciudades);

        tblSucursales.setOnMouseClicked(event -> {
            showSucursalSelected();
        });
        btnGuardar.setOnAction(event -> {
            boolean isModifying = btnGuardar.getText().equals("Modificar");

            if (isModifying && sucursalSelected == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText("No hay sucursal seleccionada");
                alert.setContentText("Por favor, selecciona una sucursal para modificar.");
                alert.showAndWait();
                return;
            }

            try {
                // Validación de campos
                if (txtNombre.getText().isEmpty() || txtLatitud.getText().isEmpty() ||
                        txtLongitud.getText().isEmpty() || txtFoto.getText().isEmpty() ||
                        txtUrl.getText().isEmpty() || txtHorarios.getText().isEmpty() ||
                        txtCalle.getText().isEmpty() || txtNumCalle.getText().isEmpty() ||
                        txtColonia.getText().isEmpty() || txtCiudad.getSelectionModel().isEmpty() ||
                        txtEstado.getSelectionModel().isEmpty()) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error de Validación");
                    alert.setHeaderText("Campos vacíos");
                    alert.setContentText("Por favor, completa todos los campos antes de continuar.");
                    alert.showAndWait();
                    return;
                }

                // Crear la instancia de Sucursal
                Sucursal sucursal = new Sucursal();
                if (isModifying) {
                    System.out.println("ID de sucursal seleccionada: " + sucursalSelected.getIdSucursal());
                    sucursal.setIdSucursal(sucursalSelected.getIdSucursal());
                }
                sucursal.setNombre(txtNombre.getText());
                sucursal.setLatitud(txtLatitud.getText());
                sucursal.setLongitud(txtLongitud.getText());
                sucursal.setFoto(txtFoto.getText());
                sucursal.setUrlWeb(txtUrl.getText());
                sucursal.setHorarios(txtHorarios.getText());
                sucursal.setCalle(txtCalle.getText());
                sucursal.setNumCalle(txtNumCalle.getText());
                sucursal.setColonia(txtColonia.getText());
                sucursal.setActivo(txtEstatus.isSelected());

                Ciudad ciudad = txtCiudad.getSelectionModel().getSelectedItem();
                sucursal.setCiudadNombre(ciudad);

                Estado estado = txtEstado.getSelectionModel().getSelectedItem();
                sucursal.setEstadoNombre(estado);

                String resultado;
                if (isModifying) {
                    resultado = modificarSucursal(sucursal);
                } else {
                    resultado = enviarSucursal(sucursal);
                }

                if (resultado != null) {
                    loadSucursales(); // Recargar la lista
                    cleanForm(); // Limpiar el formulario
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(isModifying ? "Modificar Sucursal" : "Agregar Sucursal");
                    alert.setContentText(isModifying ? "Sucursal modificada correctamente." : "Sucursal agregada correctamente.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Ocurrió un error al " + (isModifying ? "modificar" : "agregar") + " la Sucursal.");
                    alert.showAndWait();
                }

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error inesperado");
                alert.setContentText("Ocurrió un error al procesar la operación. Por favor, verifica los datos.");
                alert.showAndWait();
                e.printStackTrace();
            }
        });

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
        colId.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getIdSucursal()));
        colNombre.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getNombre()));
        colLatitud.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getLatitud()));
        colLongitud.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getLongitud()));
        colFoto.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getFoto()));
        colURL.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getUrlWeb()));
        colHorarios.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getHorarios()));
        colDireccion.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getDireccion()));
        colEstado.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getEstadoNombre().getNombre()));
        colCIudad.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getCiudadNombre().getNombre()));
        colEstatus.setCellValueFactory(col -> {
            Boolean activo = col.getValue().isActivo();
            return new SimpleObjectProperty<>(activo ? "Activo" : "Inactivo");
        });

    }

    private void loadSucursales() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = Unirest.get(globals.BASE_URL + "sucursal/getAllSucursales").asString();
                Platform.runLater(() -> {
                    try {
                        Gson gson = new Gson();
                        Sucursal[] sucursalArray = gson.fromJson(response.getBody(), Sucursal[].class); // Convertir el JSON
                        sucursales = FXCollections.observableArrayList(sucursalArray);
                        tblSucursales.setItems(sucursales);
                        tblSucursales.refresh();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }


    private void cleanForm() {
        txtNombre.setText("");
        txtLatitud.setText("");
        txtLongitud.setText("");
        txtFoto.setText(imagen);
        txtUrl.setText("");
        txtHorarios.setText("");
        txtCalle.setText("");
        txtNumCalle.setText("");
        txtColonia.setText("");
        txtIdSucursal.setText("");
        txtCiudad.getSelectionModel().clearSelection();
        txtEstado.getSelectionModel().clearSelection();
        txtEstatus.setSelected(true);
        btnGuardar.setText("Guardar");
        btnGuardar.setDisable(false);
    }


    private void loadEstados() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = Unirest.get(globals.BASE_URL + "estado/getAllEstados").asString();
                if (response.getStatus() == 200) {
                    Gson gson = new Gson();
                    estados = FXCollections.observableArrayList(List.of(gson.fromJson(response.getBody(), Estado[].class)));
                    Platform.runLater(() -> txtEstado.setItems(estados));
                } else {
                    System.err.println("Error al cargar estados: " + response.getStatus() + " - " + response.getBody());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadCiudades(int idEstado) {
        new Thread(() -> {
            try {
                HttpResponse<String> response = Unirest.get(globals.BASE_URL + "ciudad/getCiudadesPorEstado?idEstado=" + idEstado).asString();
                if (response.getStatus() == 200) {
                    Gson gson = new Gson();
                    ciudades = FXCollections.observableArrayList(List.of(gson.fromJson(response.getBody(), Ciudad[].class)));
                    Platform.runLater(() -> {
                        txtCiudad.setItems(ciudades);
                        // Si hay una sucursal seleccionada, selecciona la ciudad correspondiente
                        if (sucursalSelected != null) {
                            txtCiudad.getSelectionModel().select(findCiudadById(sucursalSelected.getCiudadNombre().getIdCiudad()));
                        }
                    });
                } else {
                    System.err.println("Error al cargar ciudades: " + response.getStatus() + " - " + response.getBody());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void showSucursalSelected() {
        sucursalSelected = tblSucursales.getSelectionModel().getSelectedItem();
        if (sucursalSelected != null) {
            txtIdSucursal.setText(String.valueOf(sucursalSelected.getIdSucursal()));
            txtNombre.setText(sucursalSelected.getNombre());
            txtLatitud.setText(sucursalSelected.getLatitud());
            txtLongitud.setText(sucursalSelected.getLongitud());
            txtFoto.setText(sucursalSelected.getFoto());
            txtUrl.setText(sucursalSelected.getUrlWeb());
            txtHorarios.setText(sucursalSelected.getHorarios());

            // Descomponer la dirección concatenada
            descomponerDireccion(sucursalSelected.getDireccion());

            txtEstatus.setSelected(sucursalSelected.isActivo());
            txtEstado.getSelectionModel().select(findEstadoById(sucursalSelected.getEstadoNombre().getIdEstado()));

            if (txtEstado.getSelectionModel().getSelectedItem() != null) {
                loadCiudades(txtEstado.getSelectionModel().getSelectedItem().getIdEstado());
            }

            // Cambiar el texto del botón a "Modificar"
            btnGuardar.setText("Modificar");
        }
    }

    // Metodo para descomponer la dirección
    private void descomponerDireccion(String direccionCompleta) {
        if (direccionCompleta != null && !direccionCompleta.isEmpty()) {
            String[] partes = direccionCompleta.split(",");
            String calleCompleta = partes[0].trim();
            String numCalle = calleCompleta.substring(calleCompleta.lastIndexOf(' ') + 1).trim();
            String calle = calleCompleta.substring(0, calleCompleta.lastIndexOf(' ')).trim();
            String colonia = partes.length > 1 ? partes[1].trim() : "";

            txtCalle.setText(calle);
            txtNumCalle.setText(numCalle);
            txtColonia.setText(colonia);

        } else {
            // Si la dirección está vacía, limpiar los campos
            txtCalle.setText("");
            txtNumCalle.setText("");
            txtColonia.setText("");
        }
    }


    public Estado findEstadoById(int id) {
        Estado estado = null;
        for (Estado item : estados) {
            if (item.getIdEstado() == id) {
                return item;
            }
        }
        return null;
    }

    public Ciudad findCiudadById(int id) {
        Ciudad ciudad = null;
        for (Ciudad item : ciudades) {
            if (item.getIdCiudad() == id) {
                return item;
            }
        }
        return null;
    }

    public String enviarSucursal(Sucursal sucursal) {
        try {
            Gson gson = new Gson();
            String sucursalJson = gson.toJson(sucursal);
            System.out.println("Datos de sucursal a enviar: " + sucursalJson);

            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "sucursal/insertSucursal")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("datosSucursal",sucursalJson)
                    .asString();

            if (response.getStatus() == 200 || response.getStatus() == 201) {
                System.out.println("Sucursal enviada exitosamente.");
                cleanForm();
                return response.getBody();
            } else {
                System.err.println("Error al enviar sucursal: " + response.getStatus() + " - " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String modificarSucursal(Sucursal sucursal) {
        try {
            Gson gson = new Gson();
            String sucursalJson = gson.toJson(sucursal);
            System.out.println("Datos de sucursal a modificar: " + sucursalJson);

            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "sucursal/updateSucursal")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("datosSucursal", sucursalJson)
                    .asString();

            if (response.getStatus() == 200 || response.getStatus() == 204) {
                System.out.println("Sucursal actualizada exitosamente.");
                System.out.println("Respuesta del servidor: " + response.getBody());
                // Aquí podrías procesar la respuesta si deseas obtener datos actualizados
                return response.getBody(); // Asegúrate de que tu servidor devuelve una respuesta adecuada
            } else {
                System.err.println("Error al actualizar sucursal: " + response.getStatus() + " - " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}