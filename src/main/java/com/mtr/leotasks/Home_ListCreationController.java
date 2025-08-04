package com.mtr.leotasks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class Home_ListCreationController implements Initializable {

    @FXML
    TextField listNameEntry;
    @FXML
    Label warningLabel;
    @FXML
    Button createListBtn;
    OneInstanceDataBank db;
    FXMLLoader homeUILoader;
    TasksModel tasksModel;

    public Home_ListCreationController(){
        db  = OneInstanceDataBank.getInstance();
        homeUILoader = new FXMLLoader(AppClass.class.getResource("home_ui.fxml"));
        tasksModel = new TasksModel();
    }



    @FXML
    protected void createList(ActionEvent event) throws IOException, SQLException {
        String listName = listNameEntry.getText();
        if (tasksModel.getListNamesLowercase().contains(listName.toLowerCase())) return; // REMINDER TO MYSELF:
        // I ADDED THIS EVEN THOUGH IT MAY SEEM USELESS.
        // IF THE USER OPENS 2 WINDOWS OF THE LIST CREATION WINDOW AND ADDS THE SAME LIST NAME TO EACH
        // THEN HE/SHE CAN BYPASS THE UNIQUE-LIST-NAME RESTRICTION; BUT THIS WILL PREVENT THAT.
        //ANYWAY...

        tasksModel.createList(listName);

        refreshHomeUI();
        closeCurrentStage();
    }

    @FXML
    protected void stopIfExists() throws SQLException {
        String listName = listNameEntry.getText().toLowerCase();
        if (tasksModel.getListNamesLowercase().contains(listName)){
            warningLabel.setVisible(true);
            createListBtn.setDisable(true);
        }
        else if (listName.isEmpty()) createListBtn.setDisable(true);
        else {
            warningLabel.setVisible(false);
            createListBtn.setDisable(false);
        }
    }

    private void refreshHomeUI() throws IOException {
        Stage stage = db.getHomeStage();
        Scene scene = new Scene(homeUILoader.load(), db.getDims()[0], db.getDims()[1]);
        stage.setScene(scene);
        stage.show();
    }

    private void closeCurrentStage() {
        Stage currentStage = (Stage) listNameEntry.getScene().getWindow();
        currentStage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listNameEntry.textProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                this.stopIfExists();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
