package com.mtr.leotasks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class Tasks_TaskAddingController implements Initializable{

    @FXML
    TextField taskEntry;
    @FXML
    DatePicker dateEntry;
    @FXML
    Label warningLabel;
    @FXML
    Button addTaskBtn;
    OneInstanceDataBank db;
    @FXML
    FXMLLoader loader;
    @FXML
    ComboBox hourComboBox, minuteComboBox;
    TasksModel tasksModel;

    public Tasks_TaskAddingController(){
        db  = OneInstanceDataBank.getInstance();
        loader = new FXMLLoader(AppClass.class.getResource("tasks_ui.fxml"));
        tasksModel = new TasksModel();
    }



    @FXML
    protected void addTask(ActionEvent event) throws IOException, SQLException {
        String task = taskEntry.getText();
        LinkedList<String[]> tasks = tasksModel.getTasks();

        //HERE I AM RECHECKING FOR ANY TASK WITH THE SAME NAME IN THE CURRENT LIST (SO THAT PEOPLE CAN'T SET THE SAME TASK CAPTION BY OPENING 2 TASK-ADDING WINDOWS
        Iterator<String[]> it = tasks.iterator();
        while (it.hasNext()){String[] currentTask = it.next(); if (currentTask[0].equals(db.getCurrentListName()) && currentTask[1].equalsIgnoreCase(task)) return;}

        LocalDate date = dateEntry.getValue();
        String min = minuteComboBox.getValue().toString();
        String hour = hourComboBox.getValue().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String dateStr = date.format(formatter);

        String dateTimeStr = dateStr + " " + hour + ":" + min + ":00";

        tasksModel.addTask(db.getCurrentListName(), task, dateTimeStr);
        refreshTasks();
        closeCurrentStage();
    }

    @FXML
    protected void stopIfExists() throws SQLException {
        String task = taskEntry.getText();
        LinkedList<String[]> tasks = tasksModel.getTasks();
        Iterator<String[]> it = tasks.iterator();
        while (it.hasNext()){
            String[] currentTask = it.next();
            if (currentTask[0].equals(db.getCurrentListName()) && currentTask[1].equalsIgnoreCase(task)){
                warningLabel.setVisible(true);
                addTaskBtn.setDisable(true);
                return;
            }
        }
        if (task.isEmpty()) addTaskBtn.setDisable(true);
        else {
            warningLabel.setVisible(false);
            addTaskBtn.setDisable(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskEntry.textProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                this.stopIfExists();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        ObservableList<String> hourOptions = FXCollections.observableArrayList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        ObservableList<String> minuteOptions = FXCollections.observableArrayList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59");
        hourComboBox.getItems().addAll(hourOptions);
        minuteComboBox.getItems().addAll(minuteOptions);
        hourComboBox.setValue("00");
        minuteComboBox.setValue("00");
        dateEntry.setValue(LocalDate.now());
    }

    private void refreshTasks() throws IOException {
        Stage stage = db.getHomeStage();
        Scene scene = new Scene(loader.load(), db.getDims()[0], db.getDims()[1]);
        stage.setScene(scene);
        stage.show();
    }

    private void closeCurrentStage() throws IOException {
        Stage currentStage = (Stage) taskEntry.getScene().getWindow();
        currentStage.close();
    }

}
