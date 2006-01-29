package org.mevenide.idea.util.ui;

import java.awt.*;
import javax.swing.*;

/**
 * @author Arik
 */
public class LabeledPanel extends JPanel {
    protected final JComponent component;

    public LabeledPanel(final String pLabel,
                        final JComponent pComponent) {
        component = pComponent;
        init(pLabel);
    }

    public LabeledPanel(final boolean pDoubleBuffered,
                        final String pLabel,
                        final JComponent pComponent) {
        super(pDoubleBuffered);
        component = pComponent;
        init(pLabel);
    }

    private void init(final String pLabel) {
        setLayout(new GridBagLayout());
        GridBagConstraints c;

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 5, 10);
        c.weightx = 1;
        add(new MultiLineLabel(pLabel), c);

        c = new GridBagConstraints();
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 10, 10);
        c.weighty = 1;
        add(component, c);
    }
}
