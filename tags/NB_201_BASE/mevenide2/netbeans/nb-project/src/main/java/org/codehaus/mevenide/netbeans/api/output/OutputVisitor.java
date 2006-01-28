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

package org.codehaus.mevenide.netbeans.api.output;

import javax.swing.Action;
import org.openide.windows.OutputListener;

/**
 * Is collecting line parsing information from all the registered Outputprocessors.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class OutputVisitor {
    
    private OutputListener outputListener;
    private Action successAction;
    private boolean important;
    private String line;
    
    /**
     * property for success Action. Holds question text.
     */
    public static final String ACTION_QUESTION = "Question";
    /**
     * property for success Action. Priority of the action.
     * From all collected actions one is used (the one with highest
     * priority).
     */
    public static final String ACTION_PRIORITY = "Priority";
    
    /** Creates a new instance of OutputVisitor */
    public OutputVisitor() {
    }

    /**
     * not to be called by the OutputProcessors.
     */
    public void resetVisitor() {
        outputListener = null;
        successAction = null;
        important = false;
        line = null;
    }
    

    public OutputListener getOutputListener() {
        return outputListener;
    }

    /**
     * add output line highlight and hyperlink via 
     * <code>org.openide.windows.OutputListener</code> instance.
     */
    public void setOutputListener(OutputListener listener) {
        outputListener = listener;
    }
    /**
     * add output line highlight and hyperlink via 
     * <code>org.openide.windows.OutputListener</code> instance.
     * @param isImportant mark the line as important (useful in Nb 4.1 only)
     */
    public void setOutputListener(OutputListener listener, boolean isImportant) {
        setOutputListener(listener);
        important = isImportant;
    }
    
    /**
     * at least one of the <code>OutputProcessor</code>s added a <code>OutputListener</code> and
     * marked it as important.
     */
    public boolean isImportant() {
        return important;
    }

    public Action getSuccessAction() {
        return successAction;
    }

    /**
     * add an action that should be performed when the build finishes.
     * Only one action will be performed, if more than one success actions are 
     * collected during processing, the one with highest value of property
     * ACTION_PRIORITY is performed. 
     * Another property used is ACTION_QUESTION which 
     * holds text for Yes/No question. If user confirms, it's performed.
     */
    public void setSuccessAction(Action sAction) {
        successAction = sAction;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    
    
}
