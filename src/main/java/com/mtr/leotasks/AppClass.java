package com.mtr.leotasks;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

public class AppClass extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        OneInstanceDataBank db = OneInstanceDataBank.getInstance();
        db.setCSS(AppClass.class.getResource("styles.css").toExternalForm());


        FXMLLoader fxmlLoader = new FXMLLoader(AppClass.class.getResource("home_ui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        db.setDims((int)scene.getWidth(), (int)scene.getHeight());
        stage.setTitle("Leo");
        stage.setScene(scene);
        stage.show();
        db.setHomeStage(stage);
        Image icon = new Image(Objects.requireNonNull(AppClass.class.getResourceAsStream("logo.png")));
        stage.getIcons().add(icon);
        db.setIcon(icon);
    }

    public static void main(String[] args) {
        launch();
    }
}