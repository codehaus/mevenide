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

import org.codehaus.mevenide.netbeans.NbMavenProject;
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
    
    private Boolean cached;
    private InstanceContent instanceContent;
    private M2ConfigProvider provider;    

    public ConfigurationProviderEnabler(NbMavenProject project, InstanceContent ic) {
        this.instanceContent = ic;
        this.project = project;
        provider = new M2ConfigProvider(project);
        if (isConfigurationEnabled()) {
            ic.add(provider);
        }
    }

    public boolean isConfigurationEnabled() {
        boolean enabled = false;
        AuxiliaryConfiguration conf = project.getLookup().lookup(AuxiliaryConfiguration.class);
        Element el = conf.getConfigurationFragment(ROOT, NAMESPACE, false);
        if (el != null) {
            NodeList list = el.getElementsByTagNameNS(NAMESPACE, ENABLED);
            if (list.getLength() > 0) {
                Element enEl = (Element)list.item(0);
                enabled = Boolean.parseBoolean(enEl.getTextContent());
            }
        }
        return enabled;
    }
    
    public void enableConfigurations(boolean enable) {
        AuxiliaryConfiguration conf = project.getLookup().lookup(AuxiliaryConfiguration.class);
        if (enable) {
            writeAuxiliaryData(conf, ENABLED, Boolean.toString(enable));
            instanceContent.add(provider);
        } else {
            conf.removeConfigurationFragment(ROOT, NAMESPACE, false);
            instanceContent.remove(provider);
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
    
}
