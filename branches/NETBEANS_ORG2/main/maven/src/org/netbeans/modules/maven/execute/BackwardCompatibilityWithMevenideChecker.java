/* ==========================================================================
 * Copyright 2003-2007 Mevenide Team
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

import java.util.Arrays;
import java.util.List;
import org.netbeans.modules.maven.api.execute.PrerequisitesChecker;
import org.netbeans.modules.maven.api.execute.RunConfig;
import org.openide.util.Exceptions;

/**
 *
 * @author mkleint
 */
public class BackwardCompatibilityWithMevenideChecker implements PrerequisitesChecker {

    public boolean checkRunConfig(String actionName, RunConfig config) {
        String[] gls = config.getGoals().toArray(new String[0]);
        boolean changed = false;
        for (int i = 0; i < gls.length; i++) {
            if (gls[i].startsWith("org.codehaus.mevenide:netbeans-deploy-plugin")) {
                gls[i] = "org.netbeans.plugins:netbeans-deploy-plugin:2.0:deploy";
                changed = true;
            } else
            if (gls[i].startsWith("org.codehaus.mevenide:netbeans-nbmreload-plugin")) {
                gls[i] = "org.netbeans.plugins:netbeans-nbmreload-plugin:2.0:reload";
                changed = true;
            } else
            if (gls[i].startsWith("org.codehaus.mevenide:netbeans-run-plugin")) {
                gls[i] = "org.netbeans.plugins:netbeans-run-plugin:2.0:reload";
                changed = true;
            } else
            if (gls[i].startsWith("org.codehaus.mevenide:netbeans-debugger-plugin")) {
                gls[i] = "org.netbeans.plugins:netbeans-debugger-plugin:2.0:reload";
                changed = true;
            }
        }
        if (changed) {
            List<String> lst = config.getGoals();
            try {
                lst.clear();
                lst.addAll(Arrays.asList(gls));
            } catch (UnsupportedOperationException x) {
                Exceptions.printStackTrace(x);
            }
        }
        return true;
    }

}
