package org.sourceheads.jfx.controls;

import java.util.Collection;
import java.util.Collections;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class DefaultTreeLayout implements TreeLayout {

    private final TreePane treePane;
    private final DoubleProperty verticalSpacing = new SimpleDoubleProperty();
    private final DoubleProperty horizontalSpacing = new SimpleDoubleProperty();
    private final BooleanProperty alignCousins = new SimpleBooleanProperty();

    public DefaultTreeLayout(final TreePane treePane) {
        this.treePane = treePane;
        final InvalidationListener listener = o -> treePane.requestLayout();
        verticalSpacing.addListener(listener);
        horizontalSpacing.addListener(listener);
        alignCousins.addListener(listener);
    }

    @Override
    public void layout() {
        final TreeNode root = treePane.getRoot();
        final boolean showRoot = treePane.getShowRoot();
        setVisible(root, showRoot);

        final Insets padding = treePane.getPadding();
        layoutNodes(padding.getTop(), padding.getLeft(),
                showRoot ? Collections.singletonList(root) : root.getChildren());
    }

    private void layoutNodes(double top, final double left, final Collection<TreeNode> treeNodes) {

        double alignedChildren = 0;
        if (getAlignCousins()) {
            alignedChildren = getTotalWidth(treeNodes);
        }

        // double childLeft = left;
        for (final TreeNode treeNode : treeNodes) {
            final Node node = treeNode.getNode();
            // treePane.getChildren().add(node);
            node.setLayoutX(left);
            node.setLayoutY(top);

            // childLeft = Math.max(childLeft, left + node.prefWidth(Region.USE_COMPUTED_SIZE));
            if (treeNode.getExpanded()) {
                treeNode.getChildren().forEach(n -> setVisible(n, true));
                final double childLeft = left + (getAlignCousins()
                        ? alignedChildren
                        : node.prefWidth(Region.USE_COMPUTED_SIZE));
                layoutNodes(top, childLeft + getHorizontalSpacing(), treeNode.getChildren());
            }
            else {
                treeNode.getChildren().forEach(this::collapse);
            }

            // top += node.prefHeight(Region.USE_COMPUTED_SIZE) + treePane.getVerticalSpacing();
            top += getTotalHeight(treeNode);
        }
    }

    private double getTotalWidth(final Collection<TreeNode> treeNodes) {
        double width = 0;
        for (final TreeNode treeNode : treeNodes) {
            final Node node = treeNode.getNode();
            width = Math.max(width, node.prefWidth(Region.USE_COMPUTED_SIZE));
        }
        return width;
    }

    private double getTotalHeight(final TreeNode treeNode) {
        final Node node = treeNode.getNode();
        final double nodeHeight = node.prefHeight(Region.USE_COMPUTED_SIZE) + getVerticalSpacing();

        if (treeNode.getExpanded() && !treeNode.getChildren().isEmpty()) {
            return Math.max(nodeHeight, getTotalHeight(treeNode.getChildren()));
        }

        return nodeHeight;
    }

    private double getTotalHeight(final Collection<TreeNode> treeNodes) {
        double height = 0;
        for (final TreeNode treeNode : treeNodes) {
            // final Node node = treeNode.getNode();
            // height += node.prefHeight(Region.USE_COMPUTED_SIZE) + treePane.getVerticalSpacing();
            height += getTotalHeight(treeNode);
        }
        return height;
    }

    private void collapse(final TreeNode treeNode) {
        setVisible(treeNode, false);
        treeNode.getChildren().forEach(this::collapse);
    }

    private void setVisible(final TreeNode treeNode, final boolean visible) {
        treeNode.getNode().setManaged(visible);
        treeNode.getNode().setVisible(visible);
    }

    //

    public double getVerticalSpacing() {
        return verticalSpacing.get();
    }

    public DoubleProperty verticalSpacingProperty() {
        return verticalSpacing;
    }

    public void setVerticalSpacing(final double verticalSpacing) {
        this.verticalSpacing.set(verticalSpacing);
    }

    public double getHorizontalSpacing() {
        return horizontalSpacing.get();
    }

    public DoubleProperty horizontalSpacingProperty() {
        return horizontalSpacing;
    }

    public void setHorizontalSpacing(final double horizontalSpacing) {
        this.horizontalSpacing.set(horizontalSpacing);
    }

    public boolean getAlignCousins() {
        return alignCousins.get();
    }

    public BooleanProperty alignCousinsProperty() {
        return alignCousins;
    }

    public void setAlignCousins(final boolean alignCousins) {
        this.alignCousins.set(alignCousins);
    }
}
