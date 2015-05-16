package org.sourceheads.jfx.controls;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class TreeNode implements Observable {

    private final ObjectProperty<Node> node = new SimpleObjectProperty<>();

    private final BooleanProperty expanded = new SimpleBooleanProperty() {

        @Override
        protected void invalidated() {
            super.invalidated();
            invalidate();
        }
    };

    protected final ReadOnlyObjectWrapper<TreeNode> parent = new ReadOnlyObjectWrapper<>();

    private final ObservableList<TreeNode> children = FXCollections.observableArrayList();

    private final ObservableList<InvalidationListener> invalidationListeners = FXCollections.observableArrayList();

    private final ObservableList<ListChangeListener<TreeNode>> listChangeListeners = FXCollections.observableArrayList();

    //

    public TreeNode() {
        children.addListener((ListChangeListener<TreeNode>) c -> {
            while (c.next()) {
                c.getRemoved().forEach(n -> n.parent.set(null));
                c.getAddedSubList().forEach(n -> n.parent.set(this));
            }
            listChange(c);
        });
    }

    public TreeNode(final Node node) {
        this.node.setValue(node);
    }

    //

    public Node getNode() {
        return node.get();
    }

    public ObjectProperty<Node> nodeProperty() {
        return node;
    }

    public void setNode(final Node node) {
        this.node.set(node);
    }

    public boolean getExpanded() {
        return expanded.get();
    }

    public BooleanProperty expandedProperty() {
        return expanded;
    }

    public void setExpanded(final boolean expanded) {
        this.expanded.set(expanded);
    }

    public void toggleExpanded() {
        setExpanded(!getExpanded());
    }

    public TreeNode getParent() {
        return parent.get();
    }

    public ReadOnlyObjectProperty<TreeNode> parentProperty() {
        return parent.getReadOnlyProperty();
    }

    public ObservableList<TreeNode> getChildren() {
        return children;
    }

    @Override
    public void addListener(final InvalidationListener listener) {
        this.invalidationListeners.add(listener);
    }

    @Override
    public void removeListener(final InvalidationListener listener) {
        this.invalidationListeners.remove(listener);
    }

    protected void invalidate() {
        invalidationListeners.forEach(l -> l.invalidated(this));
        if (parent.get() != null) {
            parent.get().invalidate();
        }
    }

    public void addListener(final ListChangeListener<TreeNode> listener) {
        this.listChangeListeners.add(listener);
    }

    public void removeListener(final ListChangeListener<TreeNode> listener) {
        this.listChangeListeners.remove(listener);
    }

    protected void listChange(final ListChangeListener.Change<? extends TreeNode> change) {
        listChangeListeners.forEach(l -> l.onChanged(change));
        if (parent.get() != null) {
            parent.get().listChange(change);
        }
    }
}
