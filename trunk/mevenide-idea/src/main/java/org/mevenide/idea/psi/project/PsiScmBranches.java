package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;
import com.intellij.psi.xml.XmlFile;

/**
 * @author Arik
 */
public class PsiScmBranches extends AbstractPsiBeanRowsObservable {
    private static final String CONTAINER_TAG_PATH = "project/branches";
    private static final String ROW_TAG_NAME = "branch";

    public PsiScmBranches(final XmlFile pXmlFile) {
        super(pXmlFile, CONTAINER_TAG_PATH, ROW_TAG_NAME);
        registerTag("tag", "tag");
    }

    public final String getTag(final int pRow) {
        return getValue(pRow, "tag");
    }

    public final void setTag(final int pRow, final Object pValue) {
        setValue(pRow, "tag", pValue);
    }
}
