package com.example.appeventos.controller;

import javafx.fxml.FXML;

import java.util.function.Consumer;

public class InicioController {

    private Consumer<String> alSeleccionarCategoria = categoria -> { };

    // Metodo que establece una acción al seleccionar una categoría
    public void configurarCategoria(Consumer<String> accion) {
        alSeleccionarCategoria = accion == null ? categoria -> { } : accion;
    }

    // Métodos para abrir formularios según cuál se ha seleccionado
    @FXML private void abrirMusica() {
        alSeleccionarCategoria.accept("Música");
    }

    @FXML private void abrirTecnologia() {
        alSeleccionarCategoria.accept("Tecnología");
    }

    @FXML private void abrirDeportes() {
        alSeleccionarCategoria.accept("Deportes");
    }

    @FXML private void abrirCultura() {
        alSeleccionarCategoria.accept("Cultura");
    }
}
