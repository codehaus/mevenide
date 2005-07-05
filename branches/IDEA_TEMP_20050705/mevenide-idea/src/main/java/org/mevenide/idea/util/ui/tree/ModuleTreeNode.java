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
package org.mevenide.idea.util.ui.tree;

import com.intellij.openapi.module.Module;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Arik
 */
public class ModuleTreeNode extends DefaultMutableTreeNode {
    public ModuleTreeNode(final Module pModule) {
        super(pModule);
    }

    public Module getUserObject() {
        return (Module) super.getUserObject();
    }

    public Module getModule() {
        return getUserObject();
    }

    public String toString() {
        final Module module = getModule();
        if (module == null)
            return null;

        return module.getName();
    }
}
