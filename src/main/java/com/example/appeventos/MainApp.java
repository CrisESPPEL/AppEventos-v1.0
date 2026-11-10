package com.example.appeventos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // Cargamos el FXML
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/com/example/appeventos/view/main-view.fxml"));
        Parent root = loader.load();

        // Creamos la escena incial
        Scene scene = new Scene(root, 1100, 720);

        // Aplicamos el CSS
        scene.getStylesheets().add(
                Objects.requireNonNull(MainApp.class.getResource("/com/example/appeventos/css/estilos.css")).toExternalForm());

        // Características de la escena
        stage.setTitle("AppEventos v1.0");
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
