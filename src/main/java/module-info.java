module com.example.analizador_sintactico {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.analizador_sintactico to javafx.fxml;
    exports com.example.analizador_sintactico;
}