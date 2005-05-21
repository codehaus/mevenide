package org.mevenide.idea.util.ui.table;

import com.jgoodies.forms.builder.ButtonStackBuilder;
import org.mevenide.idea.Res;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arik
 */
public class CRUDPanel extends JPanel {
    private static final Res RES = Res.getInstance(CRUDPanel.class);

    private final JButton addButton = new JButton(RES.get("add.button.title"));
    private final JButton removeButton = new JButton(RES.get("remove.button.title"));
    private final JButton editButton = new JButton(RES.get("edit.button.title"));

    private final boolean showAddButton;
    private final boolean showEditButton;
    private final boolean showRemoveButton;

    private ActionListener addAction;
    private ActionListener editAction;
    private ActionListener removeAction;

    private final JComponent component;
    private final ButtonStackBuilder buttonsBar = new ButtonStackBuilder();

    public CRUDPanel(final JComponent pComponent) {
        this(pComponent, true, true, true);
    }

    public CRUDPanel(final JComponent pComponent,
                     final boolean pShowAddButton,
                     final boolean pShowEditButton,
                     final boolean pShowRemoveButton) {
        component = pComponent;
        showAddButton = pShowAddButton;
        showEditButton = pShowEditButton;
        showRemoveButton = pShowRemoveButton;

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        if(component == null)
            throw new NullPointerException(RES.get("null.arg", "pComponent"));
        buttonsBar.addButtons(getButtons());
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c;

        c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 5);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        add(component, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.insets = new Insets(0, 5, 0, 0);
        add(buttonsBar.getPanel(), c);
    }

    private JButton[] getButtons() {
        final List<JButton> buttons = new ArrayList<JButton>(3);
        if(showAddButton)
            buttons.add(addButton);
        if(showEditButton)
            buttons.add(editButton);
        if(showRemoveButton)
            buttons.add(removeButton);

        return buttons.toArray(new JButton[buttons.size()]);
    }

    public JComponent getComponent() {
        return component;
    }

    public ActionListener getAddAction() {
        return addAction;
    }

    public void setAddAction(final ActionListener pAddAction) {
        if(addAction != null)
            addButton.removeActionListener(addAction);

        addAction = pAddAction;
        addButton.addActionListener(addAction);
    }

    public ActionListener getEditAction() {
        return editAction;
    }

    public void setEditAction(final ActionListener pEditAction) {
        if(editAction != null)
            editButton.removeActionListener(editAction);

        editAction = pEditAction;
        editButton.addActionListener(editAction);
    }

    public ActionListener getRemoveAction() {
        return removeAction;
    }

    public void setRemoveAction(final ActionListener pRemoveAction) {
        if(removeAction != null)
            removeButton.removeActionListener(removeAction);

        removeAction = pRemoveAction;
        removeButton.addActionListener(removeAction);
    }
}
