package com.mtr.leotasks;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.LinkedList;

public class OneInstanceDataBank {

    private static final OneInstanceDataBank instance = new OneInstanceDataBank();
    private String currentListName;
    private String css;
    private Stage homeStage;
    private int width, height;
    private Image icon;

    public static OneInstanceDataBank getInstance(){
        return instance;
    }

    public String getCurrentListName(){
        return currentListName;
    }

    public void setCurrentListName(String name){
        currentListName = name;
    }

    public String getCSS(){
        return css;
    }

    public void setCSS(String parameter){
        css = parameter;
    }

    public Stage getHomeStage(){return homeStage;} // I'LL USE THIS WHEN THE HOME STAGE NEEDS TO BE DIRECTLY REFRESHED (DURING LIST CREATION)
    public void setHomeStage(Stage stage) {homeStage = stage;}
    public int[] getDims() {
        return new int[]{width, height};
    };
    public void setDims(int width, int height) {
        this.width = width;
        this.height = height;
    };
    public Image getIcon(){
        return icon;
    }
    public void setIcon(Image icon){
        this.icon = icon;
    }
}