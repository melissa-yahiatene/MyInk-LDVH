module org.l2o1.myink {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires org.fxmisc.richtext;

    exports org.l2o1.myink;
    opens org.l2o1.myink to javafx.fxml;

    exports org.l2o1.myink.presenter;
    opens org.l2o1.myink.presenter to javafx.fxml;

    exports org.l2o1.myink.view;
    opens org.l2o1.myink.view to javafx.fxml;
}