package org.mevenide.idea.util.ui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * @author Arik
 */
public class LabeledPanel extends JPanel {

    public LabeledPanel(final String pLabel,
                        final JComponent pComponent) {
        init(pLabel, pComponent);
    }

    public LabeledPanel(final boolean pDoubleBuffered,
                        final String pLabel,
                        final JComponent pComponent) {
        super(pDoubleBuffered);
        init(pLabel, pComponent);
    }

    private void init(final String pLabel, final JComponent pComponent) {
        setLayout(new GridBagLayout());
        GridBagConstraints c;

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(15, 15, 5, 15);
        c.weightx = 1;
        add(new MultiLineLabel(pLabel), c);

        c = new GridBagConstraints();
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 15, 15, 15);
        c.weighty = 1;
        add(pComponent, c);
    }
}
