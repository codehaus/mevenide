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

package org.codehaus.mevenide.netbeans.graph;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author mkleint
 */
public class ShowGraphAction extends AbstractAction implements ContextAwareAction {
    public ShowGraphAction() {
        putValue(Action.NAME, "Show Library Dependency Graph");
    }
    
    public ShowGraphAction(NbMavenProject prj) {
        this();
        if (prj != null) {
            putValue("prj", prj);
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        NbMavenProject project = (NbMavenProject) getValue("prj");
        if (project != null) {
            TopComponent tc = new DependencyGraphTopComponent(project);
            WindowManager.getDefault().findMode("editor").dockInto(tc); //NOI18N
            tc.open();
            tc.requestActive();
        }
    }
    
    public Action createContextAwareInstance(Lookup lookup) {
        NbMavenProject prj = lookup.lookup(NbMavenProject.class);
        return new ShowGraphAction(prj);
    }
}