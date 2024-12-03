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
import org.utleon.elzarape.model.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteController {

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnLimpiar;


    @FXML
    private VBox btnInicio;

    @FXML
    private TableColumn<Cliente, String> colApellidos;

    @FXML
    private TableColumn<Cliente, String> colCiudad;

    @FXML
    private TableColumn<Cliente, String> colContrasenia;

    @FXML
    private TableColumn<Cliente, String> colEstado;

    @FXML
    private TableColumn<Cliente, String> colEstatus;

    @FXML
    private TableColumn<Cliente, Integer> colId;

    @FXML
    private TableColumn<Cliente, String> colNombre;

    @FXML
    private TableColumn<Cliente, String> colTelefono;

    @FXML
    private TableColumn<Cliente, String> colUsuario;

    @FXML
    private TableView<Cliente> tblClientes;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtIdCliente;

    @FXML
    private ComboBox<Ciudad> txtCiudad;

    @FXML
    private TextField txtContrasenia;

    @FXML
    private ComboBox<Estado> txtEstado;

    @FXML
    private CheckBox txtEstatus;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtUsuario;

    Globals globals = new Globals();
    ObservableList<Cliente> clientes;
    ObservableList<Estado> estados;
    ObservableList<Ciudad> ciudades;
    Cliente clienteSelected = null;

    public void initialize() {
        initColumns();
        txtIdCliente.setEditable(false);
        txtEstatus.setSelected(true);
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
        loadClientes();
        loadEstados();
        tblClientes.setItems(clientes);
        txtEstado.setItems(estados);
        txtCiudad.setItems(ciudades);

        tblClientes.setOnMouseClicked(event -> {
            showClienteSelected();
        });
        btnGuardar.setOnAction(event -> {
            boolean isModifying = btnGuardar.getText().equals("Modificar");

            if (isModifying && clienteSelected == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText("No hay cliente seleccionado");
                alert.setContentText("Por favor, selecciona un cliente para modificar.");
                alert.showAndWait();
                return;
            }

            try {
                if (txtNombre.getText().isEmpty() || txtApellidos.getText().isEmpty() ||
                        txtTelefono.getText().isEmpty() || txtUsuario.getText().isEmpty() ||
                        txtContrasenia.getText().isEmpty() || txtCiudad.getSelectionModel().isEmpty() ||
                        txtEstado.getSelectionModel().isEmpty()) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error de Validación");
                    alert.setHeaderText("Campos vacíos");
                    alert.setContentText("Por favor, completa todos los campos antes de continuar.");
                    alert.showAndWait();
                    return;
                }

                // Crear las instancias para el cliente
                Cliente cliente = new Cliente();
                if (isModifying) {
                    cliente.setIdCliente(clienteSelected.getIdCliente()); // Aseguramos que se incluye el ID
                }
                cliente.setActivo(txtEstatus.isSelected());

                Persona persona = new Persona();
                persona.setNombre(txtNombre.getText());
                persona.setApellidos(txtApellidos.getText());
                persona.setTelefono(txtTelefono.getText());
                cliente.setPersona(persona);

                Usuario usuario = new Usuario();
                usuario.setNombre(txtUsuario.getText());
                usuario.setContrasenia(txtContrasenia.getText());
                cliente.setUsuario(usuario);

                Ciudad ciudad = txtCiudad.getSelectionModel().getSelectedItem();
                cliente.setCiudad(ciudad);

                Estado estado = txtEstado.getSelectionModel().getSelectedItem();
                cliente.setEstado(estado);

                String resultado;
                if (isModifying) {
                    resultado = modificarCliente(cliente);
                } else {
                    resultado = enviarCliente(cliente);
                }

                if (resultado != null) {
                    loadClientes(); // Recargar la lista
                    cleanForm(); // Limpiar el formulario
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(isModifying ? "Modificar Cliente" : "Agregar Cliente");
                    alert.setContentText(isModifying ? "Cliente modificado correctamente." : "Cliente agregado correctamente.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Ocurrió un error al " + (isModifying ? "modificar" : "agregar") + " el cliente.");
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
        colId.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getIdCliente()));
        colNombre.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getPersona().getNombre()));
        colApellidos.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getPersona().getApellidos()));
        colTelefono.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getPersona().getTelefono()));
        colUsuario.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getUsuario().getNombre()));
        colContrasenia.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getUsuario().getContrasenia()));
        colEstado.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getEstado().getNombre()));
        colCiudad.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getCiudad().getNombre()));
        colEstatus.setCellValueFactory(col -> {
            Boolean activo = col.getValue().getActivo();
            return new SimpleObjectProperty<>(activo ? "Activo" : "Inactivo");
        });


    }

    private void loadClientes() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = Unirest.get(globals.BASE_URL + "cliente/getAllClientes").asString();
                Platform.runLater(() -> {
                    try {
                        Gson gson = new Gson();
                        Cliente[] clienteArray = gson.fromJson(response.getBody(), Cliente[].class); // Convertir el JSON
                        clientes = FXCollections.observableArrayList(clienteArray);
                        tblClientes.setItems(clientes);
                        tblClientes.refresh();
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
        txtContrasenia.setText("");
        txtIdCliente.setText("");
        txtCiudad.getSelectionModel().clearSelection();
        txtEstado.getSelectionModel().clearSelection();
        txtEstatus.setSelected(true);
        btnGuardar.setText("Guardar");
        btnGuardar.setDisable(false);
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
                        if (clienteSelected != null) { // Check if a client is selected
                            txtCiudad.getSelectionModel().select(findCiudadById(clienteSelected.getCiudad().getIdCiudad()));
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



    public void showClienteSelected(){

        clienteSelected = tblClientes.getSelectionModel().getSelectedItem();
        txtIdCliente.setText(String.valueOf(clienteSelected.getIdCliente()));
        txtNombre.setText(clienteSelected.getPersona().getNombre());
        txtApellidos.setText(clienteSelected.getPersona().getApellidos());
        txtTelefono.setText(clienteSelected.getPersona().getTelefono());
        txtUsuario.setText(clienteSelected.getUsuario().getNombre());
        txtContrasenia.setText(clienteSelected.getUsuario().getContrasenia());
        txtEstatus.setSelected(clienteSelected.getActivo());
        txtEstado.getSelectionModel().select(findEstadoById(clienteSelected.getEstado().getIdEstado()));
        //txtCiudad.getSelectionModel().select(findCiudadById(clienteSelected.getCiudad().getIdCiudad()));
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


    public String enviarCliente(Cliente cliente) {
        try {
            Gson gson = new Gson();
            String clienteJson = gson.toJson(cliente);
            System.out.println(clienteJson);

            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "cliente/insertarCliente")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("datosCliente",clienteJson)
                    .asString();


            if (response.getStatus() == 200 || response.getStatus() == 201){
                System.out.println("Cliente enviado exitosamente.");
                cleanForm();
                return response.getBody();
            } else {
                System.err.println("Error al enviar cliente: " + response.getStatus() + " - " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String modificarCliente(Cliente cliente) {
        try {
            Gson gson = new Gson();
            String clienteJson = gson.toJson(cliente);
            System.out.println(clienteJson);

            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "cliente/actualizarCliente")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("datosCliente",clienteJson) // Enviar el JSON por el body de la petición
                    .asString();

            // Validar la respuesta
            if (response.getStatus() == 200 || response.getStatus() == 201){
                System.out.println("Cliente actualizado exitosamente.");
                cleanForm();
                return response.getBody(); // El servidor responde con algún cuerpo JSON vacío
            } else {
                System.err.println("Error al actualizar cliente: " + response.getStatus() + " - " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
