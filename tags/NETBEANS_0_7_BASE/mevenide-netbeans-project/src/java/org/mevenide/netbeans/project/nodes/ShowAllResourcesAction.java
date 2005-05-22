/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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

package org.mevenide.netbeans.project.nodes;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class ShowAllResourcesAction extends AbstractAction {
    
    private static ShowAllResourcesAction instance;

    private boolean showAll = false;
    public static final String PROP_SHOWALL = "ShowAll"; //NOI18N
    
    /** Creates a new instance of ShowAllResourcesAction */
    private ShowAllResourcesAction() {
        putValue(Action.NAME, getDynamicName());
    }
    
    private String getDynamicName() {
        return showAll ? "Show Only Included Files" : "Show All Files";
    }
    
    public static ShowAllResourcesAction getInstance() {
        if (instance == null) {
            instance = new ShowAllResourcesAction();
        }
        return instance;
    }

    
    public boolean isShowingAll() {
        return showAll;
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        showAll = !showAll;
        putValue(Action.NAME, getDynamicName());
        firePropertyChange(PROP_SHOWALL, Boolean.valueOf(!showAll), Boolean.valueOf(showAll));
    }
}
