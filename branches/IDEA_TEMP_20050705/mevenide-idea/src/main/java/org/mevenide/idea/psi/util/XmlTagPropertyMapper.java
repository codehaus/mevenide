package org.mevenide.idea.psi.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

/**
 * @author Arik
 */
public class XmlTagPropertyMapper {
    /**
     * The registered tag paths instances.
     */
    private final Map<String, XmlTagPath> tags = new HashMap<String, XmlTagPath>(15);

    /**
     * Returns the registered property name for the given tag path.
     *
     * @param pPath the path to search for
     *
     * @return the property name, or {@code null} if no tag has the given path
     */
    public String findPropertyByPath(final String[] pPath) {
        return findPropertyByPath(StringUtils.join(pPath, "/"));
    }

    /**
     * Returns the registered property name for the given tag path.
     *
     * @param pPath the path to search for
     *
     * @return the property name, or {@code null} if no tag has the given path
     */
    public String findPropertyByPath(final String pPath) {
        for (Map.Entry<String, XmlTagPath> entry : tags.entrySet()) {
            final String property = entry.getKey();
            final XmlTagPath path = entry.getValue();
            if (path.getPath().equals(pPath))
                return property;
        }

        return null;
    }

    /**
     * Returns the property name of the tag path that is the ancestor of the given PSI
     * element.
     *
     * <p>If the given PSI element is not a child of any of the registered tag paths, this
     * method will return {@code null}.</p>
     *
     * @param pTextElt the PSI element to test
     *
     * @return property name, or {@code null}
     */
    public String findPropertyForElement(final PsiElement pTextElt) {
        for (Map.Entry<String, XmlTagPath> entry : tags.entrySet()) {
            final String property = entry.getKey();
            final XmlTagPath path = entry.getValue();
            final XmlTag tag = path.getTag();
            if (PsiTreeUtil.isAncestor(tag, pTextElt, false))
                return property;
        }

        return null;
    }

    /**
     * Registers the given property name with the specified tag path.
     *
     * @param pPropertyName the property name
     * @param pTagPath      the corresponding tag path
     */
    public XmlTagPath putTagPath(final String pPropertyName, final XmlTagPath pTagPath) {
        return tags.put(pPropertyName, pTagPath);
    }

    /**
     * Returns the tag path associated with the given property name.
     *
     * @param pPropertyName the property to get the tag path for
     *
     * @return tag path, or {@code null} if no such property is registered
     */
    public XmlTagPath getTagPath(final String pPropertyName) {
        return tags.get(pPropertyName);
    }

    /**
     * Returns registered property names.
     *
     * @return set (never {@code null})
     */
    public Set<String> getPropertyNames() {
        return tags.keySet();
    }
}
