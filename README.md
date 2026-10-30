# AppEventos v1.0 — JavaFX, FXML y MVC

Proyecto Maven educativo pensado para 2.º DAM. Se ha creado como proyecto independiente y separa modelo, persistencia, controladores y vistas.

## Requisitos

- JDK 17 o superior.
- IntelliJ IDEA con soporte para Maven.
- Scene Builder (opcional, para abrir y editar los archivos FXML visualmente).
- Conexión a Internet la primera vez que Maven descargue JavaFX.

## Ejecutar en IntelliJ IDEA

1. Descomprime `AppEventos-v1.0.zip`.
2. En IntelliJ, abre la carpeta `AppEventos-v1.0` (la que contiene `pom.xml`).
3. Selecciona JDK 17 o superior como SDK del proyecto y espera a que Maven importe las dependencias.
4. Abre la ventana **Maven** y ejecuta `Plugins > javafx > javafx:run`, o ejecuta desde una terminal situada en la carpeta del proyecto:

   ```bash
   mvn clean javafx:run
   ```

Maven descarga JavaFX; no hace falta añadir manualmente un SDK de JavaFX.

## Organización del proyecto

```text
src/main/java/com/example/appeventos/
  MainApp.java
  model/
    Evento.java
  persistence/
    RepositorioEventosCSV.java
  controller/
    MainController.java
    InicioController.java
    FormularioEventoController.java
    ListadoEventosController.java

src/main/resources/com/example/appeventos/
  view/
    main-view.fxml
    inicio-view.fxml
    formulario-evento-view.fxml
    listado-eventos-view.fxml
  css/
    estilos.css
```

- **Modelo (`Evento`)**: representa un evento y expone los datos principales del `TableView`.
- **Vistas (FXML)**: definen la interfaz. Se pueden abrir desde Scene Builder.
- **Controladores**: responden a los botones, validan y coordinan la navegación mediante `FXMLLoader`.
- **Persistencia (`RepositorioEventosCSV`)**: lee y escribe los cuatro CSV dentro de `datos/`.
- **Datos en memoria**: el listado carga los eventos leídos del CSV en una `ObservableList<Evento>` que se usa como origen del `TableView`.

## Formato y compatibilidad de los CSV

Los ficheros se crean en `datos/` usando los nombres `Eventos_Musica.csv`, `Eventos_Tecnologia.csv`, `Eventos_Deportes.csv` y `Eventos_Cultura.csv`. La primera columna contiene el identificador del registro (los CSV de v0.1 ya guardaban una fecha/hora en esa posición), y el resto mantiene el orden de los datos específicos de cada categoría. Al modificar un evento se localiza la fila por ese identificador y se sustituye, en lugar de añadir otra fila.

Las rutas son relativas al directorio de ejecución del proyecto, no a una ruta absoluta del ordenador. No se incluyen ficheros CSV ni la carpeta `target`; se crearán al ejecutar/guardar eventos.

## Vistas en Scene Builder

Abre estos archivos desde Scene Builder:

- `src/main/resources/com/example/appeventos/view/main-view.fxml`
- `src/main/resources/com/example/appeventos/view/inicio-view.fxml`
- `src/main/resources/com/example/appeventos/view/formulario-evento-view.fxml`
- `src/main/resources/com/example/appeventos/view/listado-eventos-view.fxml`

No elimines los atributos `fx:id`, `fx:controller` ni los valores `onAction` sin cambiar también los controladores Java correspondientes.

## Pruebas recomendadas antes de entregar

1. Crear un evento de cada categoría, con todos los campos obligatorios cumplimentados.
2. Cerrar y abrir la aplicación para comprobar que el listado vuelve a leer los CSV.
3. Seleccionar un evento, cambiar nombre/lugar/fecha y guardar; comprobar que se modifica la misma fila y no aparece una segunda.
4. Introducir textos con punto y coma, comillas y saltos de línea en la descripción; comprobar que se guardan y se leen correctamente.
5. Ejecutar `mvn clean javafx:run` desde una copia limpia.
6. Antes de entregar, cerrar IntelliJ y eliminar `target/` si se ha generado. No elimines `pom.xml` ni `src/`.

## Nota para explicar el diseño

La separación es intencional: el controlador de formulario no escribe directamente archivos, sino que llama a `RepositorioEventosCSV`. `MainController` coordina qué vista se muestra. `Evento` guarda el modelo de datos y `ListadoEventosController` alimenta el `TableView` con una `ObservableList`.
