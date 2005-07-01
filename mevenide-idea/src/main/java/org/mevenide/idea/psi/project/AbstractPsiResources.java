package org.mevenide.idea.psi.project;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Arik
 */
public abstract class AbstractPsiResources<ResType extends AbstractPsiResourcePatterns> extends AbstractPsiBeanRowsObservable {

    private final Map<Integer, ResType> includesCache = Collections.synchronizedMap(
        new HashMap<Integer, ResType>(10));

    private final Map<Integer, ResType> excludesCache = Collections.synchronizedMap(
        new HashMap<Integer, ResType>(10));

    protected AbstractPsiResources(final XmlFile pXmlFile,
                                   final String pContainerTagPath) {
        this(pXmlFile, pContainerTagPath, "resource");
    }

    protected AbstractPsiResources(final XmlFile pXmlFile,
                                   final String pContainerTagPath,
                                   final String pRowTagName) {
        super(pXmlFile, pContainerTagPath, pRowTagName);
        registerTag("directory", "directory");
        registerTag("targetPath", "targetPath");
    }

    public String getDirectory(final int pRow) {
        return getValue(pRow, "directory");
    }

    public void setDirectory(final int pRow, final Object pValue) {
        setValue(pRow, "directory", pValue);
    }

    public String getTargetPath(final int pRow) {
        return getValue(pRow, "targetPath");
    }

    public void setTargetPath(final int pRow, final Object pValue) {
        setValue(pRow, "targetPath", pValue);
    }

    protected final ResType getPatterns(final int pRow,
                                                    final PatternType pType) {
        final Map<Integer, ResType> cache;
        if (pType == PatternType.INCLUDES)
            cache = includesCache;
        else if (pType == PatternType.EXCLUDES)
            cache = excludesCache;
        else
            throw new IllegalArgumentException("illegal type - " + pType);

        ResType props = cache.get(pRow);
        if (props == null) {
            props = createPsiResourcePatterns(pRow, pType);
            cache.put(pRow, props);
        }

        return props;
    }

    public final ResType getIncludes(final int pRow) {
        return getPatterns(pRow, PatternType.INCLUDES);
    }

    public final ResType getExcludes(final int pRow) {
        return getPatterns(pRow, PatternType.EXCLUDES);
    }

    protected abstract ResType createPsiResourcePatterns(final int pRow, final PatternType pType);
}
