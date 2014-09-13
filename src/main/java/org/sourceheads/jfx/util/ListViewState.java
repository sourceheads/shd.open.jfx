package org.sourceheads.jfx.util;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class ListViewState<T> {

    private final Set<T> selected;
    private boolean selectFirstByDefault = false;

    public ListViewState(final ListView<T> listView) {
        this.selected = new HashSet<>(listView.getSelectionModel().getSelectedItems());
    }

    public void apply(final ListView<T> listView) {
        final MultipleSelectionModel<T> selectionModel = listView.getSelectionModel();
        selectionModel.clearSelection();

        if (!listView.getItems().isEmpty()) {
            this.selected.forEach(selectionModel::select);

            if (selectFirstByDefault && selectionModel.isEmpty()) {
                selectionModel.selectFirst();
            }
        }
    }

    public ListViewState<T> setSelectFirstByDefault(final boolean selectFirstByDefault) {
        this.selectFirstByDefault = selectFirstByDefault;
        return this;
    }
}
