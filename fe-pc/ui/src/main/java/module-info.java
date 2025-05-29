module com.example.ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires java.desktop;
    requires annotations;
    requires okhttp3;
    requires com.google.gson;
    requires org.json;
    requires java.net.http;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpmime;
    requires org.apache.httpcomponents.httpcore;

    opens com.example.ui to javafx.fxml;
    opens com.example.ui.controller to javafx.fxml;

    exports com.example.ui;
}