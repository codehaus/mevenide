package org.mevenide.idea.synchronize.inspections.srcdirs;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.mevenide.idea.Res;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.synchronize.inspections.AbstractModuleInspector;

/**
 * @author Arik
 */
public class SourceCodeDirsInspector extends AbstractModuleInspector {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(SourceCodeDirsInspector.class);

    public SourceCodeDirsInspector() {
        super(RES.get("inspector.name"), RES.get("inspector.desc"));
    }

    public ProblemInfo[] inspect(Module pModule) {
        final ModuleRootManager mgr = ModuleRootManager.getInstance(pModule);
        final VirtualFile[] sourceRoots = mgr.getSourceRoots();

        return new ProblemInfo[0];
    }
}
