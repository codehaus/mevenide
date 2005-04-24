package org.mevenide.idea.main.settings.module;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.fileEditor.VetoDocumentReloadException;
import com.intellij.openapi.fileEditor.VetoDocumentSavingException;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.common.ui.UI;

import java.io.File;

/**
 * TODO: how do we track maven poms changing INSIDE idea? (e.g. not from outside)
 * @author Arik
 */
public class ModuleSynchronizer implements ModuleComponent, FileDocumentManagerListener {
    private static final Log LOG = LogFactory.getLog(ModuleSynchronizer.class);

    private final Module module;

    public ModuleSynchronizer(final Module pModule) {
        module = pModule;
    }

    public void disposeComponent() {
        FileDocumentManager.getInstance().removeFileDocumentManagerListener(this);
    }

    public String getComponentName() {
        return ModuleSynchronizer.class.getName();
    }

    public void initComponent() {
        FileDocumentManager.getInstance().addFileDocumentManagerListener(this);
        VirtualFileManager.getInstance().addVirtualFileListener(new PomFileListener());
    }

    public void moduleAdded() {
    }

    public void projectClosed() {
    }

    public void projectOpened() {
    }

    public void beforeDocumentSaving(Document document) throws VetoDocumentSavingException {
    }

    public void fileWithNoDocumentChanged(VirtualFile pFile) {
        if(isPomFile(pFile) || isMavenScriptFile(pFile)) {
            final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
            try {
                moduleSettings.refresh();
            }
            catch (Exception e) {
                Messages.showErrorDialog(module.getProject(),
                                         e.getMessage(),
                                         UI.ERR_TITLE);
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public void beforeFileContentReload(VirtualFile file,
                                        Document document) throws VetoDocumentReloadException {
    }

    public void fileContentReloaded(VirtualFile pFile, Document document) {
    }

    public void fileContentLoaded(VirtualFile pFile, Document document) {
    }

    private boolean isPomFile(final VirtualFile pFile) {
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
        final File pomFile = moduleSettings.getPomFile();
        if (pomFile == null)
            return false;

        final File file = new File(pFile.getPath()).getAbsoluteFile();
        return file.equals(pomFile);
    }

    private boolean isMavenScriptFile(final VirtualFile pFile) {
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
        final File pomFile = moduleSettings.getPomFile();
        if (pomFile == null)
            return false;

        if (!pFile.getName().equalsIgnoreCase("maven.xml"))
            return false;

        final File dir = new File(pFile.getParent().getPath()).getAbsoluteFile();
        return dir.equals(pomFile.getParentFile());
    }

    private class PomFileListener extends VirtualFileAdapter {

        public void contentsChanged(VirtualFileEvent event) {
            try {
                final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
                final VirtualFile file = event.getFile();
                if (isPomFile(file) || isMavenScriptFile(file))
                    moduleSettings.refresh();
            }
            catch (Exception e) {
                Messages.showErrorDialog(module.getProject(),
                                         e.getMessage(),
                                         UI.ERR_TITLE);
                LOG.error(e.getMessage(), e);
            }
        }

        public void fileDeleted(VirtualFileEvent event) {
            try {
                final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
                final VirtualFile file = event.getFile();
                if (isPomFile(file)) {
                    moduleSettings.setPomFile(null);
                    return;
                }

                if (isMavenScriptFile(file))
                    moduleSettings.refresh();
            }
            catch (Exception e) {
                Messages.showErrorDialog(module.getProject(),
                                         e.getMessage(),
                                         UI.ERR_TITLE);
                LOG.error(e.getMessage(), e);
            }
        }

        public void fileMoved(VirtualFileMoveEvent event) {
            try {
                final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
                final VirtualFile file = event.getFile();
                if (isPomFile(file))
                    moduleSettings.setPomFile(new File(file.getPath()));
                else if (isMavenScriptFile(file))
                    moduleSettings.refresh();
            }
            catch (Exception e) {
                Messages.showErrorDialog(module.getProject(),
                                         e.getMessage(),
                                         UI.ERR_TITLE);
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
