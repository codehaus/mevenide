package org.mevenide.idea.psi.project;

import com.intellij.psi.xml.XmlFile;

/**
 * @author Arik
 */
public class PsiResources extends AbstractPsiResources<PsiResourcePatterns> {
    private static final String CONTAINER_TAG_PATH = "project/build/resources";

    public PsiResources(final XmlFile pXmlFile) {
        super(pXmlFile, CONTAINER_TAG_PATH);
    }

    protected PsiResourcePatterns createPsiResourcePatterns(final int pRow,
                                                            final PatternType pType) {
        return new PsiResourcePatterns(getXmlFile(), pRow, pType);
    }
}
