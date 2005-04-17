package org.mevenide.idea.goalstoolwindow;

import com.intellij.openapi.module.Module;
import org.mevenide.idea.util.Res;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Arik
 */
public class ModuleTreeNode extends DefaultMutableTreeNode {
    private static final Res RES = Res.getInstance(ModuleTreeNode.class);

    public ModuleTreeNode(final Module pModule) {
        super(pModule);
    }

    public Module getModule() {
        return (Module) userObject;
    }

    public String toString() {
        final Module module = getModule();
        if(module == null)
            return null;

        return module.getName();
    }

    public boolean isLeaf() {
        return false;
    }

}
