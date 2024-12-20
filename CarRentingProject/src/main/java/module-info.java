//module org.example.carrentingproject {
//    requires javafx.controls;
//    requires javafx.fxml;
//
//
//    opens org.example.carrentingproject to javafx.fxml;
//    exports org.example.carrentingproject;

//    opens org.example.rentingcarsproject to javafx.fxml, org.hibernate.orm.core;
//    exports org.example.rentingcarsproject;
//}
module org.example.carrentingproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires java.naming;
    requires log4j;

    opens org.example.carrentingproject to javafx.fxml;
    exports org.example.carrentingproject;
}