/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.repository;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows M2RepositoryBrowser component.
 */
public class M2RepositoryBrowserAction extends AbstractAction {
    
    public M2RepositoryBrowserAction() {
        super(NbBundle.getMessage(M2RepositoryBrowserAction.class, "CTL_M2RepositoryBrowserAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(M2RepositoryBrowserTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = M2RepositoryBrowserTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
}
