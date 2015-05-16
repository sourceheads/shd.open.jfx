package org.sourceheads.jfx.controls;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.Pane;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class TreePane extends Pane {

    private final ObjectProperty<TreeNode> root = new SimpleObjectProperty<TreeNode>() {

        @Override
        public void set(final TreeNode newValue) {
            if (get() != null) {
                get().removeListener(layoutListener);
                get().removeListener(listChangeListener);
            }
            super.set(newValue);
            newValue.addListener(layoutListener);
            newValue.addListener(listChangeListener);

            getChildren().clear();
            addTreeNodes(newValue);
            requestLayout();
        }
    };

    private final BooleanProperty showRoot = new SimpleBooleanProperty(true);

    private final ObjectProperty<TreeLayout> treeLayout = new SimpleObjectProperty<>(new DefaultTreeLayout(this));

    private final ObjectProperty<ConnectorLayout> connectorLayout =
            new SimpleObjectProperty<>(new DefaultConnectorLayout(this));

    protected final InvalidationListener layoutListener = observable -> requestLayout();

    protected final ListChangeListener<TreeNode> listChangeListener = c -> {
        c.reset();
        while (c.next()) {
            c.getRemoved().forEach(this::removeTreeNodes);
            c.getAddedSubList().forEach(this::addTreeNodes);
        }
        requestLayout();
    };

    protected final void addTreeNodes(final TreeNode treeNode) {
        getChildren().add(treeNode.getNode());
        treeNode.getChildren().forEach(this::addTreeNodes);
    }

    private void removeTreeNodes(final TreeNode treeNode) {
        getChildren().remove(treeNode.getNode());
        treeNode.getChildren().forEach(this::removeTreeNodes);
    }

    //

    public TreePane() {
        showRoot.addListener(layoutListener);
        treeLayout.addListener(layoutListener);
        connectorLayout.addListener(layoutListener);
    }

    public TreePane(final TreeNode root) {
        this();
        this.root.setValue(root);
    }

    @Override
    protected void layoutChildren() {
        // getChildren().clear();
        if (getTreeLayout() != null) {
            getTreeLayout().layout();
        }

        if (getConnectorLayout() != null) {
            getConnectorLayout().layout();
        }

        super.layoutChildren();
    }

    public TreeNode getRoot() {
        return root.get();
    }

    public ObjectProperty<TreeNode> rootProperty() {
        return root;
    }

    public void setRoot(final TreeNode root) {
        this.root.set(root);
    }

    public boolean getShowRoot() {
        return showRoot.get();
    }

    public BooleanProperty showRootProperty() {
        return showRoot;
    }

    public void setShowRoot(final boolean showRoot) {
        this.showRoot.set(showRoot);
    }

    public TreeLayout getTreeLayout() {
        return treeLayout.get();
    }

    public ObjectProperty<TreeLayout> treeLayoutProperty() {
        return treeLayout;
    }

    public void setTreeLayout(final TreeLayout treeLayout) {
        this.treeLayout.set(treeLayout);
    }

    public ConnectorLayout getConnectorLayout() {
        return connectorLayout.get();
    }

    public ObjectProperty<ConnectorLayout> connectorLayoutProperty() {
        return connectorLayout;
    }

    public void setConnectorLayout(final ConnectorLayout connectorLayout) {
        this.connectorLayout.set(connectorLayout);
    }
}
