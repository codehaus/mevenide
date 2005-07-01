package org.mevenide.idea.psi.support;

import com.intellij.psi.xml.XmlFile;

/**
 * An abstract base class for PSI support classes.
 *
 * @author Arik
 */
public abstract class AbstractPsiObject {
    /**
     * The XML file.
     */
    protected final XmlFile xmlFile;

    /**
     * Creates an instance, saving a reference to the given XML file.
     *
     * @param pXmlFile the XML file of this object
     */
    protected AbstractPsiObject(final XmlFile pXmlFile) {
        xmlFile = pXmlFile;
    }

    /**
     * Returns this object's XML file.
     *
     * @return the XML file
     */
    public final XmlFile getXmlFile() {
        return xmlFile;
    }
}
