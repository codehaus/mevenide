package org.mevenide.idea.util.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.mevenide.idea.Res;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Displays a modifable list of patterns. The user can add or remove patterns from the list.
 *
 * @author Arik
 */
public class PatternsPanel extends JPanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(PatternsPanel.class);

    /**
     * The label for the pattern field.
     */
    private final JLabel patternLabel = new JLabel(RES.get("pattern.field.title"));

    /**
     * The field where the user enters the patterns.
     */
    private final JTextField patternField = new JTextField();

    /**
     * The patterns list model.
     */
    private final DefaultListModel patternsModel = new DefaultListModel();

    /**
     * The list containing the patterns.
     */
    private final JList patternList = new JList(patternsModel);

    /**
     * This button adds the currently entered pattern in the {@link #patternField} into the {@link
     * #patternsModel pattern list model}.
     */
    private final JButton addPatternButton = new JButton(RES.get("add.button.title"));

    /**
     * Removes the selected patterns from the patterns list.
     */
    private final JButton removePatternButton = new JButton(RES.get("remove.button.title"));

    /**
     * Creates a new instance with no preset patterns.
     */
    public PatternsPanel() {
        this((String[]) null);
    }

    /**
     * Creates a new instance with the specified preset patterns that will appear in the patterns
     * list.
     *
     * @param pPatterns the preset patterns
     */
    public PatternsPanel(final List<String> pPatterns) {
        this(pPatterns.toArray(new String[pPatterns.size()]));
    }

    /**
     * Creates a new instance with the specified preset patterns that will appear in the patterns
     * list.
     *
     * @param pPatterns the preset patterns
     */
    public PatternsPanel(final String[] pPatterns) {
        initModel(pPatterns);
        initComponents();
        layoutComponents();
    }

    /**
     * Initializes the model with the specified patterns.
     *
     * @param pPatterns the patterns to add to the model
     */
    private void initModel(final String[] pPatterns) {
        if (pPatterns != null)
            for (String pattern : pPatterns)
                patternsModel.addElement(pattern);
    }

    /**
     * Initializes the components by settings required properties and event handlers.
     */
    private void initComponents() {
        setPreferredSize(new Dimension(400, 400));

        //link the pattern label to the pattern field
        patternLabel.setLabelFor(patternField);

        //attach a document listener that will enable/disable components
        patternField.getDocument().addDocumentListener(new PatternDocumentListener());

        //initialy the add pattern button should be disabled, as the pattern
        //field is empty
        addPatternButton.setEnabled(false);

        //attach the add-pattern handler
        addPatternButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addPattern(patternField.getText());
            }
        });

        //attach the remove-patterns handler
        removePatternButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removePatterns();
            }
        });

        //attach a selection listener to enable/disable buttons
        patternList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                final int[] rows = patternList.getSelectedIndices();
                removePatternButton.setEnabled(rows.length > 0);
            }
        });
    }

    /**
     * Lays out the components inside this panel.
     */
    private void layoutComponents() {
        final String cols = "right:min, 2dlu, fill:pref:grow, 2dlu, fill:min";
        final String rows = "center:pref, 2dlu, fill:pref:grow";
        final FormLayout formLayout = new FormLayout(cols, rows);
        final DefaultFormBuilder builder = new DefaultFormBuilder(formLayout, this);
        builder.setComponentFactory(new CustomFormsComponentFactory());
        final CellConstraints cc = new CellConstraints();

        builder.add(patternLabel, cc.xy(1, 1));
        builder.add(patternField, cc.xy(3, 1));
        builder.add(addPatternButton, cc.xy(5, 1));
        builder.add(new JScrollPane(patternList), cc.xyw(1, 3, 3));
        builder.add(removePatternButton, cc.xy(5, 3, CellConstraints.FILL, CellConstraints.TOP));
    }

    /**
     * Adds the specified pattern to the patterns list.
     *
     * @param pPattern the pattern to add
     */
    private void addPattern(final String pPattern) {
        if (pPattern != null && pPattern.trim().length() > 0) {
            if (patternsModel.contains(pPattern))
                return;

            patternsModel.addElement(pPattern);
            patternField.setText(null);
            patternField.requestFocusInWindow();
        }
    }

    /**
     * Removes currently selected patterns in the list.
     */
    private void removePatterns() {
        final int[] rows = patternList.getSelectedIndices();
        final Object[] values = new Object[rows.length];
        for (int i = 0; i < rows.length; i++)
            values[i] = patternsModel.getElementAt(rows[i]);

        for (final Object value : values)
            patternsModel.removeElement(value);
    }

    /**
     * Returns the list of patterns selected by the user. These are the patterns that are still in
     * the pattern list.
     *
     * <p>If no patterns are present in the list, an empty array is returned.</p>
     *
     * @return string array
     */
    public String[] getPatterns() {
        final String[] items = new String[patternsModel.getSize()];
        for (int i = 0; i < items.length; i++)
            items[i] = (String) patternsModel.getElementAt(i);

        return items;
    }

    /**
     * Displays the patterns selection dialog, preloaded with the given patterns list.
     *
     * <p>If the user cancels the dialog (by clicking the "Cancel" button), {@code null} is
     * returned. Otherwise, an array of patterns is returned. If the user does not select any
     * patterns, an empty array is returned (to distinguish the case from when the user presses
     * "Cancel", in which case {@code null} is returned).</p>
     *
     * @param pProject  the project context
     * @param pTitle    the dialog title
     * @param pPatterns the patterns to initially display in the dialog
     * @return {@code null} if the user cancels the dialog, or a (possibly empty) array of strings
     */
    public static String[] showDialog(final Project pProject,
                                      final String pTitle,
                                      final String[] pPatterns) {
        final PatternsPanel patternsPanel = new PatternsPanel(pPatterns);

        final DialogBuilder builder = new DialogBuilder(pProject);
        builder.addOkAction();
        builder.addCancelAction();
        builder.setCenterPanel(patternsPanel);
        builder.setTitle(pTitle);

        final int exitCode = builder.show();
        if (exitCode == DialogWrapper.OK_EXIT_CODE)
            return patternsPanel.getPatterns();
        else
            return null;
    }

    /**
     * Listens to changes in the pattern field document, and enables/disables relevant fields in the
     * panel accordingly.
     */
    private class PatternDocumentListener implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            final String text = patternField.getText();
            addPatternButton.setEnabled(text != null && text.trim().length() > 0);
        }

        public void insertUpdate(DocumentEvent e) {
            final String text = patternField.getText();
            addPatternButton.setEnabled(text != null && text.trim().length() > 0);
        }

        public void removeUpdate(DocumentEvent e) {
            final String text = patternField.getText();
            addPatternButton.setEnabled(text != null && text.trim().length() > 0);
        }
    }
}
