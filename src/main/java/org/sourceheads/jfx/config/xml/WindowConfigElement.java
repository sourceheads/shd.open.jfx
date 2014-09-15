package org.sourceheads.jfx.config.xml;

import javax.xml.bind.annotation.XmlAttribute;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class WindowConfigElement {

    private int left;
    private int top;
    private int width;
    private int height;
    private boolean maximized;

    //

    public static WindowConfigElement fromStage(final Stage stage) {
        final WindowConfigElement element = new WindowConfigElement();
        final boolean maximized = stage.isMaximized();
        if (maximized) {
            stage.setMaximized(false);
        }
        element.setLeft((int) stage.getX());
        element.setTop((int) stage.getY());
        element.setWidth((int) stage.getWidth());
        element.setHeight((int) stage.getHeight());
        element.setMaximized(maximized);
        return element;
    }

    public void apply(final Stage stage) {
        boolean onScreen = false;
        final ObservableList<Screen> screens = Screen.getScreens();
        for (final Screen screen : screens) {
            if (screen.getBounds().contains(left, top, width, height)) {
                onScreen = true;
            }
        }

        if (!onScreen) {
            final Screen primary = Screen.getPrimary();
            final Rectangle2D primaryBounds = primary.getBounds();

            if (left + width > primaryBounds.getMaxX()) {
                left = (int) primaryBounds.getMaxX() - width;
            }
            if (top + height > primaryBounds.getMaxY()) {
                top = (int) primaryBounds.getMaxY() - height;
            }
        }

        stage.setX(left);
        stage.setY(top);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setMaximized(maximized);
    }

    //

    @XmlAttribute
    public int getLeft() {
        return left;
    }

    public void setLeft(final int left) {
        this.left = left;
    }

    @XmlAttribute
    public int getTop() {
        return top;
    }

    public void setTop(final int top) {
        this.top = top;
    }

    @XmlAttribute
    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    @XmlAttribute
    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    @XmlAttribute
    public boolean isMaximized() {
        return maximized;
    }

    public void setMaximized(final boolean maximized) {
        this.maximized = maximized;
    }
}
