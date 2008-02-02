/*
 *  Copyright 2005-2008 Mevenide Team.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.netbeans.actions.scm;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.apache.maven.artifact.Artifact;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class SCMActions extends AbstractAction  implements Presenter.Popup{
    private Artifact artifact;
   
    public SCMActions(Artifact artifact) {
        putValue(NAME, NbBundle.getMessage(SCMActions.class, "LBL_SCM"));//NOI18N
        this.artifact = artifact;
    }
    
    
    public void actionPerformed(ActionEvent e) {
        //ignore
    }

    public JMenuItem getPopupPresenter() {
         JMenu menu = new JMenu(NbBundle.getMessage(SCMActions.class, "LBL_SCM"));
         menu.add(new OpenScmURLAction(artifact));
         menu.add(new CheckoutAction(artifact));
         return menu;
    }

}
