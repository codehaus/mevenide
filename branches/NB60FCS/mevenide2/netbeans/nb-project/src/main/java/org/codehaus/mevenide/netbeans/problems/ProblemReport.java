/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

package org.codehaus.mevenide.netbeans.problems;

import javax.swing.Action;

/**
 *
 * @author mkleint
 */
public final class ProblemReport {
    
    public static final int SEVERITY_LOW = 10;
    public static final int SEVERITY_MEDIUM = 5;
    public static final int SEVERITY_HIGH = 0;

    private int level = SEVERITY_LOW;
    private String shortDesc;
    private String longDesc;
    private Action action;
    
    public ProblemReport(int severity, String desc, String longDesc, Action correct) {
        level = severity;
        shortDesc = desc;
        this.longDesc = longDesc;
        action = correct;
    }
    
    public String getShortDescription() {
        return shortDesc;
    }
    
    public String getLongDescription() {
        return longDesc;
    }
    
    public Action getCorrectiveAction() {
        return action;
    }
    
    public int getSeverityLevel() {
        return level;
    }
    
}
