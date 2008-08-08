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

package org.netbeans.modules.maven;

import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.AuxiliaryProperties;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * implementation of AuxiliaryProperties.
 * @author mkleint
 */
public class MavenProjectPropsImpl implements AuxiliaryProperties {

    static String NAMESPACE = "http://www.netbeans.org/ns/maven-properties-data/1"; //NOI18N
    static String ROOT = "properties"; //NOI18N

    private Project prj;
    private boolean transaction = false;
    private TreeMap<String, String> transPropsShared;
    private TreeMap<String, String> transPropsPrivate;
    private AuxiliaryConfiguration aux;
    private boolean sharedChanged;

    public MavenProjectPropsImpl(Project project, AuxiliaryConfiguration aux) {
        prj = project;
        this.aux = aux;
    }

    private AuxiliaryConfiguration getAuxConf() {
        return aux;
    }

    public synchronized String get(String key, boolean shared) {
        return get(key, shared, true);
    }

    public synchronized String get(String key, boolean shared, boolean usePom) {
        if (transaction) {
            if (shared && transPropsShared.containsKey(key)) {
                return transPropsShared.get(key);
            }
            if (!shared && transPropsPrivate.containsKey(key)) {
                return transPropsPrivate.get(key);
            }
        } else {
            TreeMap<String, String> props = readProperties(getAuxConf(), shared);
            //TODO optimize
            String ret =  props.get(key);
            if (ret != null) {
                return ret;
            }
        }
        if (shared && usePom) {
            String val = prj.getLookup().lookup(NbMavenProject.class).getMavenProject().getProperties().getProperty(key);
            if (val != null) {
                return val;
            }
        }
        return null;
    }

    public synchronized void put(String key, String value, boolean shared) {
        if (shared) {
            //TODO put props to project.. shall we actually do it here?
        }
        if (transaction) {
            if (shared) {
                transPropsShared.put(key, value);
                sharedChanged = true;
            } else {
                transPropsPrivate.put(key, value);
            }
        } else {
            writeAuxiliaryData(getAuxConf(), key, value, shared);
        }
    }

    public synchronized Iterable<String> listKeys(boolean shared) {
        TreeMap<String, String> props = readProperties(getAuxConf(), shared);
        if (shared) {
            Properties mvnprops =  prj.getLookup().lookup(NbMavenProject.class).getMavenProject().getProperties();
            for (Object prop: mvnprops.keySet()) {
                props.put((String)prop, "any"); //NOI18N
            }
        }
        return props.keySet();
    }

    private void writeAuxiliaryData(AuxiliaryConfiguration conf, String property, String value, boolean shared) {
        Element el = getOrCreateRootElement(conf, shared);
        Element enEl;
        NodeList list = el.getElementsByTagNameNS(NAMESPACE, property);
        if (list.getLength() > 0) {
            enEl = (Element)list.item(0);
        } else {
            enEl = el.getOwnerDocument().createElementNS(NAMESPACE, property);
            el.appendChild(enEl);
        }
        if (value != null) {
            enEl.setTextContent(value);
        } else {
            el.removeChild(enEl);
        }
        conf.putConfigurationFragment(el, shared);
    }

    private void writeAuxiliaryData(AuxiliaryConfiguration conf, TreeMap<String, String> props, boolean shared) {
        Element el = getOrCreateRootElement(conf, shared);
        Element enEl;
        for (String key : props.keySet()) {
            NodeList list = el.getElementsByTagNameNS(NAMESPACE, key);
            if (list.getLength() > 0) {
                enEl = (Element)list.item(0);
            } else {
                enEl = el.getOwnerDocument().createElementNS(NAMESPACE, key);
                el.appendChild(enEl);
            }
            String value = props.get(key);
            if (value != null) {
                enEl.setTextContent(value);
            } else {
                el.removeChild(enEl);
            }
        }
        conf.putConfigurationFragment(el, shared);
    }

    private Element getOrCreateRootElement(AuxiliaryConfiguration conf, boolean shared) {
        Element el = conf.getConfigurationFragment(ROOT, NAMESPACE, shared);
        if (el == null) {
            el = XMLUtil.createDocument(ROOT, NAMESPACE, null, null).getDocumentElement();
            Comment comment = el.getOwnerDocument().createComment("\nProperties that influence various parts of the IDE, especially code formatting and the like. \n" + //NOI18N
                    "You can copy and paste the single properties, into the pom.xml file and the IDE will pick them up.\n" + //NOI18N
                    "That way multiple projects can share the same settings (useful for formatting rules for example).\n" + //NOI18N
                    "Any value defined here will override the pom.xml file value but is only applicable to the current project.\n"); //NOI18N
            el.appendChild(comment);
        }
        return el;
    }


    private TreeMap<String, String> readProperties(AuxiliaryConfiguration aux, boolean shared) {
        TreeMap<String, String> props = new TreeMap<String, String>();
        Element el = aux.getConfigurationFragment(ROOT, NAMESPACE, shared);
        if (el != null) {
            NodeList list = el.getChildNodes();
            if (list.getLength() > 0) {
                for (int i = 0; i < list.getLength(); i++) {
                    Node nd = list.item(i);
                    if (nd instanceof Element) {
                        Element enEl = (Element)nd;
                        props.put(enEl.getNodeName(), enEl.getTextContent());
                    }
                }
            }
        }
        return props;
    }

    public synchronized TreeMap<String, String> getRawProperties(boolean shared) {
        return readProperties(getAuxConf(), shared);
    }

    public synchronized void startTransaction() {
        transaction = true;
        transPropsShared = getRawProperties(true);
        transPropsPrivate = getRawProperties(false);
        sharedChanged = false;

    }

    public synchronized void commitTransaction() {
        transaction = false;
        if (transPropsShared == null) {
            Logger.getLogger(MavenProjectPropsImpl.class.getName()).info("Commiting a transaction that was cancelled.");
            return;
        }
        if (sharedChanged) {
            writeAuxiliaryData(getAuxConf(), transPropsShared, true);
        }
        writeAuxiliaryData(getAuxConf(), transPropsPrivate, false);
        transPropsPrivate = null;
        transPropsShared = null;
    }

    public synchronized void cancelTransaction() {
        transaction = false;
        transPropsPrivate = null;
        transPropsShared = null;
    }

}
