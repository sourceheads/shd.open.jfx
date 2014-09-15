package org.sourceheads.jfx.util;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class ClipboardUtils {

    public static void pastePlainText(final String text) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final Map<DataFormat, Object> content = new HashMap<>();
        content.put(DataFormat.PLAIN_TEXT, text);
        clipboard.setContent(content);
    }
}
