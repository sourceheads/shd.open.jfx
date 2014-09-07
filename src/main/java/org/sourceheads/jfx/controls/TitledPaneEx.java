package org.sourceheads.jfx.controls;

import com.sun.javafx.scene.control.skin.TitledPaneSkin;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

/**
 * <p>Extension of {@link javafx.scene.control.TitledPane} expanding its title graphics (if it's a
 * {@link javafx.scene.layout.Region}) to full width to support right aligned content.</p>
 * <p>Note: Leave the title text empty and include a label in the graphics region instead.</p>
 *
 * @author Stefan Fiedler
 */
public class TitledPaneEx extends TitledPane {

    public TitledPaneEx() {
    }

    public TitledPaneEx(final Node content) {
        super(null, content);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TitledPaneExSkin(this);
    }

    protected static class TitledPaneExSkin extends TitledPaneSkin {

        final Region arrow;
        final Region title;

        public TitledPaneExSkin(final TitledPane titledPane) {
            super(titledPane);
            arrow = (Region) getSkinnable().lookup(".arrow-button");
            title = (Region) getSkinnable().lookup(".title");
        }

        @Override
        protected double computeMinWidth(final double height, final double topInset, final double rightInset,
                final double bottomInset, final double leftInset) {
            setRegionGraphicsPrefWidth(USE_COMPUTED_SIZE);
            return super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset);
        }

        @Override
        protected double computePrefWidth(final double height, final double topInset, final double rightInset,
                final double bottomInset, final double leftInset) {
            setRegionGraphicsPrefWidth(USE_COMPUTED_SIZE);
            return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
        }

        @Override
        protected void layoutChildren(final double x, final double y, final double w, final double h) {
            super.layoutChildren(x, y, w, h);

            final Insets titlePadding = title.getPadding();
            double space = titlePadding.getLeft() + titlePadding.getRight();
            if (arrow != null) {
                space += arrow.prefWidth(-1);
            }
            setRegionGraphicsPrefWidth(w - space);
        }

        private void setRegionGraphicsPrefWidth(final double prefWidth) {
            final Node graphic = getSkinnable().getGraphic();
            if (graphic != null && graphic instanceof Region) {
                ((Region) graphic).setPrefWidth(prefWidth);
            }
        }
    }
}
