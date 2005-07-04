package org.mevenide.idea.psi.project.support;

import org.mevenide.idea.psi.project.PatternType;
import org.mevenide.idea.psi.project.PsiResourcePatterns;
import org.mevenide.idea.psi.project.PsiResources;
import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;

/**
 * @author Arik
 */
public abstract class AbstractPsiResourcePatterns extends AbstractPsiBeanRowsObservable implements
                                                                                        PsiResourcePatterns {
    protected final PatternType type;
    private final PsiResources resources;

    public AbstractPsiResourcePatterns(final PsiResources pResources,
                                       final String pContainerTagPath,
                                       final PatternType pType) {
        super(pResources.getXmlFile(), pContainerTagPath, getRowTagNameForType(pType));
        resources = pResources;
        type = pType;
    }

    public PsiResources getParent() {
        return resources;
    }

    public final String getPattern(final int pRow) {
        return getValue(pRow);
    }

    public final void setPattern(final int pRow, final Object pValue) {
        setValue(pRow, pValue);
    }

    public final String[] getPatterns() {
        return getValues();
    }

    public PatternType getType() {
        return type;
    }

    protected static String getRowTagNameForType(final PatternType pPatternType) {
        if (pPatternType == PatternType.INCLUDES)
            return "include";
        else
            return "exclude";
    }
}
