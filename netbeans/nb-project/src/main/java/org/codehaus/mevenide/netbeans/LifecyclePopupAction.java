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
         
package org.codehaus.mevenide.netbeans;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.apache.maven.embedder.MavenEmbedderException;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.openide.util.actions.Presenter;

/**
 * action that  shows a list of lifecycle phases for execution in popup menus..
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class LifecyclePopupAction extends AbstractAction implements Presenter.Popup {

    private NbMavenProject project;
    private List phases;
    private boolean loadingFailed = false;
    
    /** Creates a new instance of LifecyclePopupAction */
    public LifecyclePopupAction(NbMavenProject proj ) {
        project = proj;
        phases = project.getEmbedder().getLifecyclePhases();
    }

    public void actionPerformed(ActionEvent e) {
        //do nothing is just a container for submenu items
    }

    public JMenuItem getPopupPresenter() {
        JMenu menu = new JMenu("Run lifecycle phase");
        if (loadingFailed) {
            menu.setEnabled(false);
                    
        } else {
            ActionProviderImpl provider = (ActionProviderImpl)project.getLookup().lookup(ActionProviderImpl.class);
            Iterator it = phases.iterator(); 
            while (it.hasNext()) {
                String str = (String)it.next();
                NetbeansActionMapping mapp = new NetbeansActionMapping();
                mapp.addGoal(str);
                menu.add(provider.createCustomMavenAction(str, mapp));
            }
        }
        return menu;
    }
    
    
}
