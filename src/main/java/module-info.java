module com.example.safecasino {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;
    requires java.logging;


    opens vinnsla to javafx.fxml;
    exports vinnsla;
}