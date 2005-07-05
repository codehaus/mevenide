package org.mevenide.idea.psi.support;

import com.intellij.psi.xml.XmlFile;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Arik
 */
public interface XmlPsiObject {
    XmlFile getXmlFile();

    VirtualFile getVirtualFile();
}
