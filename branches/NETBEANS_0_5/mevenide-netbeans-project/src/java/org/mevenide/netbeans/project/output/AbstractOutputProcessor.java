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
package org.mevenide.netbeans.project.output;

/**
 * abstract implementation of OutputProcessor that has support to focus the processor on certain subsections of the output only.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public abstract class AbstractOutputProcessor implements OutputProcessor {
    private boolean isInWatchedGoals = false;
    /** Creates a new instance of AbstractOutputListenerProvider */
    protected AbstractOutputProcessor() {
    }
    
    final boolean isInWatchedGoals(String line) {
        if (line.matches("[-\\p{Alnum}]+\\:[-\\p{Alnum}]+\\:") ||
            line.matches("[-\\p{Alnum}]+\\:")) {
            String[] goals = getWatchedGoals();
            boolean changed = false;
            for (int i = 0; i < goals.length; i++) {
                if (line.equals(goals[i])) {
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
    
    final boolean isWatchedGoalLine(String line) {
        if (line.matches("[-\\p{Alnum}]+\\:[-\\p{Alnum}]+\\:") ||
            line.matches("[-\\p{Alnum}]+\\:")) {
            String[] goals = getWatchedGoals();
            for (int i = 0; i < goals.length; i++) {
                if (line.equals(goals[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * provide a list of goal pattern that shall be checked. eg. java:compile: pmd:report: etc.
     */
    protected abstract String[] getWatchedGoals();
}
