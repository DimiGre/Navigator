module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jgrapht.core;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}