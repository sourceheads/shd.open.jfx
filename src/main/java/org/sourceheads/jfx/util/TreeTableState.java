package org.sourceheads.jfx.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class TreeTableState<S> {

    private final Set<S> expanded = new HashSet<>();
    private final Set<S> selected;

    public TreeTableState(final TreeTableView<S> treeTableView) {
        walkTree(treeTableView.getRoot(), item -> {
            if (item.isExpanded()) {
                expanded.add(item.getValue());
            }
        });

        this.selected = treeTableView.getSelectionModel().getSelectedItems().stream()
                .map(TreeItem::getValue).collect(Collectors.toSet());
    }

    public void apply(final TreeTableView<S> treeTableView) {
        walkTree(treeTableView.getRoot(), item -> {
            if (expanded.contains(item.getValue())) {
                item.setExpanded(true);
            }
        });

        final TreeTableView.TreeTableViewSelectionModel<S> selectionModel = treeTableView.getSelectionModel();
        selectionModel.clearSelection();
        walkTree(treeTableView.getRoot(), item -> {
            if (selected.contains(item.getValue())) {
                selectionModel.select(item);
            }
        });
    }

    public static <S> void walkTree(final TreeItem<S> start, final Consumer<TreeItem<S>> consumer) {
        consumer.accept(start);
        start.getChildren().forEach(item -> walkTree(item, consumer));
    }
}
