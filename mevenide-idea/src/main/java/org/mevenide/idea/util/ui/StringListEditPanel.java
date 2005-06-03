package org.mevenide.idea.util.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ScrollPaneFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.mevenide.idea.Res;

/**
 * Displays a modifable list of strings. The user can add or remove strings from the list.
 *
 * @author Arik
 */
public class StringListEditPanel extends JPanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(StringListEditPanel.class);

    /**
     * The label for the item field.
     */
    private final JLabel itemLabel = new JLabel();

    /**
     * The field where the user enters the strings.
     */
    private final JTextField itemField = new JTextField();

    /**
     * The strings list model.
     */
    private final DefaultListModel model = new DefaultListModel();

    /**
     * The list containing the strings.
     */
    private final JList list = new JList(model);

    /**
     * This button adds the currently entered string in the {@link #itemField} into the {@link
     * #model string list model}.
     */
    private final JButton addButton = new JButton(RES.get("add.button.title"));

    /**
     * Removes the selected strings from the strings list.
     */
    private final JButton removeButton = new JButton(RES.get("remove.button.title"));

    /**
     * Creates a new instance with no preset strings.
     */
    public StringListEditPanel(final String pItemLabel) {
        this(pItemLabel, (String[]) null);
    }

    /**
     * Creates a new instance with the specified preset strings that will appear in the strings
     * list.
     *
     * @param pItems the preset strings
     */
    public StringListEditPanel(final String pItemLabel, final List<String> pItems) {
        this(pItemLabel, pItems.toArray(new String[pItems.size()]));
    }

    /**
     * Creates a new instance with the specified preset strings that will appear in the strings
     * list.
     *
     * @param pItems the preset strings
     */
    public StringListEditPanel(final String pItemLabel, final String[] pItems) {
        itemLabel.setText(pItemLabel);
        initModel(pItems);
        initComponents();
        layoutComponents();
    }

    /**
     * Initializes the model with the specified strings.
     *
     * @param pItems the strings to add to the model
     */
    private void initModel(final String[] pItems) {
        if (pItems != null)
            for (String string : pItems)
                model.addElement(string);
    }

    /**
     * Initializes the components by settings required properties and event handlers.
     */
    private void initComponents() {
        setPreferredSize(new Dimension(400, 400));

        //link the string label to the string field
        itemLabel.setLabelFor(itemField);

        //attach a document listener that will enable/disable components
        itemField.getDocument().addDocumentListener(new ItemDocumentListener());

        //initialy the add string button should be disabled, as the string
        //field is empty
        addButton.setEnabled(false);

        //attach the add-string handler
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addItem(itemField.getText());
            }
        });

        //attach the remove-strings handler
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeItems();
            }
        });

        //attach a selection listener to enable/disable buttons
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                final int[] rows = list.getSelectedIndices();
                removeButton.setEnabled(rows.length > 0);
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

        builder.add(itemLabel, cc.xy(1, 1));
        builder.add(itemField, cc.xy(3, 1));
        builder.add(addButton, cc.xy(5, 1));
        builder.add(ScrollPaneFactory.createScrollPane(list), cc.xyw(1, 3, 3));
        builder.add(removeButton, cc.xy(5, 3, CellConstraints.FILL, CellConstraints.TOP));
    }

    /**
     * Adds the specified item to the items list.
     *
     * @param pItem the item to add
     */
    private void addItem(final String pItem) {
        if (pItem != null && pItem.trim().length() > 0) {
            if (model.contains(pItem))
                return;

            model.addElement(pItem);
            itemField.setText(null);
            itemField.requestFocusInWindow();
        }
    }

    /**
     * Removes currently selected items in the list.
     */
    private void removeItems() {
        final int[] rows = list.getSelectedIndices();
        final Object[] values = new Object[rows.length];
        for (int i = 0; i < rows.length; i++)
            values[i] = model.getElementAt(rows[i]);

        for (final Object value : values)
            model.removeElement(value);
    }

    /**
     * Returns the list of items selected by the user. These are the items that are still in
     * the item list.
     *
     * <p>If no items are present in the list, an empty array is returned.</p>
     *
     * @return string array
     */
    public String[] getItems() {
        final String[] items = new String[model.getSize()];
        for (int i = 0; i < items.length; i++)
            items[i] = (String) model.getElementAt(i);

        return items;
    }

    /**
     * Displays the items selection dialog, preloaded with the given items list.
     *
     * <p>If the user cancels the dialog (by clicking the "Cancel" button), {@code null} is
     * returned. Otherwise, an array of items is returned. If the user does not select any
     * items, an empty array is returned (to distinguish the case from when the user presses
     * "Cancel", in which case {@code null} is returned).</p>
     *
     * @param pProject  the project context
     * @param pTitle    the dialog title
     * @param pItems the items to initially display in the dialog
     * @return {@code null} if the user cancels the dialog, or a (possibly empty) array of strings
     */
    public static String[] showDialog(final Project pProject,
                                      final String pTitle,
                                      final String pItemLabel,
                                      final String[] pItems) {
        final StringListEditPanel itemsPanel = new StringListEditPanel(pItemLabel, pItems);

        final DialogBuilder builder = new DialogBuilder(pProject);
        builder.addOkAction();
        builder.addCancelAction();
        builder.setCenterPanel(itemsPanel);
        builder.setTitle(pTitle);

        final int exitCode = builder.show();
        if (exitCode == DialogWrapper.OK_EXIT_CODE)
            return itemsPanel.getItems();
        else
            return null;
    }

    /**
     * Listens to changes in the item field document, and enables/disables relevant fields in the
     * panel accordingly.
     */
    private class ItemDocumentListener implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            final String text = itemField.getText();
            addButton.setEnabled(text != null && text.trim().length() > 0);
        }

        public void insertUpdate(DocumentEvent e) {
            final String text = itemField.getText();
            addButton.setEnabled(text != null && text.trim().length() > 0);
        }

        public void removeUpdate(DocumentEvent e) {
            final String text = itemField.getText();
            addButton.setEnabled(text != null && text.trim().length() > 0);
        }
    }
}
