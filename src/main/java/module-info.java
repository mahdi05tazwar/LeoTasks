module com.mtr.leotasks {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires sqlite.jdbc;


    opens com.mtr.leotasks to javafx.fxml;
    exports com.mtr.leotasks;
}