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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.mevenide.netbeans.AdditionalM2ActionsProvider;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
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
            Lookup.Result res = Lookup.getDefault().lookup(new Lookup.Template(AdditionalM2ActionsProvider.class));
            Iterator it = res.allInstances().iterator();
            while (it.hasNext()) {
                AdditionalM2ActionsProvider add = (AdditionalM2ActionsProvider) it.next();
                na = add.getMappingForAction(action, project);
                if (na != null) {
                    break;
                }
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
            
}
