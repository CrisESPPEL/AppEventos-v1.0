package com.example.appeventos.persistence;

import com.example.appeventos.model.Evento;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/** Centraliza la lectura, el alta y la actualización de los ficheros CSV. */
public class RepositorioEventosCSV {

    private static final String[] CATEGORIAS = {"Música", "Tecnología", "Deportes", "Cultura"};
    private final Path carpetaDatos = Path.of("datos");

    public List<Evento> listarTodos() throws IOException {
        List<Evento> eventos = new ArrayList<>();
        for (String categoria : CATEGORIAS) {
            Path archivo = obtenerArchivo(categoria);
            if (!Files.exists(archivo)) {
                continue;
            }
            for (List<String> registro : leerRegistros(archivo)) {
                if (registro.size() >= 2) {
                    String id = registro.get(0);
                    String[] datos = registro.subList(1, registro.size()).toArray(new String[0]);
                    eventos.add(new Evento(id, categoria, datos));
                }
            }
        }
        return eventos;
    }

    public void guardar(Evento evento) throws IOException {
        Path archivo = obtenerArchivo(evento.getCategoria());
        Files.createDirectories(carpetaDatos);
        String registro = serializar(crearCampos(evento)) + System.lineSeparator();
        Files.writeString(archivo, registro, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /**
     * Sustituye la fila cuyo identificador coincide. No añade una fila nueva,
     * por lo que la edición no crea un registro duplicado.
     */
    public void actualizar(Evento eventoActualizado) throws IOException {
        Path archivo = obtenerArchivo(eventoActualizado.getCategoria());
        if (!Files.exists(archivo)) {
            throw new IOException("No existe el fichero CSV del evento que se quiere modificar.");
        }

        List<List<String>> registros = leerRegistros(archivo);
        boolean encontrado = false;
        for (int i = 0; i < registros.size(); i++) {
            List<String> registro = registros.get(i);
            if (!registro.isEmpty() && registro.get(0).equals(eventoActualizado.getId())) {
                registros.set(i, crearCampos(eventoActualizado));
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            throw new IOException("No se ha encontrado el evento original en el fichero CSV.");
        }

        StringBuilder contenido = new StringBuilder();
        for (List<String> registro : registros) {
            contenido.append(serializar(registro)).append(System.lineSeparator());
        }
        Files.writeString(archivo, contenido.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    private List<String> crearCampos(Evento evento) {
        List<String> campos = new ArrayList<>();
        campos.add(evento.getId());
        campos.addAll(List.of(evento.getDatos()));
        return campos;
    }

    private Path obtenerArchivo(String categoria) throws IOException {
        String nombre;
        switch (categoria) {
            case "Música" -> nombre = "Eventos_Musica.csv";
            case "Tecnología" -> nombre = "Eventos_Tecnologia.csv";
            case "Deportes" -> nombre = "Eventos_Deportes.csv";
            case "Cultura" -> nombre = "Eventos_Cultura.csv";
            default -> throw new IOException("Categoría no válida: " + categoria);
        }
        return carpetaDatos.resolve(nombre);
    }

    private String serializar(List<String> campos) {
        List<String> escapados = new ArrayList<>();
        for (String campo : campos) {
            escapados.add(escapar(campo));
        }
        return String.join(";", escapados);
    }

    private String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        if (texto.contains(";") || texto.contains("\"")
                || texto.contains("\n") || texto.contains("\r")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }

    /** Lee registros completos, incluidos los campos que contengan saltos de línea entre comillas. */
    private List<List<String>> leerRegistros(Path archivo) throws IOException {
        String contenido = Files.readString(archivo, StandardCharsets.UTF_8);
        List<List<String>> registros = new ArrayList<>();
        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;

        for (int i = 0; i < contenido.length(); i++) {
            char caracter = contenido.charAt(i);

            if (caracter == '"') {
                if (entreComillas && i + 1 < contenido.length()
                        && contenido.charAt(i + 1) == '"') {
                    campo.append('"');
                    i++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if (caracter == ';' && !entreComillas) {
                campos.add(campo.toString());
                campo.setLength(0);
            } else if ((caracter == '\n' || caracter == '\r') && !entreComillas) {
                if (caracter == '\r' && i + 1 < contenido.length()
                        && contenido.charAt(i + 1) == '\n') {
                    i++;
                }
                campos.add(campo.toString());
                campo.setLength(0);
                if (!(campos.size() == 1 && campos.get(0).isEmpty())) {
                    registros.add(new ArrayList<>(campos));
                }
                campos.clear();
            } else {
                campo.append(caracter);
            }
        }

        if (campo.length() > 0 || !campos.isEmpty()) {
            campos.add(campo.toString());
            if (!(campos.size() == 1 && campos.get(0).isEmpty())) {
                registros.add(new ArrayList<>(campos));
            }
        }
        return registros;
    }
}
