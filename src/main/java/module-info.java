module p5.comdis_p5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens p5.Server to javafx.fxml;
    exports p5.Server;
    exports p5.Client;
    opens p5.Client to javafx.fxml;
    exports p5.Client.controllers;
    opens p5.Client.controllers to javafx.fxml;
}