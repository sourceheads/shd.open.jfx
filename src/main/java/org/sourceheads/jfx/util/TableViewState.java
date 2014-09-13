package org.sourceheads.jfx.util;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.control.TableView;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class TableViewState<S> {

    private final Set<S> selected;
    private boolean selectFirstByDefault = false;

    public TableViewState(final TableView<S> tableView) {
        this.selected = new HashSet<>(tableView.getSelectionModel().getSelectedItems());
    }

    public void apply(final TableView<S> tableView) {
        final TableView.TableViewSelectionModel<S> selectionModel = tableView.getSelectionModel();
        selectionModel.clearSelection();

        if (!tableView.getItems().isEmpty()) {
            this.selected.forEach(selectionModel::select);

            if (selectFirstByDefault && selectionModel.isEmpty()) {
                selectionModel.selectFirst();
            }
        }
    }

    public TableViewState<S> setSelectFirstByDefault(final boolean selectFirstByDefault) {
        this.selectFirstByDefault = selectFirstByDefault;
        return this;
    }
}
