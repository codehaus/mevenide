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
import java.util.logging.Logger;
import org.netbeans.modules.maven.api.execute.PrerequisitesChecker;
import org.netbeans.modules.maven.api.execute.RunConfig;
import org.openide.util.Exceptions;

/**
 *
 * @author mkleint
 */
public class BackwardCompatibilityWithMevenideChecker implements PrerequisitesChecker {
    private static Logger LOG = Logger.getLogger(BackwardCompatibilityWithMevenideChecker.class.getName());

    public boolean checkRunConfig(RunConfig config) {
        String[] gls = config.getGoals().toArray(new String[0]);
        boolean changed = false;
        for (int i = 0; i < gls.length; i++) {
            if (gls[i].startsWith("org.codehaus.mevenide:netbeans-deploy-plugin")) {
                gls[i] = null;
                config.setProperty("netbeans.deploy", "true");
                changed = true;
                LOG.info("Dynamically removing netbeans-deploy-plugin goal from execution. No longer supported.");
            } else
            if (gls[i].startsWith("org.codehaus.mevenide:netbeans-nbmreload-plugin")) {
                gls[i] = null;
                LOG.info("Dynamically removing netbeans-nbmreload-plugin goal from execution. No longer supported.");
                changed = true;
            } else
            if (gls[i].startsWith("org.codehaus.mevenide:netbeans-run-plugin")) {
                gls[i] = null;
                LOG.info("Dynamically removing netbeans-run-plugin goal from execution. No longer supported.");
                changed = true;
            } else
            if (gls[i].startsWith("org.codehaus.mevenide:netbeans-debugger-plugin")) {
                gls[i] = null;
                LOG.info("Dynamically removing netbeans-debugger-plugin goal from execution. No longer supported.");
                changed = true;
            }
        }
        if (changed) {
            List<String> lst = config.getGoals();
            try {
                lst.clear();
                lst.addAll(Arrays.asList(gls));
                lst.remove(null);
            } catch (UnsupportedOperationException x) {
                Exceptions.printStackTrace(x);
            }
        }
        return true;
    }

}
