package org.sourceheads.jfx.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class ImageButton extends Button {

    private final StringProperty imageUrl = new SimpleStringProperty();

    public ImageButton() {
        initialize();
    }

    public ImageButton(final String text, final String imageUrl) {
        super(text);
        initialize();
        this.imageUrl.setValue(imageUrl);
    }

    private void initialize() {
        imageUrl.addListener((observable, oldValue, newValue) ->
                setGraphic(newValue != null ? new ImageView(imageUrl.get()) : null));
    }

    public String getImageUrl() {
        return imageUrl.get();
    }

    public StringProperty imageUrlProperty() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl.set(imageUrl);
    }
}
