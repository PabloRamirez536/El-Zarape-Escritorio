module org.utleon.elzarape {
    requires javafx.controls;
    requires javafx.fxml;
    requires unirest.java;
    requires com.google.gson;
    requires org.apache.httpcomponents.httpclient;


    opens org.utleon.elzarape to javafx.fxml;
    exports org.utleon.elzarape;
    opens org.utleon.elzarape.model to com.google.gson;
}