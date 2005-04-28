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

    public boolean isLeaf() {
        return false;
    }

    public String toString() {
        final Module module = getModule();
        if (module == null)
            return null;

        return module.getName();
    }
}
