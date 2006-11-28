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

package org.codehaus.mevenide.netbeans.execute;

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
import org.codehaus.mevenide.netbeans.AdditionalM2ActionsProvider;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author mkleint
 */
public final class ActionToGoalUtils {
    
    /** Creates a new instance of ActionToGoalUtils */
    private ActionToGoalUtils() {
    }
    
    public static RunConfig createRunConfig(String action, NbMavenProject project, Lookup lookup) {
        RunConfig rc = null;
        UserActionGoalProvider user = (UserActionGoalProvider)project.getLookup().lookup(UserActionGoalProvider.class);
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
                        StringTokenizer tok = new StringTokenizer(goal, " ", false);
                        List toRet = new ArrayList();
                        while (tok.hasMoreTokens()) {
                            toRet.add(tok.nextToken());
                        }
                        if (ActionProvider.COMMAND_REBUILD.equals(action)) {
                            toRet.add(0, "clean");
                        }
                        brc.setGoals(toRet);
                        brc.setExecutionName(project.getName());
                        brc.setProperties(new Properties());
                        brc.setActivatedProfiles(Collections.EMPTY_LIST);
                        return brc;
                    }
                }
            }
            Lookup.Result res = Lookup.getDefault().lookup(new Lookup.Template(AdditionalM2ActionsProvider.class));
            Iterator it = res.allInstances().iterator();
            while (it.hasNext()) {
                AdditionalM2ActionsProvider add = (AdditionalM2ActionsProvider) it.next();
                rc = add.createConfigForDefaultAction(action, project, lookup);
                if (rc != null) {
                    break;
                }
            }
        }
        return rc;
    }
    
    public static NetbeansActionMapping getActiveMapping(String action, NbMavenProject project) {
        NetbeansActionMapping na = null;
        UserActionGoalProvider user = (UserActionGoalProvider)project.getLookup().lookup(UserActionGoalProvider.class);
        na = user.getMappingForAction(action, project);
        if (na == null) {
            na = getDefaultMapping(action, project);
        }
        return na;
    }
    
    public static NetbeansActionMapping getDefaultMapping(String action, NbMavenProject project) {
        NetbeansActionMapping na = null;
        Lookup.Result res = Lookup.getDefault().lookup(new Lookup.Template(AdditionalM2ActionsProvider.class));
        Iterator it = res.allInstances().iterator();
        while (it.hasNext()) {
            AdditionalM2ActionsProvider add = (AdditionalM2ActionsProvider) it.next();
            na = add.getMappingForAction(action, project);
            if (na != null) {
                break;
            }
        }
        return na;
    }
    
    public static void setUserActionMapping(NetbeansActionMapping action, ActionToGoalMapping mapp) {
        List lst = mapp.getActions() != null ? mapp.getActions() : new ArrayList();
        Iterator it = lst.iterator();
        while (it.hasNext()) {
            NetbeansActionMapping act = (NetbeansActionMapping)it.next();
            if (act.getActionName().equals(action.getActionName())) {
                int index = lst.indexOf(act);
                it.remove();
                lst.add(index, action);
                return;
            }
            
        }
        //if not found, dd to the end.
        lst.add(action);
    }
    
    /**
     * read the action mappings from the fileobject attribute "customActionMappings"
     * @parameter fo should be the project's root directory fileobject
     *
     */
    public static ActionToGoalMapping readMappingsFromFileAttributes(FileObject fo) {
        String string = (String)fo.getAttribute("customActionMappings");
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
                fo.setAttribute("customActionMappings", string.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

