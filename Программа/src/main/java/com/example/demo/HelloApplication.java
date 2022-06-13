package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static HelloController hc;


    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("MapBuilder");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        HelloController.setOnClose(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}