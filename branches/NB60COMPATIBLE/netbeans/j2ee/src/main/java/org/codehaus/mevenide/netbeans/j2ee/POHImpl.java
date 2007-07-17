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

import org.codehaus.mevenide.netbeans.j2ee.web.*;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.j2ee.J2eeLookupProvider;
import org.codehaus.mevenide.netbeans.j2ee.ear.EarModuleProviderImpl;
import org.codehaus.mevenide.netbeans.j2ee.ejb.EjbModuleProviderImpl;
import org.codehaus.mevenide.netbeans.problems.ProblemReport;
import org.codehaus.mevenide.netbeans.problems.ProblemReporter;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.spi.project.ui.ProjectOpenedHook;

/**
 *
 * @author mkleint
 */
public class POHImpl extends ProjectOpenedHook {
    private NbMavenProject project;
    private J2eeLookupProvider.Provider provider;

    public POHImpl(NbMavenProject prj, J2eeLookupProvider.Provider prov) {
        project = prj;
        provider = prov;
    }
    
    public void hackWebModuleServerChange() {
        provider.hackWebModuleServerChange();
    }

    public void hackEjbModuleServerChange() {
        provider.hackEjbModuleServerChange();
    }
    
    protected void projectOpened() {
        String val = project.getOriginalMavenProject().getProperties().getProperty(WebModuleProviderImpl.ATTRIBUTE_DEPLOYMENT_SERVER_ID);
        String server = project.getOriginalMavenProject().getProperties().getProperty(WebModuleProviderImpl.ATTRIBUTE_DEPLOYMENT_SERVER);
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
                impl.setServerInstanceID(instanceFound);
                impl.getConfigSupport().ensureConfigurationReady();
            }
            EarModuleProviderImpl ear = project.getLookup().lookup(EarModuleProviderImpl.class);
            if (ear != null) {
                impl.setServerInstanceID(instanceFound);
                impl.getConfigSupport().ensureConfigurationReady();
            }
        } else if (server != null) {
            ProblemReporter report = project.getLookup().lookup(ProblemReporter.class);
            ProblemReport rep = new ProblemReport(ProblemReport.SEVERITY_HIGH, 
                    "Cannot find app server \'" + Deployment.getDefault().getServerDisplayName(server) +"\'", 
                    "The app server defined for the project is not available in your IDE. Please go to Tools/Server manager and add it.", 
                    null);
            report.addReport(rep);
        }
    }

    protected void projectClosed() {
    }


}
