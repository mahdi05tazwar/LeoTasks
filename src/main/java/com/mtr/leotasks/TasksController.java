package com.mtr.leotasks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TasksController implements Initializable {

    @FXML
    Label listNameLabel;
    OneInstanceDataBank db;
    @FXML
    FXMLLoader loader;
    @FXML
    Scene scene;
    @FXML
    VBox tasksVBox;
    LinkedList<String[]> tasks;
    @FXML
    HBox sampleHBox;
    @FXML
    Stage taskAddingStage;
    TasksModel tasksModel;

    public TasksController(){
        tasksModel = new TasksModel();
        db = OneInstanceDataBank.getInstance();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tasksVBox.getChildren().remove(sampleHBox);

        listNameLabel.setText(db.getCurrentListName());

        try {
            tasks = tasksModel.getTasks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            tasks = sortTasksByDateTime(tasks);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        int i = 0;
        for (String[] task: tasks){
            if (task[0].equals(db.getCurrentListName())){
                i ++;
                String timeLeftStr = "[TASK IS DUE]";

                try {
                    if (!isDue(task[2])){
                        long[] timeLeft = getTimeLeft(task[2]);
                        timeLeftStr = "(" + timeLeft[0] + " days " + timeLeft[1] + " hours " + timeLeft[2] + " minutes left" + ")";
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                HBox hBox = new HBox();
                hBox.setId(task[1]);
                hBox.setAlignment(Pos.CENTER_LEFT);
                hBox.setPrefHeight(65);
                VBox vBox0 = new VBox();
                vBox0.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(vBox0, Priority.ALWAYS);
                VBox vBox1 = new VBox();
                vBox1.setAlignment(Pos.CENTER);
                Label label = new Label(i + ". " + task[1] + " : " + task[2] + " " + timeLeftStr);
                VBox.setVgrow(label, Priority.NEVER);
                label.getStylesheets().add(db.getCSS());
                label.getStyleClass().add("tasklabel");
                Button button = new Button("Remove");
                button.setOnAction(event -> {try {removeTask(event, task[0], task[1]);} catch (IOException | SQLException e) {throw new RuntimeException(e);}});
                button.getStylesheets().add(db.getCSS());

                tasksVBox.getChildren().add(hBox);
                hBox.getChildren().addAll(vBox0, vBox1);
                vBox0.getChildren().add(label);
                vBox1.getChildren().add(button);
            }
        }
    }


    @FXML
    protected void removeTask(ActionEvent event, String listName, String taskToRemove) throws IOException, SQLException {
        tasksModel.removeTask(listName, taskToRemove);

        Button btn = (Button) event.getSource();
        Scene currentScene = btn.getScene();
        db.setDims((int)currentScene.getWidth(), (int)currentScene.getHeight());

        Stage stage = db.getHomeStage();
        loader = new FXMLLoader(AppClass.class.getResource("tasks_ui.fxml"));
        scene = new Scene(loader.load(), db.getDims()[0], db.getDims()[1]);
        stage.setScene(scene);
    }

    @FXML
    protected void goBack(ActionEvent event) throws IOException {
        Button btn = (Button) event.getSource();
        Scene currentScene = btn.getScene();
        db.setDims((int)currentScene.getWidth(), (int)currentScene.getHeight());

        db.setCurrentListName("");
        Stage stage = (Stage) currentScene.getWindow();
        loader = new FXMLLoader(AppClass.class.getResource("home_ui.fxml"));
        scene = new Scene(loader.load(), db.getDims()[0], db.getDims()[1]);
        stage.setScene(scene);
    }

    @FXML
    protected void deleteList(ActionEvent event) throws IOException, SQLException {
        tasksModel.deleteList(db.getCurrentListName());

        goBack(event);
    }


    private long getTimeDiffInSeconds(String dueTimeString) throws ParseException {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date dueTime = dateTimeFormat.parse(dueTimeString);
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        long timeDiffInMilliseconds = dueTime.getTime() - currentTime.getTime();
        return (timeDiffInMilliseconds / 1000);
    }

    public long[] getTimeLeft(String dueTimeString) throws ParseException {
        long timeDiffInSeconds = getTimeDiffInSeconds(dueTimeString);
        long daysLeft = timeDiffInSeconds / (60 * 60 * 24);
        long hoursLeft = (timeDiffInSeconds % (60 * 60 * 24)) / (60 * 60);
        long minutesLeft = (timeDiffInSeconds % (60 * 60)) / (60);
        return new long[]{daysLeft, hoursLeft, minutesLeft};
    }

    public boolean isDue(String dueTimeString) throws ParseException {
        long timeArr = getTimeDiffInSeconds(dueTimeString);
        if (timeArr <= 0) return true;
        return false;
    }

    private LinkedList<String[]> sortTasksByDateTime(LinkedList<String[]> tasks) throws ParseException{
        int number_of_times_repositioned = 0;
        for (int i = 1; i < tasks.size(); i ++) { // THE LOOP ITERATES OVER THE LIST OF ARRAYS AND CHECKS IF A TIME DIFF OF A TASK ARRAY IS SMALLER THAN THE PREVIOUS TIME DIFF AND IF SO, THEN IT SENDS THE ITEM 1 INDEX BACK. RECURSION ENSURES THAT THIS LOOP ITSELF OCCURS MULTIPLE TIMES UNTIL NO REPOSITIONING IS NEEDED; TIME DIFF IS THE TIME REMAINING TILL THE DUE DATE OF THE TASK.
            String[] currentTask = tasks.get(i);
            long currentTimeDiff = getTimeDiffInSeconds(currentTask[2]);
            String[] previousTask = tasks.get(i-1);
            long previousTimeDiff = getTimeDiffInSeconds(previousTask[2]);
            if (currentTimeDiff < previousTimeDiff) {
                tasks.remove(i);
                tasks.add(i-1, currentTask);
                number_of_times_repositioned ++;
            }
        }
        if (number_of_times_repositioned != 0) return sortTasksByDateTime(tasks);
        return tasks;
    }

    @FXML
    protected void openTaskAddingWindow(ActionEvent event) throws IOException {
        Button btn = (Button) event.getSource();
        Scene currentScene = btn.getScene();
        db.setDims((int)currentScene.getWidth(), (int)currentScene.getHeight());

        taskAddingStage = new Stage();
        loader = new FXMLLoader(AppClass.class.getResource("task_adding_ui.fxml"));
        scene = new Scene(loader.load(), 600, 250);
        taskAddingStage.setTitle("Leo-Create Task");
        taskAddingStage.getIcons().add(db.getIcon());
        taskAddingStage.setScene(scene);
        taskAddingStage.show();
    }
}
