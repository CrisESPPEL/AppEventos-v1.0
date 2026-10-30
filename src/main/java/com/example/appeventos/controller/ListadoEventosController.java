package com.example.appeventos.controller;

import com.example.appeventos.model.Evento;
import com.example.appeventos.persistence.RepositorioEventosCSV;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.function.Consumer;

public class ListadoEventosController {

    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, String> columnaNombre;
    @FXML private TableColumn<Evento, String> columnaCategoria;
    @FXML private TableColumn<Evento, String> columnaFecha;
    @FXML private TableColumn<Evento, String> columnaLugar;
    @FXML private TableColumn<Evento, String> columnaModalidad;

    private final ObservableList<Evento> eventos = FXCollections.observableArrayList();
    private final RepositorioEventosCSV repositorio = new RepositorioEventosCSV();
    private Consumer<Evento> accionEditar = evento -> { };

    @FXML
    private void initialize() {
        columnaNombre.setCellValueFactory(celda -> new ReadOnlyStringWrapper(celda.getValue().getNombre()));
        columnaCategoria.setCellValueFactory(celda -> new ReadOnlyStringWrapper(celda.getValue().getCategoria()));
        columnaFecha.setCellValueFactory(celda -> new ReadOnlyStringWrapper(celda.getValue().getFecha()));
        columnaLugar.setCellValueFactory(celda -> new ReadOnlyStringWrapper(celda.getValue().getLugar()));
        columnaModalidad.setCellValueFactory(celda -> new ReadOnlyStringWrapper(celda.getValue().getModalidad()));
        tablaEventos.setItems(eventos);
        cargarEventos();
    }

    public void configurar(Consumer<Evento> accionEditar) {
        this.accionEditar = accionEditar == null ? evento -> { } : accionEditar;
    }

    @FXML
    private void editarSeleccionado() {
        Evento seleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAviso("Selecciona primero un evento de la tabla.");
            return;
        }
        accionEditar.accept(seleccionado);
    }

    @FXML
    private void recargarListado() {
        cargarEventos();
    }

    private void cargarEventos() {
        try {
            eventos.setAll(repositorio.listarTodos());
        } catch (IOException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error al cargar eventos");
            alerta.setHeaderText(null);
            alerta.setContentText("No se pudieron leer los archivos CSV: " + e.getMessage());
            alerta.showAndWait();
        }
    }

    private void mostrarAviso(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Listado de eventos");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
