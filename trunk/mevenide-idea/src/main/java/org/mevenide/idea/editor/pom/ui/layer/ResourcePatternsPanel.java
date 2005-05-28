package org.mevenide.idea.editor.pom.ui.layer;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

/**
 * @author Arik
 */
public class ResourcePatternsPanel extends JPanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(ResourcePatternsPanel.class);

    private final JTextField patternField = new JTextField();
    private final JList patternList = new JList();
    private final JButton addPatternButton = new JButton(RES.get("add.button.title"));
    private final JButton removePatternButton = new JButton(RES.get("remove.button.title"));

    public ResourcePatternsPanel() {
        this(null);
    }

    public ResourcePatternsPanel(final String[] pPatterns) {
        setPreferredSize(new Dimension(400, 400));
        final String cols = "right:min, 2dlu, fill:pref:grow, 2dlu, fill:min";
        final String rows = "center:pref, 2dlu, fill:pref:grow";
        final FormLayout formLayout = new FormLayout(cols, rows);
        final DefaultFormBuilder builder = new DefaultFormBuilder(formLayout, this);
        builder.setComponentFactory(new CustomFormsComponentFactory());
        final CellConstraints cc = new CellConstraints();

        builder.add(new JLabel("Pattern:"), cc.xy(1, 1));
        builder.add(patternField, cc.xy(3, 1));
        builder.add(addPatternButton, cc.xy(5, 1));
        builder.add(new JScrollPane(patternList), cc.xyw(1, 3, 3));
        builder.add(removePatternButton, cc.xy(5, 3, CellConstraints.FILL, CellConstraints.TOP));

        final DefaultListModel model = new DefaultListModel();
        if(pPatterns != null)
            for(String pattern : pPatterns)
                model.addElement(pattern);

        patternList.setModel(model);

        patternField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                final String text = patternField.getText();
                addPatternButton.setEnabled(text != null && text.trim().length() > 0);
            }

            public void removeUpdate(DocumentEvent e) {
                final String text = patternField.getText();
                addPatternButton.setEnabled(text != null && text.trim().length() > 0);
            }

            public void changedUpdate(DocumentEvent e) {
                final String text = patternField.getText();
                addPatternButton.setEnabled(text != null && text.trim().length() > 0);
            }
        });

        addPatternButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String text = patternField.getText();
                if(text != null && text.trim().length() > 0) {
                    DefaultListModel model = (DefaultListModel) patternList.getModel();
                    if(model.contains(text))
                        return;

                    model.addElement(text);
                    patternField.setText(null);
                }
            }
        });

        removePatternButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultListModel model = (DefaultListModel) patternList.getModel();
                final int[] rows = patternList.getSelectedIndices();
                final Object[] values = new Object[rows.length];
                for(int i = 0; i < rows.length; i++)
                    values[i] = model.getElementAt(rows[i]);

                for(Object value : values)
                    model.removeElement(value);
            }
        });

        patternList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                final int[] rows = patternList.getSelectedIndices();
                removePatternButton.setEnabled(rows.length > 0);
            }
        });
    }

    public String[] getSelectedPatterns() {
        final ListModel model = patternList.getModel();
        final String[] items = new String[model.getSize()];
        for(int i = 0; i < items.length; i++)
            items[i] = (String) model.getElementAt(i);

        return items;
    }
}
