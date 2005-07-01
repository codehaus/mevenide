package org.mevenide.idea.psi.project;

import com.intellij.psi.xml.XmlFile;
import org.apache.commons.lang.ArrayUtils;
import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;

/**
 * @author Arik
 */
public class PsiReports extends AbstractPsiBeanRowsObservable {
    private static final String CONTAINER_TAG_PATH = "project/reports";
    private static final String ROW_TAG_NAME = "report";

    public PsiReports(final XmlFile pXmlFile) {
        super(pXmlFile, CONTAINER_TAG_PATH, ROW_TAG_NAME);

/*
        String[] reports;
        try {
            final VirtualFile virtualFile = xmlFile.getVirtualFile();
            final Project project = xmlFile.getProject();
            final Module module = VfsUtil.getModuleForFile(project, virtualFile);
            final ILocationFinder finder = new ModuleLocationFinder(module);
            final IReportsFinder reportsFinder = new JDomReportsFinder(finder);
            reports = reportsFinder.findReports();
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            reports = new String[0];
        }
*/
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
