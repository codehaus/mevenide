package org.mevenide.idea.psi.support;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;

/**
 * @author Arik
 */
public interface XmlPsiObject {
    XmlFile getXmlFile();

    VirtualFile getVirtualFile();
}
