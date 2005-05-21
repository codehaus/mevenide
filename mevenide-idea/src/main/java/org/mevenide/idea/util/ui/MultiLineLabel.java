package org.mevenide.idea.util.ui;

import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * @author Arik
 */
public class MultiLineLabel extends JTextArea {

    public MultiLineLabel(String text, int rows, int columns) {
        super(text, rows, columns);
        init();
    }

    public MultiLineLabel(String text) {
        super(text);
        init();
    }

    public MultiLineLabel(int rows, int columns) {
        super(rows, columns);
        init();
    }

    public MultiLineLabel(Document doc, String text, int rows, int columns) {
        super(doc, text, rows, columns);
        init();
    }

    public MultiLineLabel(Document doc) {
        super(doc);
        init();
    }

    public MultiLineLabel() {
        init();
    }

    private void init() {
        setFocusable(false);
        setEditable(false);
        setWrapStyleWord(true);
        setLineWrap(true);
    }
}
