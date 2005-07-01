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
package org.mevenide.idea.module;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import org.mevenide.idea.execute.MavenRunner;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class GoalAction extends AbstractAnAction {
    private final String fqGoalName;
    private final Module module;

    public GoalAction(final Module pModule,
                      final String pFullyQualifiedName,
                      final String pGoalDescription) {
        super(pFullyQualifiedName + " for module " + pModule.getName(),
              pGoalDescription,
              Icons.GOAL);

        fqGoalName = pFullyQualifiedName;
        module = pModule;
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        MavenRunner.execute(module,
                            new String[]{fqGoalName},
                            pEvent.getDataContext());
    }
}
