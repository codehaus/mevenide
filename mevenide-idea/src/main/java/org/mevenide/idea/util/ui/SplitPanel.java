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
    private Top top;

    /**
     * The bottom component.
     */
    private Bottom bottom;

    /**
     * The split pane component that layouts the top/bottom components.
     */
    private final JSplitPane split;

    /**
     * Whether to add borders around the components.
     */
    private final boolean addBorders;

    /**
     * Creates an instance with no components.
     */
    public SplitPanel() {
        this(null, null);
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
     * Creates an instance using the two given components.
     *
     * @param pTop the top component
     * @param pBottom the bottom component
     * @param pAddBorders whether to add en empty border around the components
     */
    public SplitPanel(final Top pTop, final Bottom pBottom, final boolean pAddBorders) {
        split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                true,
                null,
                null);

        addBorders = pAddBorders;

        setLayout(new BorderLayout());
        add(split, BorderLayout.CENTER);

        setTop(pTop);
        setBottom(pBottom);
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

    public void setBottom(final Bottom pBottom) {
        split.setBottomComponent(pBottom);
        bottom = pBottom;
        if(bottom != null && addBorders)
            UIUtils.installBorder(bottom);
    }

    public void setTop(final Top pTop) {
        split.setTopComponent(pTop);
        top = pTop;
        if(top != null && addBorders)
            UIUtils.installBorder(top);
    }
}
