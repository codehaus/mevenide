/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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

package org.mevenide.project;

import org.mevenide.util.StringUtils;

/**
 * Maven itself has no class corresponding to a report, so this is a utility
 * wrapper for the simple string used.
 * 
 * @author jbonevic
 * @version $Id$
 */
public class Report {

    private String name;
    
    public Report(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public String toString() {
        return (!StringUtils.isNull(name) ? name : "unknown");
    }
    
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (! (obj instanceof Report) ) return false;
        Report that = (Report) obj;
        if (StringUtils.isNull(this.name) || StringUtils.isNull(that.name)) return false;
        return this.name.equals(that.name);
    }
    
    public int hashCode() {
        return (this.name != null ? this.name.hashCode() : super.hashCode());
    }

}
