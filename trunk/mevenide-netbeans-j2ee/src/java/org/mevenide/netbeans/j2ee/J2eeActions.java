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

package org.mevenide.netbeans.j2ee;

import java.awt.event.ActionEvent;
import java.util.WeakHashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.mevenide.netbeans.api.project.AdditionalActionsProvider;
import org.mevenide.netbeans.j2ee.deploy.DeployAction;
import org.mevenide.netbeans.project.MavenProject;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class J2eeActions implements AdditionalActionsProvider {
    
    private WeakHashMap cache = new WeakHashMap();
    /** Creates a new instance of J2EEActions */
    public J2eeActions() {
    }

    public Action[] createPopupActions(MavenProject project) {
        Action deploy = (Action)cache.get(project);
        if (deploy == null) {
            deploy = new DeployAction(project);
            cache.put(project, deploy);
        }
        return new Action[] {deploy};
    }
    

}
