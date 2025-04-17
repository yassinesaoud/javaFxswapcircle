module com.example.crud {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;

    opens com.example.crud to javafx.fxml;
    opens com.example.crud.BackOffice to javafx.fxml, javafx.base, javafx.graphics;
    opens com.example.crud.FrontOffice to javafx.fxml, javafx.base, javafx.graphics;
    opens com.example.crud.model to javafx.base;
    
    exports com.example.crud;
    exports com.example.crud.BackOffice;
    exports com.example.crud.FrontOffice;
    exports com.example.crud.model;
}