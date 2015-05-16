package org.sourceheads.jfx.controls;

import java.util.Iterator;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class DefaultConnectorLayout implements ConnectorLayout {

    public enum HorizontalPositioning {
        RELATIVE, ABSOLUTE_LEFT, ABSOLUTE_RIGHT
    }

    public enum VerticalPositioning {
        RELATIVE, ABSOLUTE
    }

    private final TreePane treePane;

    private final DoubleProperty horizontalPosition = new SimpleDoubleProperty(.5);
    private final ObjectProperty<HorizontalPositioning> horizontalPositioning =
            new SimpleObjectProperty<>(HorizontalPositioning.RELATIVE);

    private final DoubleProperty verticalPosition = new SimpleDoubleProperty(.5);
    private final ObjectProperty<VerticalPositioning> verticalPositioning =
            new SimpleObjectProperty<>(VerticalPositioning.RELATIVE);

    private final DoubleProperty parentSpacing = new SimpleDoubleProperty(0);
    private final DoubleProperty childSpacing = new SimpleDoubleProperty(0);

    public DefaultConnectorLayout(final TreePane treePane) {
        this.treePane = treePane;
        horizontalPosition.addListener(o -> treePane.requestLayout());
        horizontalPositioning.addListener(o -> treePane.requestLayout());
        verticalPosition.addListener(o -> treePane.requestLayout());
        verticalPositioning.addListener(o -> treePane.requestLayout());
        parentSpacing.addListener(o -> treePane.requestLayout());
        childSpacing.addListener(o -> treePane.requestLayout());
    }

    @Override
    public void layout() {
        for (final Iterator<Node> iter = treePane.getChildren().iterator(); iter.hasNext(); ) {
            final Node node = iter.next();
            if (node instanceof Canvas) {
                iter.remove();
            }
        }

        if (treePane.getShowRoot()) {
            drawLines(treePane.getRoot());
        }
        else {
            treePane.getRoot().getChildren().forEach(this::drawLines);
        }
    }

    private void drawLines(final TreeNode treeNode) {
        final ObservableList<TreeNode> children = treeNode.getChildren();
        if (children.isEmpty() || !treeNode.getExpanded()) {
            return;
        }

        final Node parent = treeNode.getNode();

        final TreeNode firstTreeNode = children.get(0);
        final Node firstChild = firstTreeNode.getNode();
        final Node lastChild = children.get(children.size() - 1).getNode();

        final double canvasX = parent.getLayoutX() + parent.prefWidth(Region.USE_PREF_SIZE) + getParentSpacing();
        final double canvasY = parent.getLayoutY();
        final double canvasWidth = firstChild.getLayoutX() - canvasX - getChildSpacing();
        double canvasHeight = Math.min(parent.prefHeight(Region.USE_PREF_SIZE), firstChild.prefHeight(Region.USE_PREF_SIZE));

        if (children.size() > 1) {
            canvasHeight = lastChild.getLayoutY() + lastChild.prefHeight(Region.USE_PREF_SIZE) - firstChild.getLayoutY();
        }

        final Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        canvas.setLayoutX(canvasX);
        canvas.setLayoutY(canvasY);

        final GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.DARKGREY);

        final double y = Math.min(getVerticalHalf(parent), getVerticalHalf(firstChild));
        gc.moveTo(0, y);
        gc.lineTo(canvasWidth, y);
        gc.stroke();

        if (children.size() > 1) {
            final double c = Math.floor(canvasWidth * getHorizontalPosition()) + .5;
            gc.moveTo(c, y);
            gc.lineTo(c, canvasHeight - lastChild.prefHeight(Region.USE_PREF_SIZE) + getVerticalHalf(lastChild));
            gc.stroke();

            for (final TreeNode child : children.subList(1, children.size())) {
                final Node node = child.getNode();
                final double ny = node.getLayoutY() - canvasY + getVerticalHalf(node);
                gc.moveTo(c, ny);
                gc.lineTo(canvasWidth, ny);
                gc.stroke();
            }
        }

        treePane.getChildren().add(canvas);

        children.forEach(this::drawLines);
    }

    private double getVerticalHalf(final Node node) {
        switch (getVerticalPositioning()) {
            case RELATIVE:
                return Math.floor(node.prefHeight(Region.USE_PREF_SIZE) * getVerticalPosition()) + .5;
            case ABSOLUTE:
                return getVerticalPosition();
        }
        throw new IllegalStateException("Unsupported vertical positioning: " + getVerticalPositioning());
    }

    //

    public double getHorizontalPosition() {
        return horizontalPosition.get();
    }

    public DoubleProperty horizontalPositionProperty() {
        return horizontalPosition;
    }

    public void setHorizontalPosition(final double horizontalPosition) {
        this.horizontalPosition.set(horizontalPosition);
    }

    public HorizontalPositioning getHorizontalPositioning() {
        return horizontalPositioning.get();
    }

    public ObjectProperty<HorizontalPositioning> horizontalPositioningProperty() {
        return horizontalPositioning;
    }

    public void setHorizontalPositioning(final HorizontalPositioning horizontalPositioning) {
        this.horizontalPositioning.set(horizontalPositioning);
    }

    public double getVerticalPosition() {
        return verticalPosition.get();
    }

    public DoubleProperty verticalPositionProperty() {
        return verticalPosition;
    }

    public void setVerticalPosition(final double verticalPosition) {
        this.verticalPosition.set(verticalPosition);
    }

    public VerticalPositioning getVerticalPositioning() {
        return verticalPositioning.get();
    }

    public ObjectProperty<VerticalPositioning> verticalPositioningProperty() {
        return verticalPositioning;
    }

    public void setVerticalPositioning(final VerticalPositioning verticalPositioning) {
        this.verticalPositioning.set(verticalPositioning);
    }

    public double getParentSpacing() {
        return parentSpacing.get();
    }

    public DoubleProperty parentSpacingProperty() {
        return parentSpacing;
    }

    public void setParentSpacing(final double parentSpacing) {
        this.parentSpacing.set(parentSpacing);
    }

    public double getChildSpacing() {
        return childSpacing.get();
    }

    public DoubleProperty childSpacingProperty() {
        return childSpacing;
    }

    public void setChildSpacing(final double childSpacing) {
        this.childSpacing.set(childSpacing);
    }
}
