module com.library {

    requires javafx.controls;
    requires javafx.fxml;

    opens com.library to javafx.fxml;

    opens com.library.controller to javafx.fxml;

    exports com.library;
    exports com.library.controller;
}