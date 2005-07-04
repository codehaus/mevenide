package org.mevenide.idea.psi.project.support;

import com.intellij.psi.xml.XmlFile;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.mevenide.idea.psi.project.PatternType;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.PsiResourcePatterns;
import org.mevenide.idea.psi.project.PsiResources;
import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;

/**
 * @author Arik
 */
public abstract class AbstractPsiResources extends AbstractPsiBeanRowsObservable
        implements PsiResources {
    private final Map<Integer, PsiResourcePatterns> includesCache = Collections.synchronizedMap(
            new HashMap<Integer, PsiResourcePatterns>(10));

    private final Map<Integer, PsiResourcePatterns> excludesCache = Collections.synchronizedMap(
            new HashMap<Integer, PsiResourcePatterns>(10));
    private PsiProject project;

    protected AbstractPsiResources(final PsiProject pProject,
                                   final String pContainerTagPath) {
        this(pProject.getXmlFile(), pContainerTagPath, "resource");
        project = pProject;
    }

    public PsiProject getParent() {
        return project;
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

    public final PsiResourcePatterns getPatterns(final int pRow,
                                                 final PatternType pType) {
        final Map<Integer, PsiResourcePatterns> cache;
        if (pType == PatternType.INCLUDES)
            cache = includesCache;
        else if (pType == PatternType.EXCLUDES)
            cache = excludesCache;
        else
            throw new IllegalArgumentException("illegal type - " + pType);

        PsiResourcePatterns props = cache.get(pRow);
        if (props == null) {
            props = createPsiResourcePatterns(pRow, pType);
            cache.put(pRow, props);
        }

        return props;
    }

    public final PsiResourcePatterns getIncludes(final int pRow) {
        return getPatterns(pRow, PatternType.INCLUDES);
    }

    public final PsiResourcePatterns getExcludes(final int pRow) {
        return getPatterns(pRow, PatternType.EXCLUDES);
    }

    protected abstract PsiResourcePatterns createPsiResourcePatterns(final int pRow,
                                                                     final PatternType pType);
}
