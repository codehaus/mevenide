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
 * Custom line based filter for maven executor output when running the application.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class RunOutputFilter implements OutputProcessor {
    
    public void processLine(String line, OutputVisitor visitor) {
        String ln = line.trim();
        if (ln.startsWith("[java]")) { //NOI18N
            visitor.setFilteredLine(line.substring(line.indexOf("[java]") + "[java]".length())); //NOI18N
        }
    }
}
