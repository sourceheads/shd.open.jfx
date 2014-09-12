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

    private Content content;
    private Handle handle;
    protected Side side;

    protected ResizePaneSkin(final ResizePane resizePane) {
        super(resizePane, new BehaviorBase<>(resizePane, Collections.emptyList()));

        side = resizePane.getSide();

        handle = new Handle();
        content = new Content(resizePane.getContent());
        getChildren().setAll(content, handle);

        initHandleMouseHandlers();

        registerChangeListener(resizePane.contentProperty(), "CONTENT");
        registerChangeListener(resizePane.sideProperty(), "SIDE");
        registerChangeListener(resizePane.widthProperty(), "WIDTH");
        registerChangeListener(resizePane.heightProperty(), "HEIGHT");
    }

    private void initHandleMouseHandlers() {
        handle.setOnMousePressed(event -> {
            handle.setInitialPos(content.getSize());
            handle.setPressPos(side.isHorizontal() ? event.getSceneY() : event.getSceneX());
            event.consume();
        });

        handle.setOnMouseDragged(event -> {
            final double delta = (side.isHorizontal() ? event.getSceneY() : event.getSceneX()) - handle.getPressPos();
            switch (side) {
                case BOTTOM:
                case RIGHT:
                    content.setSize(handle.getInitialPos() + delta);
                    getSkinnable().requestLayout();
                    break;
                case TOP:
                case LEFT:
                    content.setSize(handle.getInitialPos() - delta);
                    getSkinnable().requestLayout();
                    break;
                default:
                    throw new IllegalStateException("Unsupported side: " + side);
            }
            event.consume();
        });
    }

    @Override
    protected void handleControlPropertyChanged(final String property) {
        super.handleControlPropertyChanged(property);
        switch (property) {
            case "CONTENT":
                content = new Content(getSkinnable().getContent());
                getChildren().setAll(content, handle);
                getSkinnable().requestLayout();
                break;
            case "SIDE":
                side = getSkinnable().getSide();
                handle.setGrabberStyle(side);
                getSkinnable().requestLayout();
                break;
            case "WIDTH":
            case "HEIGHT":
                getSkinnable().requestLayout();
                break;
        }
    }

    @Override
    protected void layoutChildren(final double contentX, final double contentY,
            final double width, final double height) {

        final double paddingX = snappedLeftInset();
        final double paddingY = snappedTopInset();
        final double handleSize = handle.prefWidth(-1);
        final double spacing = getSkinnable().getSpacing();

        if (side.isHorizontal()) {
            handle.resize(width, handleSize);
        }
        else {
            handle.resize(handleSize, height);
        }

        switch (side) {
            case BOTTOM:
                getSkinnable().resize(width, content.getSize() + handleSize);
                content.setClipSize(width, content.getSize());
                layoutInArea(content, paddingX, paddingY, width, content.getSize());
                positionInArea(handle, paddingX, paddingY + content.getSize() + spacing, width, handleSize);
                break;
            case TOP:
                content.setClipSize(width, height - handleSize);
                layoutInArea(content, paddingX, paddingY + handleSize + spacing, width, content.getSize());
                positionInArea(handle, paddingX, paddingY, width, handleSize);
                break;
            case LEFT:
                content.setClipSize(width - handleSize, height);
                layoutInArea(content, paddingX + handleSize + spacing, paddingY, content.getSize(), height);
                positionInArea(handle, paddingX, paddingY, handleSize, height);
                break;
            case RIGHT:
                content.setClipSize(width - handleSize, height);
                layoutInArea(content, paddingX, paddingY, content.getSize(), height);
                positionInArea(handle, paddingX + content.getSize() + spacing, paddingY, handleSize, height);
                break;
            default:
                throw new IllegalStateException("Unsupported side: " + side);
        }
    }

    private void positionInArea(final Node child, final double areaX, final double areaY,
            final double areaWidth, final double areaHeight) {
        positionInArea(child, areaX, areaY, areaWidth, areaHeight, 0, HPos.CENTER, VPos.CENTER);
    }

    private void layoutInArea(final Node child, final double areaX, final double areaY,
            final double areaWidth, final double areaHeight) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, 0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    protected double computeMinWidth(final double height, final double topInset, final double rightInset,
            final double bottomInset, final double leftInset) {
        return leftInset + rightInset + (side.isVertical()
                ? content.getSize() + handle.prefWidth(-1) + getSkinnable().getSpacing()
                : content.minWidth(-1));
    }

    @Override
    protected double computeMinHeight(final double width, final double topInset, final double rightInset,
            final double bottomInset, final double leftInset) {
        return topInset + bottomInset + (side.isHorizontal()
                ? content.getSize() + handle.prefWidth(-1) + getSkinnable().getSpacing()
                : content.minHeight(-1));
    }

    @Override
    protected double computePrefWidth(final double height, final double topInset, final double rightInset,
            final double bottomInset, final double leftInset) {
        return leftInset + rightInset + (side.isVertical()
                ? content.getSize() + handle.prefWidth(-1) + getSkinnable().getSpacing()
                : content.prefWidth(-1));
    }

    @Override
    protected double computePrefHeight(final double width, final double topInset, final double rightInset,
            final double bottomInset, final double leftInset) {
        return topInset + bottomInset + (side.isHorizontal()
                ? content.getSize() + handle.prefWidth(-1) + getSkinnable().getSpacing()
                : content.prefHeight(-1));
    }

    @Override
    protected double computeMaxWidth(final double height, final double topInset, final double rightInset,
            final double bottomInset, final double leftInset) {
        return leftInset + rightInset + content.maxWidth(-1) +
                (side.isVertical() ? handle.prefWidth(-1) + getSkinnable().getSpacing() : 0);
    }

    @Override
    protected double computeMaxHeight(final double width, final double topInset, final double rightInset,
            final double bottomInset, final double leftInset) {
        return topInset + bottomInset + content.maxHeight(-1) +
                (side.isHorizontal() ? handle.prefWidth(-1) + getSkinnable().getSpacing() : 0);
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

        @Override
        protected double computeMinWidth(final double height) {
            return computePrefWidth(height);
        }

        @Override
        protected double computeMinHeight(final double width) {
            return computePrefHeight(width);
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
            return computePrefWidth(height);
        }

        @Override
        protected double computeMaxHeight(final double width) {
            return computePrefHeight(width);
        }

        @Override
        protected void layoutChildren() {
            final double grabberWidth = grabber.prefWidth(-1);
            final double grabberHeight = grabber.prefHeight(-1);
            final double grabberX = (getWidth() - grabberWidth) / 2;
            final double grabberY = (getHeight() - grabberHeight) / 2;
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

    class Content extends StackPane {

        private Node content;
        private Rectangle clipRect;
        private double size;

        public Content(final Node node) {
            this.content = node;
            this.size = side.isVertical()
                    ? Math.max(content.prefWidth(-1), content.minWidth(-1))
                    : Math.max(content.prefHeight(-1), content.minHeight(-1));
            this.clipRect = new Rectangle();
            setClip(clipRect);
            getChildren().add(node);
        }

        public Node getContent() {
            return content;
        }

        protected void setClipSize(final double w, final double h) {
            clipRect.setWidth(w);
            clipRect.setHeight(h);
        }

        public double getSize() {
            return size;
        }

        public void setSize(final double size) {
            this.size = Math.min(Math.max(size, getMinSize()), getMaxSize());
        }

        public double getMinSize() {
            return content != null ? (side.isVertical() ? content.minWidth(-1) : content.minHeight(-1)) : 0;
        }

        public double getMaxSize() {
            return content != null ? (side.isVertical() ? content.maxWidth(-1) : content.maxHeight(-1)) : 0;
        }

        @Override
        protected double computeMaxWidth(final double height) {
            return snapSize(content.maxWidth(height));
        }

        @Override
        protected double computeMaxHeight(final double width) {
            return snapSize(content.maxHeight(width));
        }
    }
}
