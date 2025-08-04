package com.mtr.leotasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class TasksModel {
    Connection connection;

    public TasksModel(){
        connection = MySQLiteConnection.Connector();
        if (connection == null) System.exit(1);
    }

    public boolean isDBConnected(){
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public LinkedList<String> getListNames() throws SQLException {
        LinkedList<String> listNames = new LinkedList<>();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String query = "SELECT * FROM LISTNAMES";
        preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            String name = resultSet.getString("name");
            listNames.add(name);
        }

        preparedStatement.close();
        resultSet.close();

        return listNames;
    }
    public LinkedList<String[]> getTasks() throws SQLException {
        LinkedList<String[]> tasks = new LinkedList<>();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String query = "SELECT * FROM TASKS";
        preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()){
            String listName = resultSet.getString("listName");
            String task = resultSet.getString("task");
            String dateTime = resultSet.getString("dateTime");
            tasks.add(new String[]{listName, task, dateTime});
        }

        preparedStatement.close();
        resultSet.close();

        return tasks;
    }


    public void createList(String listName) throws SQLException {
        PreparedStatement preparedStatement;
        String sql = "INSERT INTO LISTNAMES (\"name\") VALUES (?)";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, listName);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public void addTask(String listName, String task, String dateTime) throws SQLException {
        PreparedStatement preparedStatement;
        String sql = "INSERT INTO TASKS (\"listName\", \"task\", \"dateTime\") VALUES (?, ?, ?)";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, listName);
        preparedStatement.setString(2, task);
        preparedStatement.setString(3, dateTime);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public void deleteList(String listName) throws SQLException {
        PreparedStatement preparedStatement1, preparedStatement2;
        String sql1 = "DELETE FROM LISTNAMES where name=?";
        preparedStatement1 = connection.prepareStatement(sql1);
        preparedStatement1.setString(1, listName);
        preparedStatement1.execute();
        preparedStatement1.close();
        String sql2 = "DELETE FROM TASKS where listName=?";
        preparedStatement2 = connection.prepareStatement(sql2);
        preparedStatement2.setString(1, listName);
        preparedStatement2.execute();
        preparedStatement2.close();
    }

    public void removeTask(String listName, String task) throws SQLException {
        PreparedStatement preparedStatement;
        String sql = "DELETE FROM TASKS where listName=? and task=?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, listName);
        preparedStatement.setString(2, task);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public void deleteAllLists() throws SQLException {
        PreparedStatement preparedStatement1, preparedStatement2;
        String sql1 = "DELETE FROM LISTNAMES";
        preparedStatement1 = connection.prepareStatement(sql1);
        preparedStatement1.execute();
        preparedStatement1.close();
        String sql2 = "DELETE FROM TASKS";
        preparedStatement2 = connection.prepareStatement(sql2);
        preparedStatement2.execute();
        preparedStatement2.close();
    }


    public LinkedList<String> getListNamesLowercase() throws SQLException {
        LinkedList<String> listNamesLowercase = new LinkedList<String>();
        for (String listName: getListNames()){
            listNamesLowercase.add(listName.toLowerCase());
        }
        return listNamesLowercase;
    }
}
