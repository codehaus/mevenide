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
package org.mevenide.idea.editor.pom.ui;

import org.apache.maven.project.Project;
import org.mevenide.context.IQueryContext;

import javax.swing.*;

/**
 * @author Arik
 */
public class PomLayersComboBoxModel extends DefaultComboBoxModel {

    private final IQueryContext queryContext;

    public PomLayersComboBoxModel(final IQueryContext pQueryContext) {
        super();

        queryContext = pQueryContext;
        refresh();
    }

    public Project getSelectedItem() {
        return (Project) super.getSelectedItem();
    }

    public Project getElementAt(int index) {
        return (Project) super.getElementAt(index);
    }

    public void refresh() {
        final Project[] layers = queryContext.getPOMContext().getProjectLayers();
        for (Project layer : layers)
            addElement(layer);
    }
}
