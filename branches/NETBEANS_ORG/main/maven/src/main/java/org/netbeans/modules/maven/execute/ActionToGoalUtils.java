/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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
package org.netbeans.modules.maven.execute;

import org.netbeans.modules.maven.api.execute.RunConfig;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.maven.model.Build;
import org.netbeans.modules.maven.spi.actions.MavenActionsProvider;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.ProjectProfileHandler;
import org.netbeans.modules.maven.configurations.ConfigurationProviderEnabler;
import org.netbeans.modules.maven.configurations.M2ConfigProvider;
import org.netbeans.modules.maven.configurations.M2Configuration;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.execute.model.ActionToGoalMapping;
import org.netbeans.modules.maven.execute.model.NetbeansActionMapping;
import org.netbeans.modules.maven.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.netbeans.modules.maven.execute.model.io.xpp3.NetbeansBuildActionXpp3Writer;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author mkleint
 */
public final class ActionToGoalUtils {

    private static final String FO_ATTR_CUSTOM_MAPP = "customActionMappings"; //NOI18N

    /** Creates a new instance of ActionToGoalUtils */
    private ActionToGoalUtils() {
    }

    public static RunConfig createRunConfig(String action, NbMavenProjectImpl project, Lookup lookup) {
        RunConfig rc = null;
        boolean configsEnabled = project.getLookup().lookup(ConfigurationProviderEnabler.class).isConfigurationEnabled();
        if (configsEnabled) {
            M2ConfigProvider configs = project.getLookup().lookup(M2ConfigProvider.class);
            rc = configs.getActiveConfiguration().createConfigForDefaultAction(action, project, lookup);
        }
        if (rc == null) {
            UserActionGoalProvider user = project.getLookup().lookup(UserActionGoalProvider.class);
            rc = user.createConfigForDefaultAction(action, project, lookup);
            if (rc == null) {
                // for build and rebuild check the pom for default goal and run that one..
                if (ActionProvider.COMMAND_BUILD.equals(action) ||
                        ActionProvider.COMMAND_REBUILD.equals(action)) {
                    Build bld = project.getOriginalMavenProject().getBuild();
                    if (bld != null) {
                        String goal = bld.getDefaultGoal();
                        if (goal != null && goal.trim().length() > 0) {
                            BeanRunConfig brc = new BeanRunConfig();
                            brc.setExecutionDirectory(FileUtil.toFile(project.getProjectDirectory()));
                            brc.setProject(project);
                            StringTokenizer tok = new StringTokenizer(goal, " ", false); //NOI18N
                            List<String> toRet = new ArrayList<String>();
                            while (tok.hasMoreTokens()) {
                                toRet.add(tok.nextToken());
                            }
                            if (ActionProvider.COMMAND_REBUILD.equals(action)) {
                                toRet.add(0, "clean"); //NOI18N 
                            }
                            brc.setGoals(toRet);
                            brc.setExecutionName(project.getName());
                            brc.setProperties(new Properties());
                            brc.setActivatedProfiles(Collections.EMPTY_LIST);
                            rc= brc;
                        }
                    }
                }
                

            }
        }
        if(rc==null){
            for (MavenActionsProvider add : Lookup.getDefault().lookupAll(MavenActionsProvider.class)) {
                        if (add.isActionEnable(action, project, lookup)) {
                            rc = add.createConfigForDefaultAction(action, project, lookup);
                            if (rc != null) {
                                break;
                            }
                        }
            }
        }
        if (rc != null ) {
            List<String> acts = new ArrayList<String>(); 
            acts.addAll(rc.getActivatedProfiles());
            if(configsEnabled){
              M2ConfigProvider configs = project.getLookup().lookup(M2ConfigProvider.class);
              acts.addAll(configs.getActiveConfiguration().getActivatedProfiles());
            
            }else{
              ProjectProfileHandler profileHandler=project.getLookup().lookup(ProjectProfileHandler.class);
              acts.addAll(profileHandler.getActiveProfiles(false));
            }
            rc.setActivatedProfiles(acts);
        }
        return rc;
    }

    public static boolean isActionEnable(String action, NbMavenProjectImpl project, Lookup lookup) {
       
        if (project.getLookup().lookup(ConfigurationProviderEnabler.class).isConfigurationEnabled()) {
            M2ConfigProvider configs = project.getLookup().lookup(M2ConfigProvider.class);
            if (configs.getActiveConfiguration().isActionEnable(action, project, lookup)) {
                return true;
            }
        }
        
        //check UserActionGoalProvider first
        UserActionGoalProvider user = project.getLookup().lookup(UserActionGoalProvider.class);
        if (user.isActionEnable(action, project, lookup)) {
            return true;
        }

        if (ActionProvider.COMMAND_BUILD.equals(action) ||
                ActionProvider.COMMAND_REBUILD.equals(action)) {
            Build bld = project.getOriginalMavenProject().getBuild();
            if (bld != null) {
                String goal = bld.getDefaultGoal();
                if (goal != null && goal.trim().length() > 0) {
                    return true;
                }
            }
        }
        
        for (MavenActionsProvider add : Lookup.getDefault().lookupAll(MavenActionsProvider.class)) {
            if (add.isActionEnable(action, project, lookup)) {
                return true;
            }
        }
        return false;
    }

    public static NetbeansActionMapping getActiveMapping(String action, Project project, M2Configuration configuration) {
        NetbeansActionMapping na = null;
        if (configuration != null) {
            // this parameter is somewhat suspicuous, not idea when it could be ever used from the customizer..
            na = configuration.getMappingForAction(action, project);
        }
        if (na == null) {
            UserActionGoalProvider user = project.getLookup().lookup(UserActionGoalProvider.class);
            na = user.getMappingForAction(action, project);
            if (na == null) {
                na = getDefaultMapping(action, project);
            }
        }
        return na;
    }

    public static NetbeansActionMapping[] getActiveCustomMappings(NbMavenProjectImpl project) {
        M2ConfigProvider configs = project.getLookup().lookup(M2ConfigProvider.class);
        UserActionGoalProvider user = project.getLookup().lookup(UserActionGoalProvider.class);
        List<NetbeansActionMapping> toRet = new ArrayList<NetbeansActionMapping>();
        List<String> names = new ArrayList<String>();
        // first add all project specific custom actions.
        if (project.getLookup().lookup(ConfigurationProviderEnabler.class).isConfigurationEnabled()) {
            for (NetbeansActionMapping map : configs.getActiveConfiguration().getCustomMappings()) {
                toRet.add(map);
                names.add(map.getActionName());
            }
        }
        for (NetbeansActionMapping map : user.getCustomMappings()) {
            toRet.add(map);
            names.add(map.getActionName());
        }
        for (MavenActionsProvider prov : Lookup.getDefault().lookupAll(MavenActionsProvider.class)) {
            if (prov instanceof NbGlobalActionGoalProvider) {
                // check the global actions defined, include only if not the same name as project-specific one.
                for (NetbeansActionMapping map : ((NbGlobalActionGoalProvider) prov).getCustomMappings()) {
                    if (!names.contains(map.getActionName())) {
                        toRet.add(map);
                    }
                }
            }
        }
        return toRet.toArray(new NetbeansActionMapping[toRet.size()]);
    }

    public static NetbeansActionMapping getDefaultMapping(String action, Project project) {
        NetbeansActionMapping na = null;
        Lookup.Result res = Lookup.getDefault().lookup(new Lookup.Template(MavenActionsProvider.class));
        Iterator it = res.allInstances().iterator();
        while (it.hasNext()) {
            MavenActionsProvider add = (MavenActionsProvider) it.next();
            na = add.getMappingForAction(action, project);
            if (na != null) {
                break;
            }
        }
        return na;
    }

    /**
     * read the action mappings from the fileobject attribute "customActionMappings"
     * @parameter fo should be the project's root directory fileobject
     *
     */
    public static ActionToGoalMapping readMappingsFromFileAttributes(FileObject fo) {
        String string = (String) fo.getAttribute(FO_ATTR_CUSTOM_MAPP);
        ActionToGoalMapping mapp = null;
        if (string != null) {
            NetbeansBuildActionXpp3Reader reader = new NetbeansBuildActionXpp3Reader();
            try {
                mapp = reader.read(new StringReader(string));
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (XmlPullParserException ex) {
                ex.printStackTrace();
            }
        }
        if (mapp == null) {
            mapp = new ActionToGoalMapping();
        }
        return mapp;
    }

    /**
     * writes the action mappings to the fileobject attribute "customActionMappings"
     * @parameter fo should be the project's root directory fileobject
     *
     */
    public static void writeMappingsToFileAttributes(FileObject fo, ActionToGoalMapping mapp) {
        NetbeansBuildActionXpp3Writer writer = new NetbeansBuildActionXpp3Writer();
        StringWriter string = new StringWriter();
        boolean error = false;
        try {
            writer.write(string, mapp);
        } catch (IOException ex) {
            ex.printStackTrace();
            error = true;
        }
        if (!error) {
            try {
                fo.setAttribute(FO_ATTR_CUSTOM_MAPP, string.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
