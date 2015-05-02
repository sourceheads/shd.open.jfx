package org.sourceheads.jfx.crash;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Stage primaryStage;

    public ExceptionHandler(final Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    //

    @Override
    public void uncaughtException(final Thread t, final Throwable ex) {
        ex.printStackTrace();

        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("exception-dialog.fxml"));
        fxmlLoader.setControllerFactory(type -> new ExceptionDialogController(ex));
        try {
            final Parent parent = fxmlLoader.load();

            final Stage stage = new Stage();
            final Scene scene = new Scene(parent);
            scene.getStylesheets().add("org/sourceheads/jfx/css/common.css");
            stage.setScene(scene);
            stage.initOwner(primaryStage);
            stage.setTitle("Exception caught");
            stage.showAndWait();
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
