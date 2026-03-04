package org.l2o1.myink.view.util;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class PathTextField extends TextField {
    // Liste des caractères interdits : \ / : * ? " < > |
    private static final String FORBIDDEN_CHARS_REGEX = "[\\\\/:*?\"<>|]";

    public PathTextField(String baseString) {
        super(baseString);
        setTextFormatter();
    }

    public PathTextField() {
        this("");
    }

    private void setTextFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getText();
            if (newText.matches(".*" + FORBIDDEN_CHARS_REGEX + ".*")) {
                return null; // ignore le changement si caractères interdits
            }
            return change;
        };
        this.setTextFormatter(new TextFormatter<>(filter));
    }
}
