/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

package org.codehaus.mevenide.netbeans.j2ee.web;

import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.problems.ProblemReport;
import org.codehaus.mevenide.netbeans.problems.ProblemReporter;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.spi.project.ui.ProjectOpenedHook;

/**
 *
 * @author mkleint
 */
public class POHImpl extends ProjectOpenedHook {

    public POHImpl(NbMavenProject prj) {
        project = prj;
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
//        if (instanceFound == null) {
//            String[] ids = Deployment.getDefault().getServerInstanceIDs(new Object[] {J2eeModule.WAR});
//            if (ids != null && ids.length > 0) {
//                instanceFound = ids[0];
//            }
//        }
        if (instanceFound != null) {
            WebModuleProviderImpl impl = (WebModuleProviderImpl) project.getLookup().lookup(WebModuleProviderImpl.class);
            if (impl != null) {
                impl.setServerInstanceID(instanceFound);
                impl.getConfigSupport().ensureConfigurationReady();
            }
        } else if (server != null) {
            ProblemReporter report = (ProblemReporter) project.getLookup().lookup(ProblemReporter.class);
            ProblemReport rep = new ProblemReport(ProblemReport.SEVERITY_HIGH, 
                    "Cannot find app server \'" + Deployment.getDefault().getServerDisplayName(server) +"\'", 
                    "The app server defined for the project is not available in your IDE. Please go to Tools/Server manager and add it.", 
                    null);
            report.addReport(rep);
        }
    }

    protected void projectClosed() {
    }
    private NbMavenProject project;

}
