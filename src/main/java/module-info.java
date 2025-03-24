module com.example.safecasino {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens vinnsla to javafx.fxml;
    exports vinnsla;
}