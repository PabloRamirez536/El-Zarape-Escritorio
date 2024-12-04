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
import org.utleon.elzarape.model.*;

import java.io.IOException;
import java.util.List;

public class EmpleadoController {
    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnLimpiar;

    @FXML
    private VBox btnInicio;

    @FXML
    private TableColumn<Empleado, String> colApellidos;

    @FXML
    private TableColumn<Empleado, String> colCiudad;

    @FXML
    private TableColumn<Empleado, String> colContrasenia;

    @FXML
    private TableColumn<Empleado, String> colEstado;

    @FXML
    private TableColumn<Empleado, String> colSucursal;

    @FXML
    private TableColumn<Empleado, String> colEstatus;

    @FXML
    private TableColumn<Empleado, Integer> colId;

    @FXML
    private TableColumn<Empleado, String> colNombre;

    @FXML
    private TableColumn<Empleado, String> colTelefono;

    @FXML
    private TableColumn<Empleado, String> colUsuario;

    @FXML
    private TableView<Empleado> tblEmpleados;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtIdEmpleado;

    @FXML
    private ComboBox<Ciudad> txtCiudad;

    @FXML
    private TextField txtContrasenia;

    @FXML
    private ComboBox<Estado> txtEstado;

    @FXML
    private ComboBox<Sucursal> txtSucursal;

    @FXML
    private CheckBox txtEstatus;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtUsuario;

    Globals globals = new Globals();
    ObservableList<Empleado> empleados;
    ObservableList<Estado> estados;
    ObservableList<Ciudad> ciudades;
    ObservableList<Sucursal> sucursales;
    Empleado empleadoSelected = null;

    public void initialize() {
        initColumns();
        txtIdEmpleado.setEditable(false);
        txtEstatus.setSelected(true);
        txtContrasenia.setEditable(false);
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
        txtSucursal.setConverter(new StringConverter<>() {
            @Override
            public String toString(Sucursal sucursal) {
                return sucursal != null ? sucursal.getNombre() : "";
            }

            @Override
            public Sucursal fromString(String string) {
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
        loadEmpleados();
        loadEstados();
        loadSucursales();
        tblEmpleados.setItems(empleados);
        txtEstado.setItems(estados);
        txtCiudad.setItems(ciudades);
        txtSucursal.setItems(sucursales);
        txtContrasenia.setText(generarContrasena());

        tblEmpleados.setOnMouseClicked(event -> {
            showEmpleadoSelected();
        });
        btnGuardar.setOnAction(event -> {
            boolean isModifying = btnGuardar.getText().equals("Modificar");

            if (isModifying && empleadoSelected == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText("No hay empleado seleccionado");
                alert.setContentText("Por favor, selecciona un empleado para modificar.");
                alert.showAndWait();
                return;
            }

            try {
                if (txtNombre.getText().isEmpty() || txtApellidos.getText().isEmpty() ||
                        txtTelefono.getText().isEmpty() || txtUsuario.getText().isEmpty() ||
                        txtContrasenia.getText().isEmpty() || txtCiudad.getSelectionModel().isEmpty() || txtSucursal.getSelectionModel().isEmpty()
                        || txtEstado.getSelectionModel().isEmpty()) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error de Validación");
                    alert.setHeaderText("Campos vacíos");
                    alert.setContentText("Por favor, completa todos los campos antes de continuar.");
                    alert.showAndWait();
                    return;
                }

                // Crear las instancias para el empleado
                Empleado empleado = new Empleado();
                if (isModifying) {
                    empleado.setIdEmpleado(empleadoSelected.getIdEmpleado());
                }
                empleado.setActivo(txtEstatus.isSelected());

                Persona persona = new Persona();
                persona.setNombre(txtNombre.getText());
                persona.setApellidos(txtApellidos.getText());
                persona.setTelefono(txtTelefono.getText());
                empleado.setPersona(persona);

                Usuario usuario = new Usuario();
                usuario.setNombre(txtUsuario.getText());
                usuario.setContrasenia(txtContrasenia.getText());
                empleado.setUsuario(usuario);

                Ciudad ciudad = txtCiudad.getSelectionModel().getSelectedItem();
                empleado.setCiudad(ciudad);

                Estado estado = txtEstado.getSelectionModel().getSelectedItem();
                empleado.setEstado(estado);

                Sucursal sucursal = txtSucursal.getSelectionModel().getSelectedItem();
                empleado.setSucursal(sucursal);

                String resultado;
                if (isModifying) {
                    resultado = modificarEmpleado(empleado);
                } else {
                    resultado = enviarEmpleado(empleado);
                }

                if (resultado != null) {
                    loadEmpleados(); // Recargar la lista
                    cleanForm(); // Limpiar el formulario
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(isModifying ? "Modificar Empleado" : "Agregar Empleado");
                    alert.setContentText(isModifying ? "Empleado modificado correctamente." : "Empleado agregado correctamente.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Ocurrió un error al " + (isModifying ? "modificar" : "agregar") + " el Empleado.");
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

    private String generarContrasena() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int longitud = 12;
        StringBuilder contrasena = new StringBuilder();

        for (int i = 0; i < longitud; i++) {
            int posicion = (int) (Math.random() * caracteres.length());
            contrasena.append(caracteres.charAt(posicion));
        }

        return contrasena.toString();
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
        colId.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getIdEmpleado()));
        colNombre.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getPersona().getNombre()));
        colApellidos.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getPersona().getApellidos()));
        colTelefono.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getPersona().getTelefono()));
        colUsuario.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getUsuario().getNombre()));
        colContrasenia.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getUsuario().getContrasenia()));
        colEstado.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getEstado().getNombre()));
        colCiudad.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getCiudad().getNombre()));
        colSucursal.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getSucursal().getNombre()));
        colEstatus.setCellValueFactory(col -> {
            Boolean activo = col.getValue().getActivo();
            return new SimpleObjectProperty<>(activo ? "Activo" : "Inactivo");
        });


    }

    private void loadEmpleados() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = Unirest.get(globals.BASE_URL + "empleado/getAllEmpleados").asString();
                Platform.runLater(() -> {
                    try {
                        Gson gson = new Gson();
                        Empleado[] empleadoArray = gson.fromJson(response.getBody(), Empleado[].class); // Convertir el JSON
                        empleados = FXCollections.observableArrayList(empleadoArray);
                        tblEmpleados.setItems(empleados);
                        tblEmpleados.refresh();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }



    private void cleanForm(){
        txtNombre.setText("");
        txtApellidos.setText("");
        txtTelefono.setText("");
        txtUsuario.setText("");
        txtContrasenia.setText(generarContrasena());
        txtIdEmpleado.setText("");
        txtCiudad.getSelectionModel().clearSelection();
        txtEstado.getSelectionModel().clearSelection();
        txtSucursal.getSelectionModel().clearSelection();
        txtEstatus.setSelected(true);
        btnGuardar.setText("Guardar");
        btnGuardar.setDisable(false);
    }

    private void loadSucursales() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = Unirest.get(globals.BASE_URL+"empleado/getAllSucursalesActivas").asString();
                if (response.getStatus() == 200) {
                    Gson gson = new Gson();
                    sucursales = FXCollections.observableArrayList(List.of(gson.fromJson(response.getBody(), Sucursal[].class)));
                    Platform.runLater(() -> txtSucursal.setItems(sucursales));
                } else {
                    System.err.println("Error al cargar sucursales: " + response.getStatus() + " - " + response.getBody());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadEstados() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = Unirest.get(globals.BASE_URL+"estado/getAllEstados").asString();
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
                        if (empleadoSelected != null) { // Check if a client is selected
                            txtCiudad.getSelectionModel().select(findCiudadById(empleadoSelected.getCiudad().getIdCiudad()));
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



    public void showEmpleadoSelected(){

        empleadoSelected = tblEmpleados.getSelectionModel().getSelectedItem();
        txtIdEmpleado.setText(String.valueOf(empleadoSelected.getIdEmpleado()));
        txtNombre.setText(empleadoSelected.getPersona().getNombre());
        txtApellidos.setText(empleadoSelected.getPersona().getApellidos());
        txtTelefono.setText(empleadoSelected.getPersona().getTelefono());
        txtUsuario.setText(empleadoSelected.getUsuario().getNombre());
        txtContrasenia.setText(empleadoSelected.getUsuario().getContrasenia());
        txtEstatus.setSelected(empleadoSelected.getActivo());
        txtEstado.getSelectionModel().select(findEstadoById(empleadoSelected.getEstado().getIdEstado()));
        txtSucursal.getSelectionModel().select(findSucursalById(empleadoSelected.getSucursal().getIdSucursal()));
        if (txtEstado.getSelectionModel().getSelectedItem() != null) {
            loadCiudades(txtEstado.getSelectionModel().getSelectedItem().getIdEstado());
        }
        btnGuardar.setText("Modificar");
    }

    public Estado findEstadoById(int id) {
        Estado estado = null;
        for (Estado item : estados) {
            if(item.getIdEstado() == id) {
                return item;
            }
        }
        return null;
    }

    public Ciudad findCiudadById(int id) {
        Ciudad ciudad = null;
        for (Ciudad item : ciudades) {
            if(item.getIdCiudad() == id) {
                return item;
            }
        }
        return null;
    }

    public Sucursal findSucursalById(int id) {
        Sucursal sucursal = null;
        for (Sucursal item : sucursales) {
            if(item.getIdSucursal() == id) {
                return item;
            }
        }
        return null;
    }


    public String enviarEmpleado(Empleado empleado) {
        try {
            Gson gson = new Gson();
            String empleadoJson = gson.toJson(empleado);
            System.out.println(empleadoJson);

            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "empleado/insertarEmpleado")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("datosEmpleado",empleadoJson)
                    .asString();


            if (response.getStatus() == 200 || response.getStatus() == 201){
                System.out.println("Empleado enviado exitosamente.");
                cleanForm();
                return response.getBody();
            } else {
                System.err.println("Error al enviar Empleado: " + response.getStatus() + " - " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String modificarEmpleado(Empleado empleado) {
        try {
            Gson gson = new Gson();
            String empleadoJson = gson.toJson(empleado);
            System.out.println(empleadoJson);

            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "empleado/actualizarEmpleado")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("datosEmpleado",empleadoJson) // Enviar el JSON por el body de la petición
                    .asString();

            // Validar la respuesta
            if (response.getStatus() == 200 || response.getStatus() == 201){
                System.out.println("Empleado actualizado exitosamente.");
                cleanForm();
                return response.getBody(); // El servidor responde con algún cuerpo JSON vacío
            } else {
                System.err.println("Error al actualizar Empleado: " + response.getStatus() + " - " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
