package org.sourceheads.jfx.controls;

import java.util.Collections;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

import javafx.geometry.HPos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class ResizePaneSkin extends BehaviorSkinBase<ResizePane, BehaviorBase<ResizePane>> {

    private Node content;
    private Handle handle;
    Side side;
    private double minSize;

    protected ResizePaneSkin(final ResizePane resizePane) {
        super(resizePane, new BehaviorBase<>(resizePane, Collections.emptyList()));

        side = resizePane.getSide();

        handle = new Handle();
        content = resizePane.getContent();
        getChildren().setAll(content, handle);

        handle.setOnMousePressed(event -> {
            minSize = resizePane.minHeight(-1);
            handle.setInitialPos(resizePane.getHeight());
            handle.setPressPos(event.getSceneY());
            event.consume();
        });

        handle.setOnMouseDragged(event -> {
            double delta = 0;
            delta = event.getSceneY();
            delta -= handle.getPressPos();
            resizePane.setMinHeight(Math.max(handle.getInitialPos() + delta, minSize));
            event.consume();
        });

        registerChangeListener(resizePane.contentProperty(), "CONTENT");
        registerChangeListener(resizePane.sideProperty(), "SIDE");
        registerChangeListener(resizePane.widthProperty(), "WIDTH");
        registerChangeListener(resizePane.heightProperty(), "HEIGHT");
    }

    @Override
    protected void handleControlPropertyChanged(final String property) {
        super.handleControlPropertyChanged(property);
        if ("CONTENT".equals(property)) {
            content = getSkinnable().getContent();
            getChildren().setAll(content, handle);
            getSkinnable().requestLayout();
        }
        else if ("SIDE".equals(property)) {
            side = getSkinnable().getSide();
            handle.setGrabberStyle(side);
            getSkinnable().requestLayout();
        }
        else if ("WIDTH".equals(property) || "HEIGHT".equals(property)) {
            getSkinnable().requestLayout();
        }
    }

    @Override
    protected void layoutChildren(final double contentX, final double contentY,
            final double width, final double height) {

        final double paddingX = snappedLeftInset();
        final double paddingY = snappedTopInset();
        final double handleSize = handle.prefWidth(-1);

        if (content != null) {
            content.setClip(new Rectangle(width, height - handleSize));
            layoutInArea(content, paddingX, paddingY, width, height - handleSize,
                    0, HPos.CENTER, VPos.CENTER);
        }

        handle.resize(width, handleSize);
        positionInArea(handle, paddingX, height - handleSize, width, handleSize,
                0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    protected double computeMinWidth(final double height, final double topInset, final double rightInset,
            final double bottomInset, final double leftInset) {
        return (side.isVertical() ? handle.prefWidth(-1) : 0)
                + (content != null ? content.minWidth(-1) : 0)
                + leftInset + rightInset;
    }

    @Override
    protected double computeMinHeight(final double width, final double topInset, final double rightInset,
            final double bottomInset, final double leftInset) {
        return (side.isHorizontal() ? handle.prefWidth(-1) : 0)
                + (content != null ? content.minHeight(-1) : 0)
                + topInset + bottomInset;
    }

    @Override
    protected double computeMaxWidth(final double height, final double topInset, final double rightInset,
            final double bottomInset, final double leftInset) {
        return (side.isVertical() ? handle.prefWidth(-1) : 0)
                + (content != null ? content.maxWidth(-1) : 0)
                + leftInset + rightInset;
    }

    @Override
    protected double computeMaxHeight(final double width, final double topInset, final double rightInset,
            final double bottomInset, final double leftInset) {
        return (side.isHorizontal() ? handle.prefWidth(-1) : 0)
                + (content != null ? content.maxHeight(-1) : 0)
                + topInset + bottomInset;
    }

    @Override
    protected double computePrefWidth(final double height, final double topInset, final double rightInset,
            final double bottomInset, final double leftInset) {
        return (side.isVertical() ? handle.prefWidth(-1) : 0)
                + (content != null ? content.prefWidth(-1) : 0)
                + leftInset + rightInset;
    }

    @Override
    protected double computePrefHeight(final double width, final double topInset, final double rightInset,
            final double bottomInset, final double leftInset) {
        return (side.isHorizontal() ? handle.prefWidth(-1) : 0)
                + (content != null ? content.prefHeight(-1) : 0)
                + topInset + bottomInset;
    }

    class Handle extends StackPane {

        private double initialPos;
        private double pressPos;
        private StackPane grabber;

        public Handle() {
            getStyleClass().setAll("resize-pane-handle");

            grabber = new StackPane() {

                @Override
                protected double computeMinWidth(final double height) {
                    return 0;
                }

                @Override
                protected double computeMinHeight(final double width) {
                    return 0;
                }

                @Override
                protected double computePrefWidth(final double height) {
                    return snappedLeftInset() + snappedRightInset();
                }

                @Override
                protected double computePrefHeight(final double width) {
                    return snappedTopInset() + snappedBottomInset();
                }

                @Override
                protected double computeMaxWidth(final double height) {
                    return computePrefWidth(-1);
                }

                @Override
                protected double computeMaxHeight(final double width) {
                    return computePrefHeight(-1);
                }
            };
            setGrabberStyle(side);
            getChildren().add(grabber);
        }

        public final void setGrabberStyle(final Side side) {
            grabber.getStyleClass().clear();
            if (side.isHorizontal()) {
                grabber.getStyleClass().setAll("horizontal-grabber");
                setCursor(Cursor.V_RESIZE);
            }
            else {
                grabber.getStyleClass().setAll("vertical-grabber");
                setCursor(Cursor.H_RESIZE);
            }
        }

        @Override protected double computeMinWidth(final double height) {
            return computePrefWidth(height);
        }

        @Override protected double computeMinHeight(final double width) {
            return computePrefHeight(width);
        }

        @Override protected double computePrefWidth(final double height) {
            return snappedLeftInset() + snappedRightInset();
        }

        @Override protected double computePrefHeight(final double width) {
            return snappedTopInset() + snappedBottomInset();
        }

        @Override protected double computeMaxWidth(final double height) {
            return computePrefWidth(height);
        }

        @Override protected double computeMaxHeight(final double width) {
            return computePrefHeight(width);
        }

        @Override protected void layoutChildren() {
            final double grabberWidth = grabber.prefWidth(-1);
            final double grabberHeight = grabber.prefHeight(-1);
            final double grabberX = (getWidth() - grabberWidth)/2;
            final double grabberY = (getHeight() - grabberHeight)/2;
            grabber.resize(grabberWidth, grabberHeight);
            positionInArea(grabber, grabberX, grabberY, grabberWidth, grabberHeight,
                    /*baseline ignored*/ 0, HPos.CENTER, VPos.CENTER);
        }

        public double getInitialPos() {
            return initialPos;
        }

        public void setInitialPos(final double initialPos) {
            this.initialPos = initialPos;
        }

        public double getPressPos() {
            return pressPos;
        }

        public void setPressPos(final double pressPos) {
            this.pressPos = pressPos;
        }
    }
}
