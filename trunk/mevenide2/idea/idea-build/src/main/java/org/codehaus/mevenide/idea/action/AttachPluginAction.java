package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import org.codehaus.mevenide.idea.common.util.ErrorHandler;
import org.codehaus.mevenide.idea.component.PomTreeStructure;
import org.codehaus.mevenide.idea.gui.PomTreeView;
import org.codehaus.mevenide.idea.model.MavenPluginDocument;
import org.codehaus.mevenide.idea.model.ModelUtils;

import java.util.Collection;

public class AttachPluginAction extends PomFileAction {
    public AttachPluginAction() {
        super(true);
    }

    protected void actOnPoms(PomTreeView pomTreeView, Collection<PomTreeStructure.PomNode> selectedNodes) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, false, true, true, false, true);
        VirtualFile[] pluginFiles = FileChooser.chooseFiles(pomTreeView.getProject(), descriptor);

        for (VirtualFile pluginFile : pluginFiles) {
            try {
                MavenPluginDocument mavenPluginDocument = ModelUtils.createMavenPluginDocument(pluginFile.getPath(), true);
                if (mavenPluginDocument != null) {
                    for (PomTreeStructure.PomNode pomNode : selectedNodes) {
                        pomNode.attachPlugin(mavenPluginDocument);
                    }
                }
            } catch (Exception e) {
                ErrorHandler.processAndShowError(pomTreeView.getProject(), e);
            }
        }
    }
}
