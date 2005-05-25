package org.mevenide.idea.util.ui;

import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;

/**
 * @author Arik
 */
public class SplitPanel<Top extends JComponent, Bottom extends JComponent> extends JPanel {
    /**
     * The top component.
     */
    private final Top top;

    /**
     * The bottom component.
     */
    private final Bottom bottom;

    /**
     * Creates an instance using the two given components.
     *
     * @param pTop the top component
     * @param pBottom the bottom component
     */
    public SplitPanel(final Top pTop, final Bottom pBottom, final boolean pAddBorders) {
        top = pTop;
        bottom = pBottom;

        final JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                true,
                top, bottom);

        if (pAddBorders) {
            UIUtils.installBorder(top);
            UIUtils.installBorder(bottom);
        }

        setLayout(new BorderLayout());
        add(split, BorderLayout.CENTER);
    }

    /**
     * Creates an instance using the two given components.
     *
     * @param pTop the top component
     * @param pBottom the bottom component
     */
    public SplitPanel(final Top pTop, final Bottom pBottom) {
        this(pTop, pBottom, true);
    }

    /**
     * Returns the top component.
     *
     * @return the top component
     */
    public final Top getTop() {
        return top;
    }

    /**
     * Returns the bottom component.
     *
     * @return the bottom component
     */
    public final Bottom getBottom() {
        return bottom;
    }
}
