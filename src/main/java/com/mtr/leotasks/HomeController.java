package com.mtr.leotasks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    OneInstanceDataBank db;
    @FXML
    Stage listCreationStage, infoStage;
    Scene scene;
    @FXML
    VBox listsVBox;
    @FXML
    HBox sampleListHBox;
    @FXML
    FXMLLoader loader;
    TasksModel tasksModel;

    public HomeController() {
        db = OneInstanceDataBank.getInstance();
        tasksModel = new TasksModel();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listsVBox.getChildren().remove(sampleListHBox); // I HAVE A SAMPLE HBOX WHICH IS ONLY FOR DESIGNING PURPOSES
        // BUT I DON'T WANT THAT TO INTERFERE WHEN THE PROGRAM IS BEING RUN

        LinkedList<String> lists;
        try {
            lists = tasksModel.getListNames();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (String list: lists){
            Button button = new Button(list);
            button.setId(list);
            button.setOnAction((event) -> {try {openList(event);} catch (IOException e) {throw new RuntimeException(e);}});
            button.getStylesheets().add(db.getCSS());
            button.getStyleClass().add("listbtns");
            listsVBox.getChildren().add(button);
        }
    }

    @FXML
    protected void openList(ActionEvent event) throws IOException {
        Button btn = (Button) event.getSource();
        Scene currentScene = btn.getScene();
        db.setDims((int)currentScene.getWidth(), (int)currentScene.getHeight());


        String buttonTxt = btn.getText();
        db.setCurrentListName(buttonTxt);
        Stage stage = (Stage) currentScene.getWindow();
        loader = new FXMLLoader(AppClass.class.getResource("tasks_ui.fxml"));
        scene = new Scene(loader.load(), db.getDims()[0], db.getDims()[1]);
        stage.setScene(scene);
    }

    @FXML
    protected void openListCreationWindow(ActionEvent event) throws IOException {
        Button btn = (Button) event.getSource();
        Scene currentScene = btn.getScene();
        db.setDims((int)currentScene.getWidth(), (int)currentScene.getHeight());

        listCreationStage = new Stage();
        loader = new FXMLLoader(AppClass.class.getResource("list_creation_ui.fxml"));
        scene = new Scene(loader.load(), 600, 250);
        listCreationStage.setTitle("Leo-Create List");
        listCreationStage.setScene(scene);
        listCreationStage.getIcons().add(db.getIcon());
        listCreationStage.show();
    }

    @FXML
    protected void deleteAllLists(ActionEvent event) throws IOException, SQLException {
        tasksModel.deleteAllLists();

        Button clickedButton = (Button) event.getSource();
        Stage stage = (Stage) clickedButton.getScene().getWindow();
        loader = new FXMLLoader(AppClass.class.getResource("home_ui.fxml"));
        scene = new Scene(loader.load(), db.getDims()[0], db.getDims()[1]);
        stage.setScene(scene);
    }

    @FXML
    protected void openInfo(ActionEvent event) throws IOException {
        infoStage = new Stage();
        loader = new FXMLLoader(AppClass.class.getResource("information_ui.fxml"));
        scene = new Scene(loader.load(), 600, 250);
        infoStage.setTitle("Leo-Info");
        infoStage.setScene(scene);
        infoStage.getIcons().add(db.getIcon());
        infoStage.show();
    }

}