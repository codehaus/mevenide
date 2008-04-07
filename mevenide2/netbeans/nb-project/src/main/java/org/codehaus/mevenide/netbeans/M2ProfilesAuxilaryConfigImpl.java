/* ==========================================================================
 * Copyright 2008 Mevenide Team
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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import org.codehaus.plexus.util.StringOutputStream;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.filesystems.FileLock;
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
 * for the non shared elements and on ${basedir}/.nb-configuration-profile file for share ones.
 * @author Anuradha G
 */
public class M2ProfilesAuxilaryConfigImpl implements AuxiliaryConfiguration {

    private static final String AUX_CONFIG = "ProfileSAuxilaryConfiguration"; //NOI18N

    private static final String CONFIG_FILE_NAME = "nb-configuration-profiles.xml"; //NOI18N

    private NbMavenProject project;

    /** Creates a new instance of M2AuxilaryConfigImpl */
    public M2ProfilesAuxilaryConfigImpl(NbMavenProject proj) {
        this.project = proj;
    }

    public Element getConfigurationFragment(final String elementName, final String namespace, boolean shared) {
        if (shared) {
            final FileObject config = project.getProjectDirectory().getFileObject(CONFIG_FILE_NAME);
            if (config != null) {
                return (Element) ProjectManager.mutex().readAccess(new Mutex.Action() {

                    public Object run() {
                        Document doc;
                        InputStream in = null;
                        try {
                            in = config.getInputStream();
                            doc = XMLUtil.parse(new InputSource(in), false, true, null, null);
                            return findElement(doc.getDocumentElement(), elementName, namespace);
                        } catch (SAXException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        } finally {
                            if (in != null) {
                                try {
                                    in.close();
                                } catch (IOException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            }
                        }
                        return null;
                    }
                });
            } else {
                String element = "project-shared-configuration"; // NOI18N

                Document doc = XMLUtil.createDocument(element, null, null, null);
                doc.getDocumentElement().appendChild(doc.createTextNode(
                        "\nThis file contains profile configuration written by modules in the NetBeans IDE.\n" +
                        "The configuration is intended to be shared among all the users of project and\n" +
                        "therefore it is assumed to be part of version control checkout.\n\n"));

                return doc.createElementNS(namespace, elementName);
            }

        }
        return (Element) ProjectManager.mutex().readAccess(new Mutex.Action() {

            public Object run() {
                String str = (String) project.getProjectDirectory().getAttribute(AUX_CONFIG);
                if (str != null) {
                    Document doc;
                    try {
                        doc = XMLUtil.parse(new InputSource(new StringReader(str)), false, true, null, null);
                        Element element = findElement(doc.getDocumentElement(), elementName, namespace);
                        if (element == null) {
                            element = doc.createElementNS(namespace, elementName);
                        }
                        return element;
                    } catch (SAXException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    String element = "project-private"; // NOI18N"

                    Document doc = XMLUtil.createDocument(element, namespace, null, null);
                    return doc.createElementNS(namespace, elementName);
                }
                return null;
            }
        });
    }

    public void putConfigurationFragment(final Element fragment, final boolean shared) throws IllegalArgumentException {
        ProjectManager.mutex().writeAccess(new  

             Mutex  
                   .Action() {

            public Object run() {
                Document doc = null;
                FileObject config = project.getProjectDirectory().getFileObject(CONFIG_FILE_NAME);
                if (shared) {
                    if (config != null) {
                        try {
                            doc = XMLUtil.parse(new InputSource(config.getInputStream()), false, true, null, null);
                        } catch (SAXException ex) {
                            ex.printStackTrace();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        String element = "project-shared-configuration"; // NOI18N

                        doc = XMLUtil.createDocument(element, null, null, null);
                        doc.getDocumentElement().appendChild(doc.createTextNode(
                                "\nThis file contains profile configuration written by modules in the NetBeans IDE.\n" +
                                "The configuration is intended to be shared among all the users of project and\n" +
                                "therefore it is assumed to be part of version control checkout.\n\n"));
                        FileLock lck = null;
                        OutputStream out = null;
                        try {
                            lck = config.lock();
                            out = config.getOutputStream(lck);
                            XMLUtil.write(doc, out, "UTF-8"); //NOI18N

                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        } finally {
                            if (out != null) {
                                try {
                                    out.close();
                                } catch (IOException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            }
                            if (lck != null) {
                                lck.releaseLock();
                            }
                        }
                    }
                } else {
                    String str = (String) project.getProjectDirectory().getAttribute(AUX_CONFIG);
                    if (str != null) {
                        try {
                            doc = XMLUtil.parse(new InputSource(new StringReader(str)), false, true, null, null);
                        } catch (SAXException ex) {
                            ex.printStackTrace();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (doc == null) {
                        String element = "project-private"; // NOI18N

                        doc = XMLUtil.createDocument(element, null, null, null);
                    }
                }
                if (doc != null) {
                    Element el = findElement(doc.getDocumentElement(), fragment.getNodeName(), fragment.getNamespaceURI());
                    if (el != null) {
                        doc.getDocumentElement().removeChild(el);
                    }
                    doc.getDocumentElement().appendChild(doc.importNode(fragment, true));
                }
                if (shared) {
                    FileLock lck = null;
                    OutputStream out = null;
                    try {
                        if (config == null) {
                            config = project.getProjectDirectory().createData(CONFIG_FILE_NAME);
                        }
                        lck = config.lock();
                        out = config.getOutputStream(lck);
                        XMLUtil.write(doc, out, "UTF-8"); //NOI18N

                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                        if (lck != null) {
                            lck.releaseLock();
                        }
                    }
                } else {
                    try {
                        StringOutputStream wr = new StringOutputStream();
                        XMLUtil.write(doc, wr, "UTF-8"); //NOI18N

                        project.getProjectDirectory().setAttribute(AUX_CONFIG, wr.toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                return null;
            }
        });

    }

    public boolean removeConfigurationFragment(final String elementName, final String namespace, final boolean shared) throws IllegalArgumentException {
        return ((Boolean) ProjectManager.mutex().writeAccess(new  

             Mutex  
                   .Action() {

            public Object run() {
                Document doc = null;
                FileObject config = project.getProjectDirectory().getFileObject(CONFIG_FILE_NAME);
                if (shared) {
                    if (config != null) {
                        try {
                            doc = XMLUtil.parse(new InputSource(config.getInputStream()), false, true, null, null);
                        } catch (SAXException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    } else {
                        return Boolean.FALSE;
                    }

                } else {
                    String str = (String) project.getProjectDirectory().getAttribute(AUX_CONFIG);
                    if (str != null) {
                        try {
                            doc = XMLUtil.parse(new InputSource(new StringReader(str)), false, true, null, null);
                        } catch (SAXException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    } else {
                        return Boolean.FALSE;
                    }
                }
                if (doc != null) {
                    Element el = findElement(doc.getDocumentElement(), elementName, namespace);
                    if (el != null) {
                        doc.getDocumentElement().removeChild(el);
                    }
                }
                if (shared) {
                    FileLock lck = null;
                    OutputStream out = null;
                    try {
                        lck = config.lock();
                        out = config.getOutputStream(lck);
                        XMLUtil.write(doc, out, "UTF-8"); //NOI18N

                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                        if (lck != null) {
                            lck.releaseLock();
                        }
                    }
                } else {
                    try {
                        StringOutputStream wr = new StringOutputStream();
                        XMLUtil.write(doc, wr, "UTF-8"); //NOI18N

                        project.getProjectDirectory().setAttribute(AUX_CONFIG, wr.toString());
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
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
                Element el = (Element) l.item(i);
                if (name.equals(el.getLocalName()) && (namespace == null || namespace.equals(el.getNamespaceURI()))) {
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
}
