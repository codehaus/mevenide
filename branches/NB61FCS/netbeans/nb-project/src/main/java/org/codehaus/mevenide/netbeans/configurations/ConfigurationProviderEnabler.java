/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.netbeans.configurations;

import java.util.List;
import org.codehaus.mevenide.netbeans.M2AuxilaryConfigImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectProfileHandler;
import org.codehaus.plexus.util.StringUtils;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.util.lookup.InstanceContent;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * a class that is always present in projects lookup and can be queried 
 * if M2ConfigProvider is enabled or not and sets enable state value as well.
 * @author mkleint
 */
public class ConfigurationProviderEnabler {
    private NbMavenProject project;
    static String NAMESPACE = "http://www.netbeans.org/ns/maven-config-data/1"; //NOI18N
    static String ROOT = "config-data"; //NOI18N
    static String ENABLED = "enabled"; //NOI18N
    static String ACTIVATED = "activated"; //NOI18N
    static String CONFIGURATIONS = "configurations"; //NOI18N
    static String CONFIG = "configuration"; //NOI18N
    static String CONFIG_PROFILES_ATTR = "profiles"; //NOI18N
    static String CONFIG_ID_ATTR = "id"; //NOI18N
    
    private Boolean cached;
    private InstanceContent instanceContent;
    private M2ConfigProvider provider;
    private M2AuxilaryConfigImpl aux;

    public ConfigurationProviderEnabler(NbMavenProject project, M2AuxilaryConfigImpl auxiliary, ProjectProfileHandler hand) {
        this.project = project;
        aux = auxiliary;
        provider = new M2ConfigProvider(project, aux, hand);
    }
    
    public M2ConfigProvider getConfigProvider() {
        return provider;
    }

    public synchronized boolean isConfigurationEnabled() {
        boolean enabled = false;
        if (cached == null) {
            Element el = aux.getConfigurationFragment(ROOT, NAMESPACE, false);
            if (el != null) {
                NodeList list = el.getElementsByTagNameNS(NAMESPACE, ENABLED);
                if (list.getLength() > 0) {
                    Element enEl = (Element)list.item(0);
                    enabled = Boolean.parseBoolean(enEl.getTextContent());
                }
            }
            cached = enabled;
        } else {
            enabled = cached;
        }
        return enabled;
    }
    
    public synchronized void enableConfigurations(boolean enable) {
        if (enable) {
            writeAuxiliaryData(aux, ENABLED, Boolean.toString(enable));
            if (instanceContent != null) {
                instanceContent.add(provider);
            }
        } else {
            aux.removeConfigurationFragment(ROOT, NAMESPACE, false);
            if (instanceContent != null) {
                instanceContent.remove(provider);
            }
        }
        cached = enable;
    }
    
    public synchronized void setInstanceContent(InstanceContent ic) {
        this.instanceContent = ic;
        if (isConfigurationEnabled()) {
            ic.add(provider);
        }
    }
    
    public static void writeAuxiliaryData(AuxiliaryConfiguration conf, String property, String value) {
        Element el = conf.getConfigurationFragment(ROOT, NAMESPACE, false);
        if (el == null) {
            el = XMLUtil.createDocument(ROOT, NAMESPACE, null, null).getDocumentElement();
        }
        Element enEl;
        NodeList list = el.getElementsByTagNameNS(NAMESPACE, property);
        if (list.getLength() > 0) {
            enEl = (Element)list.item(0);
        } else {
            enEl = el.getOwnerDocument().createElementNS(NAMESPACE, property);
            el.appendChild(enEl);
        }
        enEl.setTextContent(value);
        conf.putConfigurationFragment(el, false);
    }
    
    static void writeAuxiliaryData(AuxiliaryConfiguration conf, boolean shared, List<M2Configuration> configs) {
        Element el = conf.getConfigurationFragment(ROOT, NAMESPACE, shared);
        if (el == null) {
            el = XMLUtil.createDocument(ROOT, NAMESPACE, null, null).getDocumentElement();
        }
        Element enEl;
        NodeList list = el.getElementsByTagNameNS(NAMESPACE, CONFIGURATIONS);
        if (list.getLength() > 0) {
            enEl = (Element)list.item(0);
            NodeList nl = enEl.getChildNodes();
            int len = nl.getLength();
            for (int i = 0; i < len; i++) {
                enEl.removeChild(nl.item(0));
            }
        } else {
            enEl = el.getOwnerDocument().createElementNS(NAMESPACE, CONFIGURATIONS);
            el.appendChild(enEl);
        }
        for (M2Configuration config : configs) {
            Element child  = enEl.getOwnerDocument().createElementNS(NAMESPACE, CONFIG);
            child.setAttribute(CONFIG_ID_ATTR, config.getId());
            child.setAttribute(CONFIG_PROFILES_ATTR, StringUtils.join(config.getActivatedProfiles().iterator(), " "));
            enEl.appendChild(child);
        }
        conf.putConfigurationFragment(el, shared);
    }

}
