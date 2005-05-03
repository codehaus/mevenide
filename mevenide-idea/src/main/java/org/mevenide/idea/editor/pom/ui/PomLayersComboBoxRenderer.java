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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.Project;

import javax.swing.*;
import java.awt.Component;

/**
 * @author Arik
 */
public class PomLayersComboBoxRenderer extends DefaultListCellRenderer {

    @Override public Component getListCellRendererComponent(final JList pCombo,
                                                            final Object pLayer,
                                                            final int pIndex,
                                                            final boolean pSelected,
                                                            final boolean pCellHasFocus) {
        final String text;

        //
        //we expect that the actual value is a Maven Project - that's what the model is
        //supposed to use. Otherwise, we gracefuly fail to the default implementation.
        //
        if (pLayer instanceof Project) {
            final Project layer = (Project) pLayer;
            text = StringUtils.repeat("\t", pIndex) + layer.getId();
        }
        else
            text = pLayer == null ? "" : pLayer.toString();

        //
        //activate super method, to actually configure the visual component
        //
        return super.getListCellRendererComponent(pCombo, text, pIndex, pSelected, pCellHasFocus);
    }
}
