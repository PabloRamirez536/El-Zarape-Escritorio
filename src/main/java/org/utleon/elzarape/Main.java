package org.utleon.elzarape;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("Login - El Zarape");
        stage.setScene(scene);
        stage.show();
    }

    //    public void start(Stage primaryStage) throws Exception {
//        Parent parent = FXMLLoader.load(Main.class.getResource("inicio.fxml"));
//        primaryStage.setTitle("El Zarape - Sistema de Gestión");
//        primaryStage.setMaximized(true);
//        primaryStage.setScene(new Scene(parent));
//        primaryStage.show();
//    }

}
