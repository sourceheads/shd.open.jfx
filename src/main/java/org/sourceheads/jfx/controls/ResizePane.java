package org.sourceheads.jfx.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.javafx.css.converters.EnumConverter;
import com.sun.javafx.css.converters.SizeConverter;

import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
@DefaultProperty("content")
public class ResizePane extends Control {

    private static final String DEFAULT_CLASS = "resize-pane";

    private ObjectProperty<Side> side;
    private DoubleProperty spacing;
    private final ObjectProperty<Node> content = new SimpleObjectProperty<>();

    @SuppressWarnings("unchecked")
    public ResizePane() {
        getStyleClass().setAll(DEFAULT_CLASS);
        ((StyleableProperty<Boolean>) focusTraversableProperty()).applyStyle(null, false);
        pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, true);
        pseudoClassStateChanged(BOTTOM_PSEUDOCLASS_STATE, true);
    }

    public ResizePane(final Node content) {
        this();
        this.content.set(content);
    }

    public ResizePane(final Side side, final Node content) {
        this();
        setSide(side);
        this.content.set(content);
    }

    //

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ResizePaneSkin(this);
    }

    //

    public final Side getSide() {
        return side != null ? side.get() : Side.BOTTOM;
    }

    public final void setSide(final Side side) {
        sideProperty().set(side);
    }

    public final ObjectProperty<Side> sideProperty() {
        if (side == null) {
            side = new StyleableObjectProperty<Side>(Side.BOTTOM) {

                @Override
                @SuppressWarnings("PrivateMemberAccessBetweenOuterAndInnerClass")
                protected void invalidated() {
                    pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE, get().isVertical());
                    pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, get().isHorizontal());

                    pseudoClassStateChanged(BOTTOM_PSEUDOCLASS_STATE, get() == Side.BOTTOM);
                    pseudoClassStateChanged(TOP_PSEUDOCLASS_STATE, get() == Side.TOP);
                    pseudoClassStateChanged(LEFT_PSEUDOCLASS_STATE, get() == Side.LEFT);
                    pseudoClassStateChanged(RIGHT_PSEUDOCLASS_STATE, get() == Side.RIGHT);
                }

                @Override
                public Object getBean() {
                    return ResizePane.this;
                }

                @Override
                public String getName() {
                    return "side";
                }

                @Override
                public CssMetaData<? extends Styleable, Side> getCssMetaData() {
                    return StyleableProperties.SIDE;
                }
            };
        }
        return side;
    }

    public final Node getContent() {
        return content.get();
    }

    public final ObjectProperty<Node> contentProperty() {
        return content;
    }

    public final void setContent(final Node content) {
        this.content.set(content);
    }

    public final DoubleProperty spacingProperty() {
        if (spacing == null) {
            spacing = new StyleableDoubleProperty() {

                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public Object getBean() {
                    return ResizePane.this;
                }

                @Override
                public String getName() {
                    return "spacing";
                }

                @Override
                public CssMetaData<ResizePane, Number> getCssMetaData() {
                    return StyleableProperties.SPACING;
                }
            };
        }
        return spacing;
    }

    public final void setSpacing(final double value) {
        spacingProperty().set(value);
    }

    public final double getSpacing() {
        return spacing == null ? 0 : spacing.get();
    }

    //

    private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
    private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");

    private static final PseudoClass TOP_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("top");
    private static final PseudoClass BOTTOM_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("bottom");
    private static final PseudoClass LEFT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("left");
    private static final PseudoClass RIGHT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("right");

    private static class StyleableProperties {

        private static final CssMetaData<ResizePane, Side> SIDE = new CssMetaData<ResizePane, Side>(
                "-fx-side", new EnumConverter<>(Side.class), Side.BOTTOM) {

            @Override
            public Side getInitialValue(final ResizePane styleable) {
                return styleable.getSide();
            }

            @Override
            @SuppressWarnings("PrivateMemberAccessBetweenOuterAndInnerClass")
            public boolean isSettable(final ResizePane styleable) {
                return styleable.side == null || !styleable.side.isBound();
            }

            @Override
            @SuppressWarnings("unchecked")
            public StyleableProperty<Side> getStyleableProperty(final ResizePane styleable) {
                return (StyleableProperty<Side>) styleable.sideProperty();
            }
        };

        private static final CssMetaData<ResizePane, Number> SPACING = new CssMetaData<ResizePane, Number>(
                "-fx-spacing", SizeConverter.getInstance(), 0d) {

            @Override
            @SuppressWarnings("PrivateMemberAccessBetweenOuterAndInnerClass")
            public boolean isSettable(final ResizePane node) {
                return node.spacing == null || !node.spacing.isBound();
            }

            @Override
            @SuppressWarnings("unchecked")
            public StyleableProperty<Number> getStyleableProperty(final ResizePane node) {
                return (StyleableProperty<Number>) node.spacingProperty();
            }
        };


        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final ArrayList<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<>(Control.getClassCssMetaData());
            styleables.add(SIDE);
            styleables.add(SPACING);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @SuppressWarnings("PrivateMemberAccessBetweenOuterAndInnerClass")
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
}
