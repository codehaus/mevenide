package org.mevenide.idea.project;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.project.support.AbstractPomSettingsManager;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.impl.DefaultPsiProject;
import org.mevenide.idea.psi.util.PsiUtils;

/**
 * @author Arik
 */
public class PomModelManager extends AbstractPomSettingsManager {
    private static final Key<PsiProject> KEY = Key.create(PsiProject.class.getName());

    /**
     * Creates an instance for the given project.
     *
     * @param pProject the project this component instance will be registered for
     */
    public PomModelManager(final Project pProject) {
        super(pProject);
    }

    public PsiProject getPsiProject(final String pUrl) {
        if (!isValid(pUrl))
            return null;

        boolean recreate = true;
        PsiProject psi = get(KEY, pUrl);

        if (psi != null) {
            final XmlFile xmlFile = psi.getXmlFile();
            final VirtualFile psiFile = xmlFile.getVirtualFile();
            if (psiFile != null) {
                final String psiUrl = psiFile.getUrl();
                recreate = !pUrl.equals(psiUrl);
            }
        }

        if (recreate) {
            final VirtualFile file = getFile(pUrl);
            assert file != null;

            final XmlFile xmlFile = PsiUtils.findXmlFile(project, file);
            assert xmlFile != null;

            psi = new DefaultPsiProject(xmlFile);
        }

        return psi;
    }

    public static PomModelManager getInstance(final Project pProject) {
        return pProject.getComponent(PomModelManager.class);
    }
}
