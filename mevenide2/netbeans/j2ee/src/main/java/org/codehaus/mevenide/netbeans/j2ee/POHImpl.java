/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

package org.codehaus.mevenide.netbeans.j2ee;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.model.Profile;
import org.codehaus.mevenide.netbeans.j2ee.web.*;
import org.codehaus.mevenide.netbeans.api.Constants;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import org.codehaus.mevenide.netbeans.j2ee.ear.EarModuleProviderImpl;
import org.codehaus.mevenide.netbeans.j2ee.ejb.EjbModuleProviderImpl;
import org.codehaus.mevenide.netbeans.problems.ProblemReport;
import org.codehaus.mevenide.netbeans.problems.ProblemReporter;
import org.codehaus.mevenide.netbeans.spi.archetype.WizardExtenderUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ServerManager;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
public class POHImpl extends ProjectOpenedHook {
    private Project project;
    private J2eeLookupProvider.Provider provider;
    private PropertyChangeListener refreshListener;

    public POHImpl(Project prj, J2eeLookupProvider.Provider prov) {
        project = prj;
        provider = prov;
        
    }
    
    public void hackModuleServerChange() {
        provider.hackModuleServerChange();
    }
    
    protected void projectOpened() {
        provider.hackModuleServerChange();
        ProjectURLWatcher watch = project.getLookup().lookup(ProjectURLWatcher.class);
        String val = watch.getMavenProject().getProperties().getProperty(Constants.HINT_DEPLOY_J2EE_SERVER_ID);
        String server = watch.getMavenProject().getProperties().getProperty(Constants.HINT_DEPLOY_J2EE_SERVER);
        if (server == null) {
            //try checking for old values..
            server = watch.getMavenProject().getProperties().getProperty(Constants.HINT_DEPLOY_J2EE_SERVER_OLD);
        }
        String instanceFound = null;
        if (server != null) {
            String[] instances = Deployment.getDefault().getInstancesOfServer(server);
            String inst = null;
            if (instances != null && instances.length > 0) {
                inst = instances[0];
                for (int i = 0; i < instances.length; i++) {
                    if (val != null && val.equals(instances[i])) {
                        inst = instances[i];
                        break;
                    }
                }
                instanceFound = inst;
            }
        }

        if (instanceFound != null) {
            WebModuleProviderImpl impl = project.getLookup().lookup(WebModuleProviderImpl.class);
            if (impl != null) {
                impl.setServerInstanceID(instanceFound);
                impl.getConfigSupport().ensureConfigurationReady();
            }
            EjbModuleProviderImpl ejb = project.getLookup().lookup(EjbModuleProviderImpl.class);
            if (ejb != null) {
                ejb.setServerInstanceID(instanceFound);
                ejb.getConfigSupport().ensureConfigurationReady();
            }
            EarModuleProviderImpl ear = project.getLookup().lookup(EarModuleProviderImpl.class);
            if (ear != null) {
                ear.setServerInstanceID(instanceFound);
                ear.getConfigSupport().ensureConfigurationReady();
            }
        } else if (server != null) {
            ProblemReporter report = project.getLookup().lookup(ProblemReporter.class);
            String tit = Deployment.getDefault().getServerDisplayName(server);
            if (tit == null) {
                tit = server;
            }
            ProblemReport rep = new ProblemReport(ProblemReport.SEVERITY_HIGH, 
                    NbBundle.getMessage(POHImpl.class, "MSG_AppServer", tit),
                    NbBundle.getMessage(POHImpl.class, "HINT_AppServer"),
                    new AddServerAction(project));
            report.addReport(rep);
            
        }
        if (refreshListener == null) {
            //#121148 when the user edits the file we need to reset the server instance
            ProjectURLWatcher watcher = project.getLookup().lookup(ProjectURLWatcher.class);
            refreshListener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (ProjectURLWatcher.PROP_PROJECT.equals(evt.getPropertyName())) {
                        projectOpened();
                    }
                }
            };
            watcher.addPropertyChangeListener(refreshListener);
        }
    }

    protected void projectClosed() {
        //is null check necessary?
        if (refreshListener != null) {
            ProjectURLWatcher watcher = project.getLookup().lookup(ProjectURLWatcher.class);
            watcher.removePropertyChangeListener(refreshListener);
            refreshListener = null;
        }
    }
    
    private static class AddServerAction extends AbstractAction {
        private Project prj;
        private AddServerAction(Project project) {
            prj = project;
            putValue(Action.NAME, NbBundle.getMessage(POHImpl.class, "TXT_Add_Server"));
        }
        
        public void actionPerformed(ActionEvent e) {
            String newOne = ServerManager.showAddServerInstanceWizard();
            String serverType = null;
            if (newOne != null) {
                serverType = Deployment.getDefault().getServerID(newOne);
            }
            try {
                ModelHandle handle = WizardExtenderUtils.createModelHandle(prj);
                //get rid of old settings.
                Profile prof = handle.getNetbeansPublicProfile(false);
                if (prof != null) {
                    prof.getProperties().remove(Constants.HINT_DEPLOY_J2EE_SERVER_OLD);
                }
                if (newOne != null) {
                    handle.getPOMModel().getProperties().setProperty(Constants.HINT_DEPLOY_J2EE_SERVER, serverType);
                    handle.getNetbeansPrivateProfile().getProperties().setProperty(Constants.HINT_DEPLOY_J2EE_SERVER_ID, newOne);
                    handle.markAsModified(handle.getProfileModel());
                } else {
                    handle.getPOMModel().getProperties().remove(Constants.HINT_DEPLOY_J2EE_SERVER);
                    org.apache.maven.profiles.Profile privprof = handle.getNetbeansPrivateProfile(false);
                    if (privprof != null) {
                        privprof.getProperties().remove(Constants.HINT_DEPLOY_J2EE_SERVER_ID);
                        handle.markAsModified(handle.getProfileModel());
                    }
                }
                handle.markAsModified(handle.getPOMModel());
                WizardExtenderUtils.writeModelHandle(handle, prj);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (XmlPullParserException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
