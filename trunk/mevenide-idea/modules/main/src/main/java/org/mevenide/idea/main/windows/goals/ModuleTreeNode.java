package org.mevenide.idea.main.windows.goals;

import com.intellij.openapi.module.Module;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Arik
 */
public class ModuleTreeNode extends DefaultMutableTreeNode {

    public ModuleTreeNode(final Module pModule) {
        super(pModule);
    }

    public Module getModule() {
        return (Module) userObject;
    }

    public String toString() {
        final Module module = getModule();
        if (module == null)
            return null;

        return module.getName();
    }

    public boolean isLeaf() {
        return false;
    }

}