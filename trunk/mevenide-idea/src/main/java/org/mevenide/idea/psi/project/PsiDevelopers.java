package org.mevenide.idea.psi.project;

import com.intellij.psi.xml.XmlFile;

/**
 * @author Arik
 */
public class PsiDevelopers extends AbstractPsiTeamMembers {
    private static final String CONTAINER_TAG_PATH = "project/developers";
    private static final String ROW_TAG_NAME = "developer";

    public PsiDevelopers(final XmlFile pXmlFile) {
        super(pXmlFile, CONTAINER_TAG_PATH, ROW_TAG_NAME);
    }

}
