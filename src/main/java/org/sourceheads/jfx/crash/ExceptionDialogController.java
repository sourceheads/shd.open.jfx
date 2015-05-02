package org.sourceheads.jfx.crash;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class ExceptionDialogController {

    private final Throwable throwable;

    public Label laMessage;
    public TextArea taStacktrace;

    public ExceptionDialogController(final Throwable throwable) {
        this.throwable = throwable;
    }

    @FXML
    public void initialize() {
        laMessage.setText(throwable.getMessage());

        final StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));

        taStacktrace.setText(stringWriter.toString());
    }

    public void close(final ActionEvent actionEvent) {
        laMessage.getScene().getWindow().hide();
    }
}
