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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProfileUtils;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import org.codehaus.mevenide.netbeans.customizer.CustomizerProviderImpl;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.ProjectConfigurationProvider;
import org.openide.util.RequestProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author mkleint
 */
public class M2ConfigProvider implements ProjectConfigurationProvider<M2Configuration> {

    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private NbMavenProject project;
    private List<M2Configuration> profiles = null;
    private final M2Configuration DEFAULT;
    private M2Configuration active;
    private String initialActive;
    
    
    public M2ConfigProvider(NbMavenProject proj) {
        project = proj;
        DEFAULT = M2Configuration.createDefault(project);
        //read the active one..
        AuxiliaryConfiguration conf = project.getLookup().lookup(AuxiliaryConfiguration.class);
        Element el = conf.getConfigurationFragment(ConfigurationProviderEnabler.ROOT, ConfigurationProviderEnabler.NAMESPACE, false);
        if (el != null) {
            NodeList list = el.getElementsByTagNameNS(ConfigurationProviderEnabler.NAMESPACE, ConfigurationProviderEnabler.ACTIVATED);
            if (list.getLength() > 0) {
                Element enEl = (Element)list.item(0);
                initialActive = new String(enEl.getTextContent());
            }
        }
        
        active = DEFAULT;
        project.getProjectWatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                    synchronized (M2ConfigProvider.this) {
                        profiles = null;
                    }
                    RequestProcessor.getDefault().post(new Runnable() {
                        public void run() {
                            firePropertyChange();
                        }

                    });
                }
            }
        });
    }
    
    public synchronized Collection<M2Configuration> getConfigurations() {
        if (profiles == null) {
            profiles = createProfilesList();
        }
        return new ArrayList<M2Configuration>(profiles);
    }

    public boolean hasCustomizer() {
        return true;
    }

    public void customize() {
        CustomizerProviderImpl prv = project.getLookup().lookup(CustomizerProviderImpl.class);
        prv.showCustomizer(ModelHandle.PANEL_CONFIGURATION);
    }

    public boolean configurationsAffectAction(String action) {
        if (ActionProvider.COMMAND_DELETE.equals(action) || ActionProvider.COMMAND_COPY.equals(action) || ActionProvider.COMMAND_MOVE.equals(action)) {
            return false;
        }
        return true;
    }

    public void addPropertyChangeListener(PropertyChangeListener lst) {
        support.addPropertyChangeListener(lst);
    }

    public void removePropertyChangeListener(PropertyChangeListener lst) {
        support.removePropertyChangeListener(lst);
    }

    public synchronized M2Configuration getActiveConfiguration() {
        if (initialActive != null) {
            for (M2Configuration conf : getConfigurations()) {
                if (initialActive.equals(conf.getId())) {
                    active = conf;
                    initialActive = null;
                    break;
                }
            }
        }
        return active;
    }

    public synchronized void setActiveConfiguration(M2Configuration configuration) throws IllegalArgumentException, IOException {
        M2Configuration old = active;
        active = configuration;
        ConfigurationProviderEnabler.writeAuxiliaryData(
                project.getLookup().lookup(AuxiliaryConfiguration.class), 
                ConfigurationProviderEnabler.ACTIVATED, active.getId());
        support.firePropertyChange(PROP_CONFIGURATION_ACTIVE, old, active);
    }

    private List<M2Configuration> createProfilesList() {
        List<String> profs = ProfileUtils.retrieveAllProfiles(project.getOriginalMavenProject());
        List<M2Configuration> config = new ArrayList<M2Configuration>();
        config.add(DEFAULT);
        for (String prof : profs) {
            config.add(new M2Configuration(prof, project));
        }
        return config;
    }

    private void firePropertyChange() {
        support.firePropertyChange(ProjectConfigurationProvider.PROP_CONFIGURATIONS, null, null);
    }
}
