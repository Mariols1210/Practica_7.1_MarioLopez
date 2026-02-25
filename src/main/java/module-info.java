//module org.example.mario_pr51 {
//    requires javafx.controls;
//    requires javafx.fxml;
//
//
//    opens org.example.mario_pr51 to javafx.fxml;
//    exports org.example.mario_pr51;
//}

module org.example.mario_pr51 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    requires net.sf.jasperreports.core;
    requires org.apache.commons.logging;
    requires com.fasterxml.jackson.dataformat.xml;


    opens org.example.mario_pr51 to javafx.fxml;
    opens org.example.mario_pr51.BD to javafx.fxml;
    exports org.example.mario_pr51;
    exports org.example.mario_pr51.BD;
}