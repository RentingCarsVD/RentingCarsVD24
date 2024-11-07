module org.example.rentingcars {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires java.naming;


    opens org.example.rentingcars to javafx.fxml;
    exports org.example.rentingcars.tables;
}