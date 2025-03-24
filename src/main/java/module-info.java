module com.example.safecasino {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.safecasino to javafx.fxml;
    exports com.example.safecasino;
}