package org.mevenide.idea.psi;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.impl.DefaultPsiProject;
import org.mevenide.idea.psi.util.PsiUtils;
import org.mevenide.idea.util.components.AbstractModuleComponent;

/**
 * @author Arik
 */
public class PsiPomManager extends AbstractModuleComponent {
    private PsiProject psiProject;

    public PsiPomManager(final Module pModule) {
        super(pModule);
    }

    @Override
    public void moduleAdded() {
        final ModuleSettings settings = ModuleSettings.getInstance(module);
        final VirtualFile pomVirtualFile = settings.getPomVirtualFile();
        psiProject = new DefaultPsiProject(PsiUtils.findXmlFile(module, pomVirtualFile));
    }

    public PsiProject getPsiProject() {
        return psiProject;
    }

    /**
     * Returns the PSI-POM manager for the specified module.
     *
     * @param pModule the module to retrieve the component for
     *
     * @return instance
     */
    public static PsiPomManager getInstance(final Module pModule) {
        return pModule.getComponent(PsiPomManager.class);
    }
}
