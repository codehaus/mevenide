package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;
import com.intellij.psi.xml.XmlFile;

/**
 * @author Arik
 */
public abstract class AbstractPsiResourcePatterns extends AbstractPsiBeanRowsObservable {
    protected final PatternType type;

    public AbstractPsiResourcePatterns(final XmlFile pXmlFile,
                                       final String pContainerTagPath,
                                       final PatternType pType) {
        super(pXmlFile, pContainerTagPath, getRowTagNameForType(pType));
        type = pType;
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
