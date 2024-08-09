module charlesmercado0522.weatherapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;


    opens charlesmercado0522.weatherapp to javafx.fxml;
    exports charlesmercado0522.weatherapp;
    exports charlesmercado0522.weatherapp.types;
}