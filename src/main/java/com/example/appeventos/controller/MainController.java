package com.example.appeventos.controller;

import com.example.appeventos.model.Evento;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {

    @FXML private VBox contenedorCentral;

    @FXML
    private void initialize() {
        mostrarInicio();
    }

    @FXML
    private void mostrarInicio() {
        cargarVista("/com/example/appeventos/view/inicio-view.fxml");
    }

    @FXML
    private void nuevoEvento() {
        mostrarFormulario("Música", null);
    }

    @FXML
    private void mostrarListado() {
        try {
            FXMLLoader loader = crearLoader("/com/example/appeventos/view/listado-eventos-view.fxml");
            Parent vista = loader.load();
            ListadoEventosController controller = loader.getController();
            controller.configurar(this::editarEvento);
            mostrarContenido(vista);
        } catch (IOException e) {
            mostrarError("No se pudo abrir el listado de eventos: " + e.getMessage());
        }
    }

    public void mostrarFormulario(String categoria, Evento evento) {
        // Cargamos formulario-evento-view.fxml, que muestra la vista de los formularios de los eventos.
        try {
            FXMLLoader loader = crearLoader("/com/example/appeventos/view/formulario-evento-view.fxml");
            Parent vista = loader.load();
            FormularioEventoController controller = loader.getController();
            controller.configurar(categoria, evento, guardado -> mostrarListado());
            mostrarContenido(vista);
        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    private void editarEvento(Evento evento) {
        mostrarFormulario(evento.getCategoria(), evento);
    }

    private void cargarVista(String recurso) {
        // Cargamos inicio-view.fxml, que muestra la vista del contenido del panel central del menú principal.
        try {
            FXMLLoader loader = crearLoader(recurso);
            Parent vista = loader.load();
            if (recurso.endsWith("inicio-view.fxml")) {
                InicioController controller = loader.getController();
                controller.configurarCategoria(categoria -> mostrarFormulario(categoria, null));
            }
            mostrarContenido(vista);
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista: " + e.getMessage());
        }
    }

    private void mostrarContenido(Parent vista) {
        contenedorCentral.getChildren().setAll(vista);
        VBox.setVgrow(vista, javafx.scene.layout.Priority.ALWAYS);
    }

    // Metodo para cargar un fichero fxml
    private FXMLLoader crearLoader(String recurso) {
        return new FXMLLoader(getClass().getResource(recurso));
    }

    // Metodo que sirve como constructor para poder mostrar un mensaje de error
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("AppEventos");
        alerta.setHeaderText("Ha ocurrido un error");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
