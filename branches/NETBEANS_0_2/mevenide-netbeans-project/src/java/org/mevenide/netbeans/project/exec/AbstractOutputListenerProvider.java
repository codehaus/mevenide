/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.netbeans.project.exec;

import java.util.Arrays;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public abstract class AbstractOutputListenerProvider implements OutputListenerProvider {
    private boolean isInWatchedGoals = false;
    /** Creates a new instance of AbstractOutputListenerProvider */
    protected AbstractOutputListenerProvider() {
    }
    
    final boolean isInWatchedGoals(String line) {
        if (line.matches("\\p{Alnum}+\\:\\p{Alnum}+\\:")) {
            String[] goals = getWatchedGoals();
            boolean changed = false;
            for (int i = 0; i < goals.length; i++) {
                if (line.startsWith(goals[i])) {
                    isInWatchedGoals = true;
                    changed = true;
                    break;
                }
            }
            if (!changed) {
                isInWatchedGoals = false;
            }
        } 
        return isInWatchedGoals;
    }
    
    
    protected abstract String[] getWatchedGoals();
}
