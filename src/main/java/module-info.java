module org.example.simplegame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens org.example.simplegame to javafx.fxml;
    exports org.example.simplegame;
}