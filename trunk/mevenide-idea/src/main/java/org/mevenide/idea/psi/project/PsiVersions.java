package org.mevenide.idea.psi.project;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;

/**
 * @author Arik
 */
public class PsiVersions extends AbstractPsiBeanRowsObservable {
    public PsiVersions(final XmlFile pXmlFile) {
        super(pXmlFile, "project/versions", "version");
        registerTag("id", "id");
        registerTag("name", "name");
        registerTag("tag", "tag");
    }

    public String getId(final int pRow) {
        return getValue(pRow, "id");
    }

    public void setId(final int pRow, final String pId) {
        setValue(pRow, "id", pId);
    }

    public String getName(final int pRow) {
        return getValue(pRow, "name");
    }

    public void setName(final int pRow, final String pName) {
        setValue(pRow, "name", pName);
    }

    public String getTag(final int pRow) {
        return getValue(pRow, "tag");
    }

    public void setTag(final int pRow, final String pTag) {
        setValue(pRow, "tag", pTag);
    }
}
