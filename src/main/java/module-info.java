module charlesmercado0522.weatherapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens charlesmercado0522.weatherapp to javafx.fxml;
    exports charlesmercado0522.weatherapp;
}