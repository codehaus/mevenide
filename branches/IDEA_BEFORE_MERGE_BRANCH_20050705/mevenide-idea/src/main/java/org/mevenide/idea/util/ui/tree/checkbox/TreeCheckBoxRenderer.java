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
package org.mevenide.idea.util.ui.tree.checkbox;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * @author Arik
 */
public class TreeCheckBoxRenderer extends DefaultTreeCellRenderer {
    private final TreeCheckBoxRenderingComponent renderer = new TreeCheckBoxRenderingComponent();

    public Component getTreeCellRendererComponent(final JTree pTree,
                                                  final Object pValue,
                                                  final boolean pSelected,
                                                  final boolean pExpanded,
                                                  final boolean pLeaf,
                                                  final int pRow,
                                                  final boolean pHasFocus) {
        renderer.configure(pTree,
                           pValue,
                           pSelected,
                           pExpanded,
                           pLeaf,
                           pRow,
                           pHasFocus);
        return renderer;
    }
}