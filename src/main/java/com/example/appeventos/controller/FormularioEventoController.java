package com.example.appeventos.controller;

import com.example.appeventos.model.Evento;
import com.example.appeventos.persistence.RepositorioEventosCSV;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FormularioEventoController {

    @FXML private Label lblTitulo;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private TextField txtNombre;
    @FXML private HBox filaDeporte;
    @FXML private TextField txtDeporte;
    @FXML private ComboBox<String> cbTipo;
    @FXML private HBox filaTematica;
    @FXML private TextField txtTematica;
    @FXML private HBox filaNivel;
    @FXML private ComboBox<String> cbNivel;
    @FXML private TextField txtLugar;
    @FXML private DatePicker dpFecha;
    @FXML private Spinner<Integer> spAforo;
    @FXML private ComboBox<String> cbPublico;
    @FXML private RadioButton rbPresencial;
    @FXML private RadioButton rbOnline;
    @FXML private RadioButton rbHibrido;
    @FXML private HBox filaInscripcion;
    @FXML private CheckBox chkInscripcion;
    @FXML private HBox filaMaterial;
    @FXML private CheckBox chkMaterial;
    @FXML private HBox filaDestacado;
    @FXML private CheckBox chkDestacado;
    @FXML private HBox filaGratuito;
    @FXML private CheckBox chkGratuito;
    @FXML private Slider slInteres;
    @FXML private Slider slDifusion;
    @FXML private Slider slOrganizacion;
    @FXML private Label lblInteres;
    @FXML private Label lblDifusion;
    @FXML private Label lblOrganizacion;
    @FXML private TextArea txtDescripcion;

    private final ToggleGroup grupoModalidad = new ToggleGroup();
    private final RepositorioEventosCSV repositorio = new RepositorioEventosCSV();
    private Evento eventoOriginal;
    private Consumer<Boolean> alGuardar = correcto -> { };
    private boolean actualizandoCategoria;

    @FXML
    private void initialize() {
        cbCategoria.getItems().setAll("Música", "Tecnología", "Deportes", "Cultura");
        cbPublico.setEditable(true);
        cbPublico.setPromptText("Escribe el público objetivo");
        spAforo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1_000_000, 1));

        rbPresencial.setToggleGroup(grupoModalidad);
        rbOnline.setToggleGroup(grupoModalidad);
        rbHibrido.setToggleGroup(grupoModalidad);
        rbPresencial.setSelected(true);

        prepararSlider(slInteres, lblInteres);
        prepararSlider(slDifusion, lblDifusion);
        prepararSlider(slOrganizacion, lblOrganizacion);

        cbCategoria.setOnAction(evento -> {
            if (!actualizandoCategoria && cbCategoria.getValue() != null) {
                actualizarCamposCategoria(cbCategoria.getValue());
            }
        });
    }

    /** Se llama desde MainController después de cargar este FXML con FXMLLoader. */
    public void configurar(String categoria, Evento evento, Consumer<Boolean> alGuardar) {
        this.eventoOriginal = evento;
        this.alGuardar = alGuardar == null ? correcto -> { } : alGuardar;

        actualizandoCategoria = true;
        cbCategoria.setValue(categoria == null ? "Música" : categoria);
        actualizandoCategoria = false;
        actualizarCamposCategoria(cbCategoria.getValue());

        if (evento == null) {
            lblTitulo.setText("Nuevo evento");
            cbCategoria.setDisable(false);
            limpiarFormulario();
        } else {
            lblTitulo.setText("Modificar evento");
            cbCategoria.setDisable(true);
            cargarEvento(evento);
        }
    }

    private void prepararSlider(Slider slider, Label etiquetaValor) {
        slider.setMin(1);
        slider.setMax(10);
        slider.setValue(5);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setSnapToTicks(true);
        slider.setBlockIncrement(1);
        slider.valueProperty().addListener((observable, anterior, nuevo) ->
                etiquetaValor.setText(String.valueOf(nuevo.intValue())));
        etiquetaValor.setText("5");
    }

    private void actualizarCamposCategoria(String categoria) {
        boolean musica = "Música".equals(categoria);
        boolean tecnologia = "Tecnología".equals(categoria);
        boolean deportes = "Deportes".equals(categoria);
        boolean cultura = "Cultura".equals(categoria);

        mostrarFila(filaDeporte, deportes);
        mostrarFila(filaTematica, tecnologia);
        mostrarFila(filaNivel, tecnologia);
        mostrarFila(filaInscripcion, tecnologia);
        mostrarFila(filaMaterial, deportes);
        mostrarFila(filaDestacado, !tecnologia);
        mostrarFila(filaGratuito, cultura);

        List<String> tipos = switch (categoria) {
            case "Música" -> List.of("Concierto", "Festival", "Recital");
            case "Tecnología" -> List.of("Charla", "Taller", "Feria", "Jornada");
            case "Deportes" -> List.of("Competición", "Torneo", "Exhibición", "Actividad deportiva");
            case "Cultura" -> List.of("Exposición", "Obra de teatro", "Cine", "Feria", "Encuentro cultural");
            default -> List.of();
        };
        cbTipo.getItems().setAll(tipos);
        cbTipo.setValue(null);

        cbNivel.getItems().setAll("Iniciación", "Intermedio", "Avanzado");
        cbNivel.setValue(null);

        if (musica || tecnologia || deportes || cultura) {
            // Todas las categorías mantienen los mismos controles comunes.
            cbPublico.setPromptText("Escribe el público objetivo");
        }
    }

    private void mostrarFila(HBox fila, boolean mostrar) {
        fila.setVisible(mostrar);
        fila.setManaged(mostrar);
    }

    @FXML
    private void guardarEvento() {
        String categoria = cbCategoria.getValue();
        if (categoria == null || categoria.isBlank()) {
            mostrarError("Selecciona una categoría.");
            return;
        }
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError("Introduce el nombre del evento.");
            return;
        }
        if (cbTipo.getValue() == null) {
            mostrarError("Selecciona el tipo de evento o actividad.");
            return;
        }
        if ("Tecnología".equals(categoria)) {
            if (txtTematica.getText().trim().isEmpty()) {
                mostrarError("Introduce la tecnología o temática principal.");
                return;
            }
            if (cbNivel.getValue() == null) {
                mostrarError("Selecciona el nivel.");
                return;
            }
        }
        if ("Deportes".equals(categoria) && txtDeporte.getText().trim().isEmpty()) {
            mostrarError("Introduce el deporte o disciplina.");
            return;
        }
        if (txtLugar.getText().trim().isEmpty()) {
            mostrarError("Introduce el lugar del evento.");
            return;
        }
        if (dpFecha.getValue() == null) {
            mostrarError("Selecciona la fecha del evento.");
            return;
        }
        String publico = cbPublico.getEditor().getText().trim();
        if (publico.isEmpty()) {
            mostrarError("Introduce el público objetivo.");
            return;
        }

        String modalidad = obtenerModalidad();
        if (modalidad.isEmpty()) {
            mostrarError("Selecciona una modalidad.");
            return;
        }

        String[] datos = crearDatos(categoria, publico, modalidad);
        try {
            if (eventoOriginal == null) {
                repositorio.guardar(new Evento(categoria, datos));
            } else {
                Evento actualizado = new Evento(eventoOriginal.getId(), categoria, datos);
                repositorio.actualizar(actualizado);
            }
            mostrarConfirmacion(eventoOriginal == null
                    ? "Evento guardado correctamente."
                    : "Evento modificado correctamente.");
            alGuardar.accept(true);
        } catch (IOException e) {
            mostrarError("No se pudo guardar el evento: " + e.getMessage());
        }
    }

    private String[] crearDatos(String categoria, String publico, String modalidad) {
        List<String> datos = new ArrayList<>();
        datos.add(txtNombre.getText().trim());

        switch (categoria) {
            case "Música" -> {
                datos.add(cbTipo.getValue());
                datos.add(txtLugar.getText().trim());
                datos.add(dpFecha.getValue().toString());
                datos.add(String.valueOf(spAforo.getValue()));
                // Se conserva el orden del CSV de v0.1: modalidad, público, destacado.
                datos.add(modalidad);
                datos.add(publico);
                datos.add(chkDestacado.isSelected() ? "Sí" : "No");
            }
            case "Tecnología" -> {
                datos.add(cbTipo.getValue());
                datos.add(txtTematica.getText().trim());
                datos.add(cbNivel.getValue());
                datos.add(txtLugar.getText().trim());
                datos.add(dpFecha.getValue().toString());
                datos.add(String.valueOf(spAforo.getValue()));
                datos.add(modalidad);
                datos.add(chkInscripcion.isSelected() ? "Sí" : "No");
                datos.add(publico);
            }
            case "Deportes" -> {
                datos.add(txtDeporte.getText().trim());
                datos.add(cbTipo.getValue());
                datos.add(txtLugar.getText().trim());
                datos.add(dpFecha.getValue().toString());
                datos.add(String.valueOf(spAforo.getValue()));
                datos.add(modalidad);
                datos.add(chkMaterial.isSelected() ? "Sí" : "No");
                datos.add(chkDestacado.isSelected() ? "Sí" : "No");
                datos.add(publico);
            }
            case "Cultura" -> {
                datos.add(cbTipo.getValue());
                datos.add(txtLugar.getText().trim());
                datos.add(dpFecha.getValue().toString());
                datos.add(String.valueOf(spAforo.getValue()));
                datos.add(publico);
                datos.add(modalidad);
                datos.add(chkGratuito.isSelected() ? "Sí" : "No");
                datos.add(chkDestacado.isSelected() ? "Sí" : "No");
            }
            default -> throw new IllegalArgumentException("Categoría desconocida: " + categoria);
        }

        datos.add(String.valueOf((int) slInteres.getValue()));
        datos.add(String.valueOf((int) slDifusion.getValue()));
        datos.add(String.valueOf((int) slOrganizacion.getValue()));
        datos.add(txtDescripcion.getText().trim());
        return datos.toArray(new String[0]);
    }

    private String obtenerModalidad() {
        if (rbPresencial.isSelected()) return "Presencial";
        if (rbOnline.isSelected()) return "Online";
        if (rbHibrido.isSelected()) return "Híbrido";
        return "";
    }

    private void cargarEvento(Evento evento) {
        String[] datos = evento.getDatos();
        String categoria = evento.getCategoria();
        txtNombre.setText(dato(datos, 0));

        switch (categoria) {
            case "Música" -> {
                cbTipo.setValue(dato(datos, 1));
                txtLugar.setText(dato(datos, 2));
                dpFecha.setValue(fecha(dato(datos, 3)));
                establecerAforo(dato(datos, 4));
                seleccionarModalidad(dato(datos, 5));
                cbPublico.getEditor().setText(dato(datos, 6));
                chkDestacado.setSelected("Sí".equalsIgnoreCase(dato(datos, 7)));
                establecerSliders(datos, 8, 9, 10);
                txtDescripcion.setText(dato(datos, 11));
            }
            case "Tecnología" -> {
                cbTipo.setValue(dato(datos, 1));
                txtTematica.setText(dato(datos, 2));
                cbNivel.setValue(dato(datos, 3));
                txtLugar.setText(dato(datos, 4));
                dpFecha.setValue(fecha(dato(datos, 5)));
                establecerAforo(dato(datos, 6));
                seleccionarModalidad(dato(datos, 7));
                chkInscripcion.setSelected("Sí".equalsIgnoreCase(dato(datos, 8)));
                cbPublico.getEditor().setText(dato(datos, 9));
                establecerSliders(datos, 10, 11, 12);
                txtDescripcion.setText(dato(datos, 13));
            }
            case "Deportes" -> {
                txtDeporte.setText(dato(datos, 1));
                cbTipo.setValue(dato(datos, 2));
                txtLugar.setText(dato(datos, 3));
                dpFecha.setValue(fecha(dato(datos, 4)));
                establecerAforo(dato(datos, 5));
                seleccionarModalidad(dato(datos, 6));
                chkMaterial.setSelected("Sí".equalsIgnoreCase(dato(datos, 7)));
                chkDestacado.setSelected("Sí".equalsIgnoreCase(dato(datos, 8)));
                cbPublico.getEditor().setText(dato(datos, 9));
                establecerSliders(datos, 10, 11, 12);
                txtDescripcion.setText(dato(datos, 13));
            }
            case "Cultura" -> {
                cbTipo.setValue(dato(datos, 1));
                txtLugar.setText(dato(datos, 2));
                dpFecha.setValue(fecha(dato(datos, 3)));
                establecerAforo(dato(datos, 4));
                cbPublico.getEditor().setText(dato(datos, 5));
                seleccionarModalidad(dato(datos, 6));
                chkGratuito.setSelected("Sí".equalsIgnoreCase(dato(datos, 7)));
                chkDestacado.setSelected("Sí".equalsIgnoreCase(dato(datos, 8)));
                establecerSliders(datos, 9, 10, 11);
                txtDescripcion.setText(dato(datos, 12));
            }
            default -> mostrarError("La categoría del evento no es válida.");
        }
    }

    private String dato(String[] datos, int indice) {
        return indice >= 0 && indice < datos.length && datos[indice] != null ? datos[indice] : "";
    }

    private LocalDate fecha(String texto) {
        if (texto == null || texto.isBlank()) return null;
        try {
            return LocalDate.parse(texto);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private void establecerAforo(String texto) {
        try {
            int valor = Integer.parseInt(texto);
            spAforo.getValueFactory().setValue(Math.max(1, Math.min(1_000_000, valor)));
        } catch (NumberFormatException e) {
            spAforo.getValueFactory().setValue(1);
        }
    }

    private void seleccionarModalidad(String modalidad) {
        switch (modalidad) {
            case "Online" -> rbOnline.setSelected(true);
            case "Híbrido" -> rbHibrido.setSelected(true);
            default -> rbPresencial.setSelected(true);
        }
    }

    private void establecerSliders(String[] datos, int interes, int difusion, int organizacion) {
        slInteres.setValue(enteroEntre(dato(datos, interes), 5));
        slDifusion.setValue(enteroEntre(dato(datos, difusion), 5));
        slOrganizacion.setValue(enteroEntre(dato(datos, organizacion), 5));
    }

    private int enteroEntre(String texto, int porDefecto) {
        try {
            return Math.max(1, Math.min(10, Integer.parseInt(texto)));
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtNombre.clear();
        txtDeporte.clear();
        txtTematica.clear();
        cbTipo.setValue(null);
        cbNivel.setValue(null);
        txtLugar.clear();
        dpFecha.setValue(null);
        spAforo.getValueFactory().setValue(1);
        cbPublico.setValue(null);
        cbPublico.getEditor().clear();
        rbPresencial.setSelected(true);
        chkInscripcion.setSelected(false);
        chkMaterial.setSelected(false);
        chkDestacado.setSelected(false);
        chkGratuito.setSelected(false);
        slInteres.setValue(5);
        slDifusion.setValue(5);
        slOrganizacion.setValue(5);
        txtDescripcion.clear();
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("AppEventos");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarConfirmacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("AppEventos");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
