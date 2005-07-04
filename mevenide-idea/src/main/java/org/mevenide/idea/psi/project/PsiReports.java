package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.XmlPsiObject;
import org.mevenide.idea.util.event.BeanRowsObservable;

/**
 * @author Arik
 */
public interface PsiReports extends BeanRowsObservable, XmlPsiObject, PsiChild<PsiProject> {
    String getReport(int pRow);

    void setReport(int pRow, Object pValue);

    String[] getReports();

    boolean isReportRegistered(String pReportName);
}
