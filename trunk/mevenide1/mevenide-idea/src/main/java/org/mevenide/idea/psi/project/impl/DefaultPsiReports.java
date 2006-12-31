package org.mevenide.idea.psi.project.impl;

import org.apache.commons.lang.ArrayUtils;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.PsiReports;
import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;

/**
 * @author Arik
 */
public class DefaultPsiReports extends AbstractPsiBeanRowsObservable implements PsiReports {
    private static final String CONTAINER_TAG_PATH = "project/reports";
    private static final String ROW_TAG_NAME = "report";

    private final PsiProject project;

    public DefaultPsiReports(final PsiProject pProject) {
        super(pProject.getXmlFile(), CONTAINER_TAG_PATH, ROW_TAG_NAME);
        project = pProject;
    }

    public PsiProject getParent() {
        return project;
    }

    public final String getReport(final int pRow) {
        return getValue(pRow);
    }

    public final void setReport(final int pRow, final Object pValue) {
        setValue(pRow, pValue);
    }

    public final String[] getReports() {
        return getValues();
    }

    public final boolean isReportRegistered(final String pReportName) {
        final String[] reports = getReports();
        return ArrayUtils.contains(reports, pReportName);
    }
}
