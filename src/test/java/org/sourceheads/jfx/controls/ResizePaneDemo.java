package org.sourceheads.jfx.controls;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class ResizePaneDemo extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {

        /*final ResizePane resizePaneLeft = new ResizePane(Side.RIGHT, new ListView<>());
        final ResizePane resizePaneRight = new ResizePane(Side.LEFT, new ListView<>());
        final ResizePane resizePaneTop = new ResizePane(new ListView<>());
        final ResizePane resizePaneBottom = new ResizePane(Side.TOP, new ListView<>());

        final AnchorPane anchorPane = new AnchorPane(resizePaneLeft, resizePaneRight, resizePaneTop, resizePaneBottom);
        anchorPane.setPrefWidth(800);
        anchorPane.setPrefHeight(600);

        AnchorPane.setRightAnchor(resizePaneRight, .0);
        AnchorPane.setBottomAnchor(resizePaneBottom, .0);*/

        final Parent parent = FXMLLoader.load(getClass().getResource("resize-pane-demo.fxml"));
        final Scene scene = new Scene(parent);
        scene.getStylesheets().add("/org/sourceheads/jfx/controls/resize-pane.css");
        scene.getStylesheets().add("/org/sourceheads/jfx/css/common.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("ResizePane Demo");
        primaryStage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
