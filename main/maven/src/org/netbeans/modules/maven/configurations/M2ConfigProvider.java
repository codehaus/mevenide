/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.configurations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.ProjectProfileHandler;
import org.netbeans.modules.maven.api.customizer.ModelHandle;
import org.netbeans.modules.maven.customizer.CustomizerProviderImpl;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.ProjectConfigurationProvider;
import org.openide.util.RequestProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * WARNING: this class shall in no way use project.getLookup() as it's called
 * in the critical loop (getOriginalMavenproject
 * @author mkleint
 */
public class M2ConfigProvider implements ProjectConfigurationProvider<M2Configuration> {

    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private NbMavenProjectImpl project;
    private List<M2Configuration> profiles = null;
    private List<M2Configuration> shared = null;
    private List<M2Configuration> nonshared = null;
    private final M2Configuration DEFAULT;
    private M2Configuration active;
    private String initialActive;
    private AuxiliaryConfiguration aux;
    private ProjectProfileHandler profileHandler;
    
    public M2ConfigProvider(NbMavenProjectImpl proj, AuxiliaryConfiguration aux, ProjectProfileHandler prof) {
        project = proj;
        this.aux = aux;
        profileHandler = prof;
        DEFAULT = M2Configuration.createDefault(project);
        //read the active one..
        Element el = aux.getConfigurationFragment(ConfigurationProviderEnabler.ROOT, ConfigurationProviderEnabler.NAMESPACE, false);
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
                if (NbMavenProjectImpl.PROP_PROJECT.equals(evt.getPropertyName())) {
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
        //trigger the active configuration check..
        getActiveConfiguration();
    }
    
    private synchronized Collection<M2Configuration> getConfigurations(boolean skipProfiles) {
        if (profiles == null && !skipProfiles) {
            profiles = createProfilesList();
        }
        if (shared == null) {
            //read from auxconf
            shared = readConfiguration(true);
        }
        if (nonshared == null) {
            //read from auxconf
            nonshared = readConfiguration(false);
        }
        ArrayList<M2Configuration> toRet = new ArrayList<M2Configuration>();
        toRet.add(DEFAULT);
        toRet.addAll(shared);
        toRet.addAll(nonshared);
        if (!skipProfiles) {
            toRet.addAll(profiles);
        }
        if (active != null && !toRet.contains(active)) {
            toRet.add(active);
        }
        return toRet;
        
    }
    
    public synchronized Collection<M2Configuration> getConfigurations() {
        return getConfigurations(false);
    }

    public M2Configuration getDefaultConfig() {
        return DEFAULT;
    }
    
    public synchronized Collection<M2Configuration> getProfileConfigurations() {
        getConfigurations();
        return profiles;
    }
    
    public synchronized Collection<M2Configuration> getSharedConfigurations() {
        getConfigurations();
        return shared;
    }
    
    public synchronized Collection<M2Configuration> getNonSharedConfigurations() {
        getConfigurations();
        return nonshared;
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
            for (M2Configuration conf : getConfigurations(true)) {
                if (initialActive.equals(conf.getId())) {
                    active = conf;
                    initialActive = null;
                    break;
                }
            }
            if (initialActive != null) {
                //asume it's profile based.
                active = new M2Configuration(initialActive, project);
                active.setActivatedProfiles(Collections.singletonList(initialActive));
                initialActive = null;
            }
        }
        return active;
    }
    
    public synchronized void setConfigurations(List<M2Configuration> shared, List<M2Configuration> nonshared, boolean includeProfiles) {
        ConfigurationProviderEnabler.writeAuxiliaryData(aux, true, shared);
        ConfigurationProviderEnabler.writeAuxiliaryData(aux, false, nonshared);
        this.shared = shared;
        this.nonshared = nonshared;
        this.profiles = null;
        firePropertyChange();
    }

    public synchronized void setActiveConfiguration(M2Configuration configuration) throws IllegalArgumentException, IOException {
        if (active == configuration || (active != null && active.equals(configuration))) {
            return;
        }
        M2Configuration old = active;
        active = configuration;
        ConfigurationProviderEnabler.writeAuxiliaryData(
                aux, 
                ConfigurationProviderEnabler.ACTIVATED, active.getId());
        support.firePropertyChange(PROP_CONFIGURATION_ACTIVE, old, active);
    }

    private List<M2Configuration> createProfilesList() {
        List<String> profs = profileHandler.getAllProfiles();
        List<M2Configuration> config = new ArrayList<M2Configuration>();
//        config.add(DEFAULT);
        for (String prof : profs) {
            M2Configuration c = new M2Configuration(prof, project);
            c.setActivatedProfiles(Collections.singletonList(prof));
            config.add(c);
        }
        return config;
    }

    private void firePropertyChange() {
        support.firePropertyChange(ProjectConfigurationProvider.PROP_CONFIGURATIONS, null, null);
    }
    
    private List<M2Configuration> readConfiguration(boolean shared) {
        Element el = aux.getConfigurationFragment(ConfigurationProviderEnabler.ROOT, ConfigurationProviderEnabler.NAMESPACE, shared);
        if (el != null) {
            NodeList list = el.getElementsByTagNameNS(ConfigurationProviderEnabler.NAMESPACE, ConfigurationProviderEnabler.CONFIG);
            if (list.getLength() > 0) {
                List<M2Configuration> toRet = new ArrayList<M2Configuration>();
                int len = list.getLength();
                for (int i = 0; i < len; i++) {
                    Element enEl = (Element)list.item(i);
                    
                    M2Configuration c = new M2Configuration(enEl.getAttribute(ConfigurationProviderEnabler.CONFIG_ID_ATTR), project);
                    String profs = enEl.getAttribute(ConfigurationProviderEnabler.CONFIG_PROFILES_ATTR);
                    if (profs != null) {
                        String[] s = profs.split(" ");
                        List<String> prf = new ArrayList<String>();
                        for (String s2 : prf) {
                            if (s2.trim().length() > 0) {
                                prf.add(s2.trim());
                            }
                        }
                        c.setActivatedProfiles(prf);
                    }
                    toRet.add(c);
                }
                return toRet;
            }
        }
        return new ArrayList<M2Configuration>();
    }
}
