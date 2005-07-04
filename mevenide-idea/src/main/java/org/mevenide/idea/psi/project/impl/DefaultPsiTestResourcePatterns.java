package org.mevenide.idea.psi.project.impl;

import org.mevenide.idea.psi.project.PatternType;
import org.mevenide.idea.psi.project.PsiResources;
import org.mevenide.idea.psi.project.support.AbstractPsiResourcePatterns;

/**
 * @author Arik
 */
public class DefaultPsiTestResourcePatterns extends AbstractPsiResourcePatterns {
    public DefaultPsiTestResourcePatterns(final PsiResources pResources,
                                          final int pResourceRow,
                                          final PatternType pPatternType) {
        super(pResources,
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
