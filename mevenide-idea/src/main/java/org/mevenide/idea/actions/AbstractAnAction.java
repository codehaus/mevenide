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
package org.mevenide.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;

/**
 * @author Arik
 */
public abstract class AbstractAnAction extends AnAction {
    protected final Log LOG;

    protected AbstractAnAction() {
        LOG = createLog();
    }

    protected AbstractAnAction(final String pText) {
        super(pText);
        LOG = createLog();
    }

    protected AbstractAnAction(final String pText,
                               final String pDescription,
                               final Icon pIcon) {
        super(pText, pDescription, pIcon);
        LOG = createLog();
    }

    private Log createLog() {
        return LogFactory.getLog(this.getClass());
    }

    protected Project getProject(final AnActionEvent pEvent) {
        return (Project) pEvent.getDataContext().getData(DataConstants.PROJECT);
    }
}
