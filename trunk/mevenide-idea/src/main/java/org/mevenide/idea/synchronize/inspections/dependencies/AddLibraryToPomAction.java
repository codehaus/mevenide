package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.psi.util.PsiUtils;
import org.mevenide.idea.psi.util.XmlTagPath;
import org.mevenide.idea.synchronize.AbstractFixAction;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class AddLibraryToPomAction extends AbstractFixAction {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(AddLibraryToPomAction.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(AddDependencyToIdeaAction.class);

    private final Runnable dependencyAdder = new AddToPomRunnable();
    private final Module module;
    private final VirtualFile libraryFile;

    public AddLibraryToPomAction(final ProblemInfo pProblem,
                                 final Module pModule,
                                 final VirtualFile pLibraryFile) {
        super(RES.get("add.lib2pom.action.name", pLibraryFile.getPath()),
              RES.get("add.lib2pom.action.desc",
                      pLibraryFile.getPath(),
                      pModule.getName()),
              Icons.FIX_PROBLEMS,
              pProblem);
        module = pModule;
        libraryFile = pLibraryFile;
    }

    public void actionPerformed(AnActionEvent e) {
        IDEUtils.runCommand(module, dependencyAdder);
    }

    private class AddToPomRunnable implements Runnable {
        /**
         * @todo would be nice to show a dialog to let the user verify the new dependency
         * details
         */
        public void run() {
            final VirtualFile typeDir = libraryFile.getParent();
            final VirtualFile groupDir = typeDir.getParent();

            final String groupId = groupDir.getName();
            final String artifactId;
            final String version;

            String type = typeDir.getName();
            if (type.endsWith("s"))
                type = type.substring(0, type.length() - 1);

            final String fileName = libraryFile.getName();
            final int hyphenIndex = fileName.lastIndexOf('-');
            if (hyphenIndex < 0) {
                UIUtils.showError(module,
                                  "Could not derive dependency details from file '" + fileName + "' - please enter the dependency manually.");
                return;
            }
            final int dotIndex = fileName.lastIndexOf('.');
            if (hyphenIndex < 0) {
                UIUtils.showError(module,
                                  "Could not derive dependency details from file '" + fileName + "' - please enter the dependency manually.");
                return;
            }
            artifactId = fileName.substring(0, hyphenIndex);
            version = fileName.substring(hyphenIndex + 1, dotIndex);

            final ModuleSettings settings = ModuleSettings.getInstance(module);
            final VirtualFile pomFile = settings.getPomVirtualFile();
            final Document pomDoc = FileDocumentManager.getInstance().getDocument(pomFile);
            final XmlFile xmlFile = PsiUtils.findXmlFile(module, pomDoc);
            final XmlTagPath path = new XmlTagPath(xmlFile, "project/dependencies");

            try {
                final XmlTag depsTag = path.ensureTag();
                final XmlTag childTag = depsTag.createChildTag("dependency",
                                                               depsTag.getNamespace(),
                                                               null,
                                                               false);
                final XmlTag depTag = (XmlTag) depsTag.add(childTag);

                if (groupId != null && groupId.trim().length() > 0)
                    PsiUtils.setTagValue(module, depTag, "groupId", groupId);
                if (artifactId != null && artifactId.trim().length() > 0)
                    PsiUtils.setTagValue(module, depTag, "artifactId", artifactId);
                if (version != null && version.trim().length() > 0)
                    PsiUtils.setTagValue(module, depTag, "version", version);
                if (type != null && type.trim().length() > 0)
                    PsiUtils.setTagValue(module, depTag, "type", type);
            }
            catch (IncorrectOperationException e) {
                LOG.error(e.getMessage(), e);
                UIUtils.showError(module, e);
            }
        }
    }
}
