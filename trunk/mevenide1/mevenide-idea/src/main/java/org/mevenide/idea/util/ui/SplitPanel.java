package org.mevenide.idea.util.ui;

import java.awt.*;
import javax.swing.*;

/**
 * @author Arik
 */
public class SplitPanel<First extends JComponent, Second extends JComponent>
        extends JPanel {
    /**
     * The first component.
     */
    private First first;

    /**
     * The second component.
     */
    private Second second;

    /**
     * The split pane component that layouts the first/second components.
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
     * @param pTop    the first component
     * @param pBottom the second component
     */
    public SplitPanel(final First pTop, final Second pBottom) {
        this(pTop, pBottom, true);
    }

    /**
     * Creates an instance using the two given components.
     *
     * @param pTop        the first component
     * @param pBottom     the second component
     * @param pAddBorders whether to add en empty border around the components
     */
    public SplitPanel(final First pTop, final Second pBottom, final boolean pAddBorders) {
        this(pTop, pBottom, pAddBorders, true);
    }

    /**
     * Creates an instance using the two given components.
     *
     * @param pTop        the first component
     * @param pBottom     the second component
     * @param pAddBorders whether to add en empty border around the components
     */
    public SplitPanel(final First pTop,
                      final Second pBottom,
                      final boolean pAddBorders,
                      final boolean pVertical) {
        split = new JSplitPane(
                pVertical ?
                        JSplitPane.VERTICAL_SPLIT :
                        JSplitPane.HORIZONTAL_SPLIT,
                true,
                null,
                null);

        addBorders = pAddBorders;

        setLayout(new BorderLayout());
        add(split, BorderLayout.CENTER);

        setFirst(pTop);
        setSecond(pBottom);
    }

    /**
     * Returns the first component.
     *
     * @return the first component
     */
    public final First getFirst() {
        return first;
    }

    /**
     * Returns the second component.
     *
     * @return the second component
     */
    public final Second getSecond() {
        return second;
    }

    public void setSecond(final Second pSecond) {
        split.setBottomComponent(pSecond);
        second = pSecond;
        if (second != null && addBorders) {
            if (split.getOrientation() == JSplitPane.VERTICAL_SPLIT)
                UIUtils.installBorder(second, 5, 0, 0, 0);
            else
                UIUtils.installBorder(second, 0, 5, 0, 0);
        }
    }

    public void setFirst(final First pFirst) {
        split.setTopComponent(pFirst);
        first = pFirst;
        if (first != null && addBorders) {
            if (split.getOrientation() == JSplitPane.VERTICAL_SPLIT)
                UIUtils.installBorder(first, 0, 0, 5, 0);
            else
                UIUtils.installBorder(second, 0, 0, 0, 5);
        }
    }

    public JSplitPane getSplit() {
        return split;
    }
}
