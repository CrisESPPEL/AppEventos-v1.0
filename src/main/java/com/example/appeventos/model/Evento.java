package com.example.appeventos.model;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Modelo de un evento. Los datos específicos se conservan en el orden del formulario
 * para mantener una estructura sencilla y compatible con los CSV de AppEventos v0.1.
 */
public class Evento {

    private final String id;
    private final String categoria;
    private String[] datos;

    /** Crea un evento nuevo. El identificador se almacena como primera columna del CSV. */
    public Evento(String categoria, String[] datos) {
        this(LocalDateTime.now().toString(), categoria, datos);
    }

    /** Constructor utilizado al leer del CSV o al actualizar un evento existente. */
    public Evento(String id, String categoria, String[] datos) {
        this.id = id;
        this.categoria = categoria;
        this.datos = datos == null ? new String[0] : Arrays.copyOf(datos, datos.length);
    }

    public String getId() {
        return id;
    }

    public String getCategoria() {
        return categoria;
    }

    public String[] getDatos() {
        return Arrays.copyOf(datos, datos.length);
    }

    public void setDatos(String[] datos) {
        this.datos = datos == null ? new String[0] : Arrays.copyOf(datos, datos.length);
    }

    public String getNombre() {
        return dato(0);
    }

    public String getLugar() {
        return switch (categoria) {
            case "Tecnología" -> dato(4);
            case "Deportes" -> dato(3);
            default -> dato(2);
        };
    }

    public String getFecha() {
        return switch (categoria) {
            case "Tecnología" -> dato(5);
            case "Deportes" -> dato(4);
            default -> dato(3);
        };
    }

    public String getModalidad() {
        return switch (categoria) {
            case "Música" -> dato(5);
            case "Tecnología" -> dato(7);
            default -> dato(6);
        };
    }

    private String dato(int indice) {
        if (indice < 0 || indice >= datos.length || datos[indice] == null) {
            return "";
        }
        return datos[indice];
    }
}
