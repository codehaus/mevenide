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
package org.codehaus.mevenide.netbeans.apisupport;

import java.io.File;
import java.io.InputStream;
import org.codehaus.mevenide.netbeans.AdditionalM2ActionsProvider;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import org.codehaus.mevenide.netbeans.execute.AbstractActionGoalProvider;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author mkleint
 */
public class NbmActionGoalProvider implements AdditionalM2ActionsProvider {

    private AbstractActionGoalProvider platformDelegate = new AbstractActionGoalProvider() {

        protected InputStream getActionDefinitionStream() {
            String path = "/org/codehaus/mevenide/netbeans/apisupport/platformActionMappings.xml"; //NOI18N
            InputStream in = getClass().getResourceAsStream(path);
            assert in != null : "no instream for " + path; //NOI18N
            return in;
        }

        public boolean isActionEnable(String action, NbMavenProject project, Lookup lookup) {
            return isActionEnable(action, project, lookup);
        }
    };
    private AbstractActionGoalProvider ideDelegate = new AbstractActionGoalProvider() {

        protected InputStream getActionDefinitionStream() {
            String path = "/org/codehaus/mevenide/netbeans/apisupport/ideActionMappings.xml"; //NOI18N
            InputStream in = getClass().getResourceAsStream(path);
            assert in != null : "no instream for " + path; //NOI18N
            return in;
        }

        public boolean isActionEnable(String action, NbMavenProject project, Lookup lookup) {
            return isActionEnable(action, project, lookup);
        }
    };

    /** Creates a new instance of NbmActionGoalProvider */
    public NbmActionGoalProvider() {
    }

    public boolean isActionEnable(String action, NbMavenProject project, Lookup lookup) {
        if (!ActionProvider.COMMAND_RUN.equals(action) &&
                !ActionProvider.COMMAND_DEBUG.equals(action) &&
                !"nbmreload".equals(action)) {
            return false;
        }
        if (hasNbm(project) || isPlatformApp(project)) {
            return true;
        }
        
        return false;
    }

    public RunConfig createConfigForDefaultAction(String actionName,
            NbMavenProject project,
            Lookup lookup) {
        if (!ActionProvider.COMMAND_RUN.equals(actionName) &&
                !ActionProvider.COMMAND_DEBUG.equals(actionName) &&
                !"nbmreload".equals(actionName)) {
            return null;
        }
        if (isPlatformApp(project)) {
            return createConfig(actionName, project, lookup, platformDelegate);
        }
        if (hasNbm(project)) {
            return createConfig(actionName, project, lookup, ideDelegate);
        }
        return null;
    }

    public NetbeansActionMapping getMappingForAction(String actionName,
            NbMavenProject project) {
        if (!ActionProvider.COMMAND_RUN.equals(actionName) &&
                !ActionProvider.COMMAND_DEBUG.equals(actionName) &&
                !"nbmreload".equals(actionName)) {
            return null;
        }
        if (isPlatformApp(project)) {
            return createMapping(actionName, project, platformDelegate);
        }
        if (hasNbm(project)) {
            return createMapping(actionName, project, ideDelegate);
        }
        return null;
    }

    private RunConfig createConfig(String actionName, NbMavenProject project, Lookup lookup, AbstractActionGoalProvider delegate) {
        RunConfig conf = delegate.createConfigForDefaultAction(actionName, project, lookup);
        if (conf != null && project.getOriginalMavenProject().getProperties().getProperty(MavenNbModuleImpl.PROP_NETBEANS_INSTALL) == null) {
            conf.getProperties().setProperty(MavenNbModuleImpl.PROP_NETBEANS_INSTALL, guessNetbeansInstallation());
        }
        return conf;
    }

    private NetbeansActionMapping createMapping(String actionName, NbMavenProject project, AbstractActionGoalProvider delegate) {
        NetbeansActionMapping mapp = delegate.getMappingForAction(actionName, project);
        if (mapp != null && project.getOriginalMavenProject().getProperties().getProperty(MavenNbModuleImpl.PROP_NETBEANS_INSTALL) == null) {
            mapp.getProperties().setProperty(MavenNbModuleImpl.PROP_NETBEANS_INSTALL, guessNetbeansInstallation());
        }
        return mapp;
    }

    private boolean hasNbm(Project project) {
        ProjectURLWatcher watch = project.getLookup().lookup(ProjectURLWatcher.class);
        boolean isPom = ProjectURLWatcher.TYPE_POM.equals(watch.getPackagingType());
        boolean hasNbm = ProjectURLWatcher.TYPE_NBM.equals(watch.getPackagingType());
        if (isPom) {
            SubprojectProvider prov = project.getLookup().lookup(SubprojectProvider.class);
            for (Project prj : prov.getSubprojects()) {
                ProjectURLWatcher w2 = prj.getLookup().lookup(ProjectURLWatcher.class);
                if (ProjectURLWatcher.TYPE_NBM.equals(w2.getPackagingType())) {
                    hasNbm = true;
                    break;
                }
            }
        }
        return hasNbm;
    }

    private String guessNetbeansInstallation() {
        //TODO netbeans.home is obsolete.. what to replace it with though?
        File fil = new File(System.getProperty("netbeans.home")); //NOI18N
        fil = FileUtil.normalizeFile(fil);
        return fil.getParentFile().getAbsolutePath(); //NOI18N
    }

    private boolean isPlatformApp(Project p) {
        ProjectURLWatcher watch = p.getLookup().lookup(ProjectURLWatcher.class);
        boolean isPom = ProjectURLWatcher.TYPE_POM.equals(watch.getPackagingType());
        if (isPom) {
            String brand = PluginPropertyUtils.getPluginProperty(p, "org.codehaus.mojo", //NOI18N
                    "nbm-maven-plugin", "brandingToken", null); //NOI18N
            if (brand != null ||
                    watch.getMavenProject().getProperties().getProperty("netbeans.branding.token") != null) { //NOI18N
                return true;
            }
        }
        return false;
    }
}
