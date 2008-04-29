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



package org.codehaus.mevenide.idea.util;

import org.apache.log4j.Logger;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class CommonUtils {
    private static final Logger LOG = Logger.getLogger(CommonUtils.class);

    public static int destroyProcess(Process process) {
        if (!isProcessTerminated(process)) {
            process.destroy();

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                LOG.error(e);
            }
        }

        return (process != null)
               ? process.exitValue()
               : 0;
    }

    private static boolean isProcessTerminated(Process process) {
        if (process == null) {
            return true;
        }

        try {
            process.exitValue();
        } catch (IllegalThreadStateException e) {
            return false;
        }

        return true;
    }
}
