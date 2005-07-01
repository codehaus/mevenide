package org.mevenide.idea.psi.project;

import com.intellij.psi.xml.XmlFile;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;

/**
 * @author Arik
 */
public class PsiDependencies extends AbstractPsiBeanRowsObservable {
    private static final String CONTAINER_TAG_PATH = "project/dependencies";
    private static final String ROW_TAG_NAME = "dependency";

    private final Map<Integer, PsiDependencyProperties> propsCache = Collections.synchronizedMap(
        new HashMap<Integer, PsiDependencyProperties>(10));

    public PsiDependencies(final XmlFile pXmlFile) {
        super(pXmlFile, CONTAINER_TAG_PATH, ROW_TAG_NAME);
        registerTag("groupId", "groupId");
        registerTag("artifactId", "artifactId");
        registerTag("version", "version");
        registerTag("type", "type");
        registerTag("url", "url");
    }

    public final String getGroupId(final int pRow) {
        return getValue(pRow, "groupId");
    }

    public void setGroupId(final int pRow, final String pGroupId) {
        setValue(pRow, "groupId", pGroupId);
    }

    public final String getArtifactId(final int pRow) {
        return getValue(pRow, "artifactId");
    }

    public void setArtifactId(final int pRow, final String pArtifactId) {
        setValue(pRow, "artifactId", pArtifactId);
    }

    public final String getVersion(final int pRow) {
        return getValue(pRow, "version");
    }

    public void setVersion(final int pRow, final String pVersion) {
        setValue(pRow, "version", pVersion);
    }

    public final String getType(final int pRow) {
        return getValue(pRow, "type");
    }

    public void setType(final int pRow, final String pType) {
        setValue(pRow, "type", pType);
    }

    public final String getUrl(final int pRow) {
        return getValue(pRow, "url");
    }

    public void setUrl(final int pRow, final String pUrl) {
        setValue(pRow, "url", pUrl);
    }

    public final PsiDependencyProperties getProperties(final int pRow) {
        PsiDependencyProperties props = propsCache.get(pRow);
        if (props == null) {
            props = new PsiDependencyProperties(getXmlFile(), pRow);
            propsCache.put(pRow, props);
        }

        return props;
    }
}
