module com.example.safecasino {
    requires javafx.controls;
    requires javafx.fxml;


    opens vinnsla to javafx.fxml;
    exports vinnsla;
}