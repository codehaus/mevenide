package org.mevenide.idea.psi.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlFile;

/**
 * @author Arik
 */
public class PsiNamedPropertyChangeListener extends PsiPropertyChangeListener {
    /**
     * Maps properties to XML tag paths.
     */
    private final XmlTagPropertyMapper mapper = new XmlTagPropertyMapper();

    /**
     * A convenience prefix to use when registering new tags.
     */
    private final String tagPrefix;

    public PsiNamedPropertyChangeListener() {
        this(null);
    }

    /**
     * Creates a new instance with the given tag prefix - all registered tag paths will
     * start with that prefix (convenience).
     *
     * @param pTagPrefix the prefix to use - may be {@code null} or empty string.
     */
    public PsiNamedPropertyChangeListener(final String pTagPrefix) {
        if (pTagPrefix == null)
            tagPrefix = "";
        else if (pTagPrefix.endsWith("/"))
            tagPrefix = pTagPrefix;
        else
            tagPrefix = pTagPrefix + "/";
    }

    /**
     * Registers the given property name with the specified tag path.
     *
     * @param pPropertyName the property name
     * @param pTagPath      the corresponding tag path
     */
    public final void registerTag(final String pPropertyName,
                                  final XmlFile pFile,
                                  final String pTagPath) {
        final String path = tagPrefix + pTagPath;
        mapper.putTagPath(pPropertyName, new XmlTagPath(pFile, path));
    }

    protected String getPropertyForElement(final PsiElement pElement) {
        return mapper.findPropertyForElement(pElement);
    }

    protected String getPropertyForPath(final String[] pPath) {
        return mapper.findPropertyByPath(pPath);
    }

    protected XmlTagPath getPropertyTagPath(final String pProperty) {
        return mapper.getTagPath(pProperty);
    }
}
