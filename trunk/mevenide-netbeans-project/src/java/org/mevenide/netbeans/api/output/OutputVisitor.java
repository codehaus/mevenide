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

import javax.swing.Action;
import org.openide.windows.OutputListener;

/**
 * Is collecting line parsing information from all the registered Outputprocessors.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class OutputVisitor {
    
    private String filteredLine;
    private OutputListener outputListener;
    private Action successAction;
    private boolean important;
    
    /** Creates a new instance of OutputVisitor */
    public OutputVisitor() {
    }

    public void resetVisitor() {
        filteredLine = null;
        outputListener = null;
        successAction = null;
        important = false;
    }
    
    public String getFilteredLine() {
        return filteredLine;
    }

    public void setFilteredLine(String filteredLine) {
        this.filteredLine = filteredLine;
    }

    public OutputListener getOutputListener() {
        return outputListener;
    }

    public void setOutputListener(OutputListener outputListener) {
        this.outputListener = outputListener;
    }
    
    public void setOutputListener(OutputListener outputListener, boolean impor) {
        setOutputListener(outputListener);
        important = impor;
    }
    
    public boolean isImportant() {
        return important;
    }

    public Action getSuccessAction() {
        return successAction;
    }

    public void setSuccessAction(Action sAction) {
        successAction = sAction;
    }

    
    
}
