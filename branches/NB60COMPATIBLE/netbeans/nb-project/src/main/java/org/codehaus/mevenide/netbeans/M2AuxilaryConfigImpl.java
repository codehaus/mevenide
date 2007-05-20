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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.codehaus.plexus.util.StringOutputStream;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
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

    //copied from junit module..
    /** */
    private static final String JUNIT_NAMESPACE_URI
            = "http://www.netbeans.org/ns/junit/1"; //NOI18N
    /** */
    private static final String JUNIT_VERSION_ELEM_NAME = "junit-version"; //NOI18N
    /** */
    private static final String JUNIT_VERSION_ATTR_NAME = "value"; //NOI18N
    
    private static final String JUNIT3 = "junit3"; //NOI18N
    private static final String JUNIT4 = "junit4"; //NOI18N
    
    private static final String JUNIT_GR_ART = "junit"; //NOI18N

    
    private static final String AUX_CONFIG = "AuxilaryConfiguration"; //NOI18N
    private NbMavenProject project;
    
    /** Creates a new instance of M2AuxilaryConfigImpl */
    public M2AuxilaryConfigImpl(NbMavenProject proj) {
        this.project = proj;
    }
    
    public Element getConfigurationFragment(final String elementName, final String namespace, boolean shared) {
        if (shared) {
            //somewhat hack... generate what the junit module expects based on the pom dependency section.
            if (JUNIT_NAMESPACE_URI.equals(namespace) && JUNIT_VERSION_ELEM_NAME.equals(elementName)) {
                List<Artifact> arts = project.getOriginalMavenProject().getTestArtifacts();
                Artifact artifact = null;
                for (Artifact art : arts) {
                    if (JUNIT_GR_ART.equals(art.getGroupId()) && JUNIT_GR_ART.equals(art.getArtifactId())) {
                        artifact = art;
                        break;
                    }
                }
                if (artifact != null) {
                    String version = artifact.getVersion();
                    if (version.startsWith("3.")) {
                        return createJUnitElement(JUNIT3);
                    } 
                    else if (version.startsWith("4.")) {
                        return createJUnitElement(JUNIT4);
                    } else {
                        ErrorManager.getDefault().log("AuxilaryConfiguration: Unknown version of junit artifact found in project:" + version); //NOI18N
                    }
                }
                // do not print the error message now, just prent we'return just not set.
                return null;
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
            if (JUNIT_NAMESPACE_URI.equals(fragment.getNamespaceURI())) {
                //somewhat hack... update pom based on the junit module data.
                String attr = fragment.getAttribute(JUNIT_VERSION_ATTR_NAME);
                List<Artifact> arts = project.getOriginalMavenProject().getTestArtifacts();
                Artifact artifact = null;
                for (Artifact art : arts) {
                    if (JUNIT_GR_ART.equals(art.getGroupId()) && JUNIT_GR_ART.equals(art.getArtifactId())) {
                        artifact = art;
                        break;
                    }
                }
                if (artifact != null) {
                    String ver = artifact.getVersion();
                    if ((attr.equals(JUNIT4) && ver.startsWith("4.")) ||
                        (attr.equals(JUNIT3) && ver.startsWith("3."))) {
                        //don't update anything, somewhere the stuff is correctly set.
                        return;
                    }
                }
                FileObject fo = project.getProjectDirectory().getFileObject("pom.xml");
                Model model = WriterUtils.loadModel(fo); //NOI18N
                if (model != null) {
                    Dependency dep = PluginPropertyUtils.checkModelDependency(model,JUNIT_GR_ART,JUNIT_GR_ART, true);
                    dep.setVersion(attr.equals(JUNIT4) ? "4.1" : "3.8.2");
                    dep.setScope("test");
                    //TODO how to upgrade always to a version that is correct in terms of junit support?
                    try {
                        WriterUtils.writePomModel(fo, model);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }                
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
    
    private Element createJUnitElement(String version) {
        Element toRet = null;
        Document doc = createXmlDocument();
        if (doc != null) {
            toRet = doc.createElementNS(
                    JUNIT_NAMESPACE_URI,
                    JUNIT_VERSION_ELEM_NAME);
            toRet.setAttribute(JUNIT_VERSION_ATTR_NAME,
                    version);
        }
        return toRet;
    }
    
    /**
     * Creates a new DOM document.
     *
     * @return  created document, or {@code null} if the document
     *          could not be created
     */
    private Document createXmlDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            return factory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException ex) {
            return null;
        }
    }
}
