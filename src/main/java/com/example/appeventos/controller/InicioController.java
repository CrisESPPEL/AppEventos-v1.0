package com.example.appeventos.controller;

import javafx.fxml.FXML;

import java.util.function.Consumer;

public class InicioController {

    private Consumer<String> alSeleccionarCategoria = categoria -> { };

    public void configurarCategoria(Consumer<String> accion) {
        alSeleccionarCategoria = accion == null ? categoria -> { } : accion;
    }

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
