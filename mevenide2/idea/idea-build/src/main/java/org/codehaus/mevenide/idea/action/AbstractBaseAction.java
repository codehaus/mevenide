/* ==========================================================================
 * Copyright 2006 Mevenide Team
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



package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.AnAction;

import org.codehaus.mevenide.idea.helper.ActionContext;

import javax.swing.*;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public abstract class AbstractBaseAction extends AnAction {

    /**
     * Field description
     */
    protected ActionContext actionContext;

    /**
     * Constructs ...
     */
    protected AbstractBaseAction() {
        super();
    }

    /**
     * Constructs ...
     *
     * @param text        Document me!
     * @param description Document me!
     * @param icon        Document me!
     */
    protected AbstractBaseAction(String text, String description, Icon icon) {
        super(text, description, icon);
    }
}
