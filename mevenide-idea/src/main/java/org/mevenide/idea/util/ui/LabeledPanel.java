package org.mevenide.idea.util.ui;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.DefaultFormBuilder;

import javax.swing.JPanel;
import javax.swing.JComponent;
import java.awt.BorderLayout;

/**
 * @author Arik
 */
public class LabeledPanel extends JPanel {

    public LabeledPanel(final String pTitle,
                        final String pLabel,
                        final JComponent pComponent) {
        init(pTitle, pLabel, pComponent);
    }

    public LabeledPanel(final boolean pDoubleBuffered,
                        final String pTitle,
                        final String pLabel,
                        final JComponent pComponent) {
        super(pDoubleBuffered);
        init(pTitle, pLabel, pComponent);
    }

    private void init(final String pTitle, final String pLabel, final JComponent pComponent) {
        final String columnSpecs = "fill:pref:grow";
        final String rowSpec = "fill:min:none, fill:min:none, fill:pref:grow(0.5)";
        final FormLayout layout = new FormLayout(columnSpecs, rowSpec);

        final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setDefaultDialogBorder();
        builder.setComponentFactory(new CustomFormsComponentFactory());

        //
        //add the dependency table
        //
        builder.appendSeparator(pTitle);
        builder.append(new MultiLineLabel(pLabel));
        builder.append(pComponent);

        //
        //set the panel
        //
        setLayout(new BorderLayout());
        add(builder.getPanel(), BorderLayout.CENTER);
    }
}
