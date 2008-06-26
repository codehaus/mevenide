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
package org.netbeans.modules.maven.apisupport;

import java.io.File;
import java.io.InputStream;
import org.netbeans.modules.maven.spi.actions.MavenActionsProvider;
import org.netbeans.modules.maven.api.PluginPropertyUtils;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.api.execute.RunConfig;
import org.netbeans.modules.maven.spi.actions.AbstractMavenActionsProvider;
import org.netbeans.modules.maven.execute.model.NetbeansActionMapping;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 *
 * @author mkleint
 */
public class NbmActionGoalProvider implements MavenActionsProvider {
    private static final RequestProcessor PROCESSOR=new  RequestProcessor("NbmActionGoalProvider Clearing Task");//NOI18N
    private AbstractMavenActionsProvider platformDelegate = new AbstractMavenActionsProvider() {

        protected InputStream getActionDefinitionStream() {
            String path = "/org/netbeans/modules/maven/apisupport/platformActionMappings.xml"; //NOI18N
            InputStream in = getClass().getResourceAsStream(path);
            assert in != null : "no instream for " + path; //NOI18N
            return in;
        }

        @Override
        public boolean isActionEnable(String action, Project project, Lookup lookup) {
            return isActionEnable(action, project, lookup);
        }
    };
    private AbstractMavenActionsProvider ideDelegate = new AbstractMavenActionsProvider() {

        protected InputStream getActionDefinitionStream() {
            String path = "/org/netbeans/modules/maven/apisupport/ideActionMappings.xml"; //NOI18N
            InputStream in = getClass().getResourceAsStream(path);
            assert in != null : "no instream for " + path; //NOI18N
            return in;
        }

        @Override
        public boolean isActionEnable(String action, Project project, Lookup lookup) {
            return isActionEnable(action, project, lookup);
        }
    };

    private int CACHED_PLATFORM = VAL_NOT_CACHED;
    
    private static int VAL_NOT_CACHED = 0;
    private static int VAL_IDE = 1;
    private static int VAL_PLATFORM = 2;
    private static int VAL_NOT_NB = 3;
    
    private RequestProcessor.Task clearingTask = PROCESSOR.create(
    new Runnable() {
        public void run() {
            CACHED_PLATFORM = VAL_NOT_CACHED;
        }
    });
    
    
    /** Creates a new instance of NbmActionGoalProvider */
    public NbmActionGoalProvider() {
        clearingTask.setPriority(Thread.MIN_PRIORITY);
    }

    public boolean isActionEnable(String action, Project project, Lookup lookup) {
        if (!ActionProvider.COMMAND_RUN.equals(action) &&
                !ActionProvider.COMMAND_DEBUG.equals(action) &&
                !"nbmreload".equals(action)) {
            return false;
        }
        if (CACHED_PLATFORM == VAL_NOT_CACHED) {
            if (isPlatformApp(project)) {
                CACHED_PLATFORM = VAL_PLATFORM;
            }
            else if (hasNbm(project)) {
                CACHED_PLATFORM = VAL_IDE;
            } else {
                CACHED_PLATFORM = VAL_NOT_NB;
            }
            clearingTask.schedule(500);
        } 
        if (CACHED_PLATFORM != VAL_NOT_NB) {
            return true;
        }
        
        return false;
    }

    public RunConfig createConfigForDefaultAction(String actionName,
            Project project,
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
            Project project) {
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

    private RunConfig createConfig(String actionName, Project project, Lookup lookup, AbstractMavenActionsProvider delegate) {
        RunConfig conf = delegate.createConfigForDefaultAction(actionName, project, lookup);
        if (conf != null) {
            NbMavenProject mp = project.getLookup().lookup(NbMavenProject.class);
            if (mp.getMavenProject().getProperties().getProperty(MavenNbModuleImpl.PROP_NETBEANS_INSTALL) == null) {
                conf.getProperties().setProperty(MavenNbModuleImpl.PROP_NETBEANS_INSTALL, guessNetbeansInstallation());
            }
        }
        return conf;
    }

    private NetbeansActionMapping createMapping(String actionName, Project project, AbstractMavenActionsProvider delegate) {
        NetbeansActionMapping mapp = delegate.getMappingForAction(actionName, project);
        if (mapp != null) {
            NbMavenProject mp = project.getLookup().lookup(NbMavenProject.class);
            if (mp.getMavenProject().getProperties().getProperty(MavenNbModuleImpl.PROP_NETBEANS_INSTALL) == null) {
                mapp.getProperties().setProperty(MavenNbModuleImpl.PROP_NETBEANS_INSTALL, guessNetbeansInstallation());
            }
        }
        return mapp;
    }

    private boolean hasNbm(Project project) {
        NbMavenProject watch = project.getLookup().lookup(NbMavenProject.class);
        String pack = watch.getPackagingType();
        boolean isPom = NbMavenProject.TYPE_POM.equals(pack);
        boolean hasNbm = NbMavenProject.TYPE_NBM.equals(pack);
        if (isPom) {
            SubprojectProvider prov = project.getLookup().lookup(SubprojectProvider.class);
            for (Project prj : prov.getSubprojects()) {
                NbMavenProject w2 = prj.getLookup().lookup(NbMavenProject.class);
                if (NbMavenProject.TYPE_NBM.equals(w2.getPackagingType())) {
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
        NbMavenProject watch = p.getLookup().lookup(NbMavenProject.class);
        boolean isPom = NbMavenProject.TYPE_POM.equals(watch.getPackagingType());
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
