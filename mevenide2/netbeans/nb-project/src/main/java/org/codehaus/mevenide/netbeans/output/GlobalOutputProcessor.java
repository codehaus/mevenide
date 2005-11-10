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
package org.codehaus.mevenide.netbeans.output;

import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;

/**
 * processing start, end and steps of build process
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class GlobalOutputProcessor implements OutputProcessor {
    
    /** Creates a new instance of GlobalOutputProcessor */
    public GlobalOutputProcessor() {
    }

    public String[] getRegisteredOutputSequences() {
        return new String[] {"project-execute"};
    }

    public void processLine(String line, OutputVisitor visitor) {
    }

    public void sequenceStart(String sequenceId, OutputVisitor visitor) {
        if (sequenceId.startsWith("project-execute")) {
            visitor.setLine(sequenceId);
        } else {
            visitor.setLine("[" + sequenceId.substring("mojo-execute#".length()) + "]");
        }
    }

    public void sequenceEnd(String sequenceId, OutputVisitor visitor) {
        if (sequenceId.startsWith("project-execute")) {
            visitor.setLine("-------------------------------------------------------" +
                            "\nBUILD SUCCESSFUL\n" +
                            "-------------------------------------------------------");
        }
    }

    public void sequenceFail(String sequenceId, OutputVisitor visitor) {
        if (sequenceId.startsWith("project-execute")) {
            visitor.setLine("-------------------------------------------------------" +
                            "\nBUILD FAILED\n" +
                            "-------------------------------------------------------");
        }
    }
    
}
