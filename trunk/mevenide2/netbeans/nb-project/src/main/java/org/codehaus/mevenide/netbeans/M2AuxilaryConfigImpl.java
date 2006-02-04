/* ==========================================================================
 * Copyright 2006 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */

package org.codehaus.mevenide.netbeans;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.codehaus.plexus.util.StringOutputStream;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.CacheDirectoryProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.Mutex;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * implementation of AuxiliaryConfiguration that relies on FileObject's attributes
 * @author mkleint
 */
public class M2AuxilaryConfigImpl implements AuxiliaryConfiguration {
    
    private NbMavenProject project;
    
    /** Creates a new instance of M2AuxilaryConfigImpl */
    public M2AuxilaryConfigImpl(NbMavenProject proj) {
        this.project = proj;
    }
    
    public Element getConfigurationFragment(final String elementName, final String namespace, boolean shared) {
        if (shared) {
            ErrorManager.getDefault().log("Maven2 support doesn't support shared custom configurations. Element was:" + elementName + " , namespace:" + namespace);
            return null;
        }
        return (Element)ProjectManager.mutex().readAccess(new Mutex.Action() {
            public Object run() {
                String str = (String)project.getProjectDirectory().getAttribute("AuxilaryConfiguration");
                if (str != null) {
                    Document doc;
                    try {
                        doc = XMLUtil.parse(new InputSource(new StringReader(str)), false, true, null, null);
                        return findElement(doc.getDocumentElement(), elementName, namespace);
                    } catch (SAXException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                return null;
            }});
    }
    
    public void putConfigurationFragment(final Element fragment, boolean shared) throws IllegalArgumentException {
        if (shared) {
            ErrorManager.getDefault().log("Maven2 support doesn't support shared custom configurations. Element was:" + fragment.getNodeName());
        }
        ProjectManager.mutex().writeAccess(new Mutex.Action() {
            public Object run() {
                String str = (String)project.getProjectDirectory().getAttribute("AuxilaryConfiguration");
                Document doc = null;
                if (str != null) {
                    try {
                        doc = XMLUtil.parse(new InputSource(new StringReader(str)), false, true, null, null);
                    } catch (SAXException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    String element = "project-private"; // NOI18N
                    doc = XMLUtil.createDocument(element, null, null, null);
                }
                if (doc != null) {
                    Element el = findElement(doc.getDocumentElement(), fragment.getNodeName(), fragment.getNamespaceURI());
                    if (el != null) {
                        doc.getDocumentElement().removeChild(el);
                    }
                    doc.getDocumentElement().appendChild(doc.importNode(fragment, true));
                }
                
                try {
                    StringOutputStream wr = new StringOutputStream();
                    XMLUtil.write(doc, wr, "UTF-8");
                    project.getProjectDirectory().setAttribute("AuxilaryConfiguration", wr.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        });
        
    }
    
    public boolean removeConfigurationFragment(final String elementName, final String namespace, boolean shared) throws IllegalArgumentException {
        if (shared) {
            ErrorManager.getDefault().log("Maven2 support doesn't support shared custom configurations. Element was:" + elementName + " , namespace:" + namespace);
            return false;
        }
        return ((Boolean)ProjectManager.mutex().writeAccess(new Mutex.Action() {
            public Object run() {
                String str = (String)project.getProjectDirectory().getAttribute("AuxilaryConfiguration");
                Document doc = null;
                if (str != null) {
                    try {
                        doc = XMLUtil.parse(new InputSource(new StringReader(str)), false, true, null, null);
                    } catch (SAXException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    return Boolean.FALSE;
                }
                if (doc != null) {
                    Element el = findElement(doc.getDocumentElement(), elementName, namespace);
                    if (el != null) {
                        doc.getDocumentElement().removeChild(el);
                    }
                }
                try {
                    StringOutputStream wr = new StringOutputStream();
                    XMLUtil.write(doc, wr, "UTF-8");
                    project.getProjectDirectory().setAttribute("AuxilaryConfiguration", wr.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return Boolean.TRUE;
            }
        })).booleanValue();
    }
    
    
    private static Element findElement(Element parent, String name, String namespace) {
        Element result = null;
        NodeList l = parent.getChildNodes();
        int len = l.getLength();
        for (int i = 0; i < len; i++) {
            if (l.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element)l.item(i);
                if (name.equals(el.getLocalName()) && namespace.equals(el.getNamespaceURI())) {
                    if (result == null) {
                        result = el;
                    } else {
                        return null;
                    }
                }
            }
        }
        return result;
    }
    
    private static final DocumentBuilder db;
    static {
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
    }
    private static Element cloneSafely(Element el) {
        // Using XMLUtil.createDocument is much too slow.
        synchronized (db) {
            Document dummy = db.newDocument();
            return (Element) dummy.importNode(el, true);
        }
        
    }

    
}
