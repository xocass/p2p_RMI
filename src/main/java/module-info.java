module p5.comdis_p5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens p5.comdis_p5 to javafx.fxml;
    exports p5.comdis_p5;
}