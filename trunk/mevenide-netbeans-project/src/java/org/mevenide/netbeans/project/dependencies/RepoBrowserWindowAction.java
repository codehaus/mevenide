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

package org.mevenide.netbeans.project.dependencies;

import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;

/** The action which shows maven repository browser.
*
* @author Milos Kleint
*/
public final class RepoBrowserWindowAction extends CallableSystemAction {

    public void performAction() {
        RepoBrowserWindow win = RepoBrowserWindow.findDefault();
        win.open();
        win.requestActive();
    }
    
    protected boolean asynchronous() {
        return false;
    }

    public String getName() {
        return "Maven Repository Browser";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected String iconResource () {
        return "org/mevenide/netbeans/project/resources/RepoBrowser.png"; // NOI18N
    }
}
