package org.mevenide.idea.psi.project;

import com.intellij.psi.xml.XmlFile;

/**
 * @author Arik
 */
public class PsiTestResources extends AbstractPsiResources<PsiTestResourcePatterns> {
    private static final String CONTAINER_TAG_PATH = "project/build/unitTest";

    public PsiTestResources(final XmlFile pXmlFile) {
        super(pXmlFile, CONTAINER_TAG_PATH);
    }

    protected PsiTestResourcePatterns createPsiResourcePatterns(final int pRow,
                                                                final PatternType pType) {
        return new PsiTestResourcePatterns(getXmlFile(), pRow, pType);
    }
}
