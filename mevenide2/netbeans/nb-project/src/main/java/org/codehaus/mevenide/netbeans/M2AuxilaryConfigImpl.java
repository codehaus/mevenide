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
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.util.StringOutputStream;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.ErrorManager;
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

    private static final String AUX_CONFIG = "AuxilaryConfiguration"; //NOI18N
    private NbMavenProject project;
    
    /** Creates a new instance of M2AuxilaryConfigImpl */
    public M2AuxilaryConfigImpl(NbMavenProject proj) {
        this.project = proj;
    }
    
    public Element getConfigurationFragment(final String elementName, final String namespace, boolean shared) {
        if (shared) {
            if (namespace.equals("http://www.sun.com/creator/ns")) {
                
                return getMockCreatorElement();
            }
            ErrorManager.getDefault().log("Maven2 support doesn't support shared custom configurations. Element was:" + elementName + " , namespace:" + namespace); //NOI18N
            return null;
        }
        return (Element)ProjectManager.mutex().readAccess(new Mutex.Action() {
            public Object run() {
                String str = (String)project.getProjectDirectory().getAttribute(AUX_CONFIG);
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
            if (fragment.getNamespaceURI().equals("http://www.sun.com/creator/ns")) {
                return;
            }
            ErrorManager.getDefault().log("Maven2 support doesn't support shared custom configurations. Element was:" + fragment.getNodeName()); //NOI18N
            return;
        }
        ProjectManager.mutex().writeAccess(new Mutex.Action() {
            public Object run() {
                String str = (String)project.getProjectDirectory().getAttribute(AUX_CONFIG);
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
                    XMLUtil.write(doc, wr, "UTF-8"); //NOI18N
                    project.getProjectDirectory().setAttribute(AUX_CONFIG, wr.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        });
        
    }
    
    public boolean removeConfigurationFragment(final String elementName, final String namespace, boolean shared) throws IllegalArgumentException {
        if (shared) {
            if (namespace.equals("http://www.sun.com/creator/ns")) {
                return true;
            }
            ErrorManager.getDefault().log("Maven2 support doesn't support shared custom configurations. Element was:" + elementName + " , namespace:" + namespace); //NOI18N
            return false;
        }
        return ((Boolean)ProjectManager.mutex().writeAccess(new Mutex.Action() {
            public Object run() {
                String str = (String)project.getProjectDirectory().getAttribute(AUX_CONFIG);
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
                    XMLUtil.write(doc, wr, "UTF-8"); //NOI18N
                    project.getProjectDirectory().setAttribute(AUX_CONFIG, wr.toString());
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

    //TODO major hack!
    private Element getMockCreatorElement() {
        List<Artifact> artifacts = project.getOriginalMavenProject().getCompileArtifacts();
        boolean create = false;
        //TODO a hack to return the element only conditionally when the web extension was used.
        //#123599
        for (Artifact art : artifacts) {
            String artId = art.getArtifactId();
            String grId = art.getGroupId();
            if (artId.contains("woodstock")) { //NOI18N
                create = true;
            }
            if ("webui".equals(artId)) {
                create = true;
            }
        }
        if (!create) {
            return null;
        }
        Document doc = XMLUtil.createDocument("creator-data", "http://www.sun.com/creator/ns", null, null);
        Element el = doc.getDocumentElement();
        el.setAttribute("jsf.current.theme", "woodstock-theme-default");
        el.setAttribute("jsf.pagebean.package", project.getOriginalMavenProject().getGroupId());
        el.setAttribute("jsf.project.libraries.dir", "lib");
        el.setAttribute("jsf.project.version", "4.0");
        el.setAttribute("jsf.startPage", "Page1.jsp");
        return el;
    }
}
