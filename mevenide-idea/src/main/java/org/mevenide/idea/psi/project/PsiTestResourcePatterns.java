package org.mevenide.idea.psi.project;

import com.intellij.psi.xml.XmlFile;

/**
 * @author Arik
 */
public class PsiTestResourcePatterns extends AbstractPsiResourcePatterns {

    public PsiTestResourcePatterns(final XmlFile pXmlFile,
                                   final int pResourceRow,
                                   final PatternType pPatternType) {
        super(pXmlFile,
              buildContainerPath(pResourceRow, pPatternType),
              pPatternType);
    }

    protected static String buildContainerPath(final int pRow,
                                               final PatternType pType) {
        final StringBuilder buf = new StringBuilder();
        buf.append("project/build/unitTest/resource[").append(pRow).append(']');

        if (pType == PatternType.INCLUDES)
            buf.append("/includes");
        else
            buf.append("/excludes");

        return buf.toString();
    }
}
