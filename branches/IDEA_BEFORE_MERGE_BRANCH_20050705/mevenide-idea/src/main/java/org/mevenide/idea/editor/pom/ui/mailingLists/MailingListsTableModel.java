package org.mevenide.idea.editor.pom.ui.mailingLists;

import org.mevenide.idea.psi.project.PsiMailingLists;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.util.AbstractBeanRowsTableModel;

/**
 * @author Arik
 */
public class MailingListsTableModel extends AbstractBeanRowsTableModel<PsiMailingLists> {
    private static final String[] COLUMN_TITLES = new String[]{
        "Name",
        "Subscribe",
        "Unsubscribe",
        "Archive"
    };

    public MailingListsTableModel(final PsiProject pModel) {
        this(pModel.getMailingLists());
    }

    public MailingListsTableModel(final PsiMailingLists pModel) {
        super(pModel, COLUMN_TITLES);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return model.getName(rowIndex);
            case 1:
                return model.getSubscribe(rowIndex);
            case 2:
                return model.getUnsubscribe(rowIndex);
            case 3:
                return model.getArchive(rowIndex);
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        final String value = aValue == null ? null : aValue.toString();
        switch (columnIndex) {
            case 0:
                model.setName(rowIndex, value);
                break;
            case 1:
                model.setSubscribe(rowIndex, value);
                break;
            case 2:
                model.setUnsubscribe(rowIndex, value);
                break;
            case 3:
                model.setArchive(rowIndex, value);
                break;
            default:
                throw new IllegalArgumentException("Illegal column number.");
        }
    }

    protected int getColumnIndexByProperty(final String pProperyName) {
        if ("name".equals(pProperyName))
            return 0;
        else if ("subscribe".equals(pProperyName))
            return 1;
        else if ("unsubscribe".equals(pProperyName))
            return 2;
        else if ("archive".equals(pProperyName))
            return 3;
        else
            return -1;
    }
}
