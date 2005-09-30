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
package org.mevenide.netbeans.api.output;

import java.util.regex.Pattern;



/**
 * abstract implementation of OutputProcessor that has support to focus the processor 
 * on certain subsections of the output only.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public abstract class AbstractOutputProcessor implements OutputProcessor {
    private boolean isInWatchedGoals = false;
    /** Creates a new instance of AbstractOutputListenerProvider */
    private Pattern pattern1;
    private Pattern pattern2;
    protected AbstractOutputProcessor() {
        pattern1 = Pattern.compile("[-\\p{Alnum}]+\\:[-\\p{Alnum}]+\\:"); //NOI18N
        pattern2 = Pattern.compile("[-\\p{Alnum}]+\\:"); //NOI18N
    }
    
    /**
     * watches for important goal (as specified by <code>getWatchedGoals()</code>)
     * and returns true when processing a line for the goal's section.
     * helps the subclass to focus on processing just the important section of output.
     */
    public final boolean isInWatchedGoals(String line) {
        if (   pattern1.matcher(line).matches() 
            || pattern2.matcher(line).matches()) 
        {
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
    
    /**
     * check if the current line matches any of the goals returned
     * by the getWatchedGoals() method.
     */
    public final boolean isWatchedGoalLine(String line) {
        if (   pattern1.matcher(line).matches() 
            || pattern2.matcher(line).matches()) 
        {
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
    public abstract String[] getWatchedGoals();
}
