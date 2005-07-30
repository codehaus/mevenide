package org.mevenide.idea.psi.project.impl;

import org.mevenide.idea.psi.project.PsiMailingLists;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;

/**
 * @author Arik
 */
public class DefaultPsiMailingLists extends AbstractPsiBeanRowsObservable
        implements PsiMailingLists {
    private static final String CONTAINER_TAG_PATH = "project/mailingLists";
    private static final String ROW_TAG_NAME = "mailingList";

    private final PsiProject project;

    public DefaultPsiMailingLists(final PsiProject pProject) {
        super(pProject.getXmlFile(), CONTAINER_TAG_PATH, ROW_TAG_NAME);
        project = pProject;
        registerTag("name", "name");
        registerTag("subscribe", "subscribe");
        registerTag("unsubscribe", "unsubscribe");
        registerTag("archive", "archive");
    }

    public PsiProject getParent() {
        return project;
    }

    public final String getName(final int pRow) {
        return getValue(pRow, "name");
    }

    public void setName(final int pRow, final String pName) {
        setValue(pRow, "name", pName);
    }

    public final String getSubscribe(final int pRow) {
        return getValue(pRow, "subscribe");
    }

    public void setSubscribe(final int pRow, final String pSubscribe) {
        setValue(pRow, "subscribe", pSubscribe);
    }

    public final String getUnsubscribe(final int pRow) {
        return getValue(pRow, "unsubscribe");
    }

    public void setUnsubscribe(final int pRow, final String pUnsubscribe) {
        setValue(pRow, "unsubscribe", pUnsubscribe);
    }

    public final String getArchive(final int pRow) {
        return getValue(pRow, "archive");
    }

    public void setArchive(final int pRow, final String pArchive) {
        setValue(pRow, "archive", pArchive);
    }
}
