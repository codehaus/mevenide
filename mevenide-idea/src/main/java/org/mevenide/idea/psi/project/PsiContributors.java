package org.mevenide.idea.psi.project;

import com.intellij.psi.xml.XmlFile;

/**
 * @author Arik
 */
public class PsiContributors extends AbstractPsiTeamMembers {
    private static final String CONTAINER_TAG_PATH = "project/contributors";
    private static final String ROW_TAG_NAME = "contributor";

    public PsiContributors(final XmlFile pXmlFile) {
        super(pXmlFile, CONTAINER_TAG_PATH, ROW_TAG_NAME);
    }
}
