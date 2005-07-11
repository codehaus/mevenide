package org.mevenide.idea.util.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;

/**
 * @author Arik
 */
public class RelativeBrowseActionListener<T extends JComponent>
        extends ComponentWithBrowseButton.BrowseFolderActionListener<T> {
    private final VirtualFile referenceDir;
    private final ComponentWithBrowseButton<T> textField;
    private final TextComponentAccessor<T> accessor;

    public RelativeBrowseActionListener(final VirtualFile pReferenceDir,
                                        final String pTitle,
                                        final String pDescription,
                                        final ComponentWithBrowseButton<T> pTextField,
                                        final Project pProject,
                                        final FileChooserDescriptor pFileChooserDesc,
                                        final TextComponentAccessor<T> pAccessor) {
        super(pTitle, pDescription, pTextField, pProject, pFileChooserDesc, pAccessor);
        referenceDir = pReferenceDir;
        textField = pTextField;
        accessor = pAccessor;
    }

    @Override
    protected void onFileChoosen(final VirtualFile pChosenFile) {
        String relativePath = null;

        if (referenceDir == null) {
            super.onFileChoosen(pChosenFile);
            return;
        }

        VirtualFile parent = referenceDir;
        int levels = 0;
        while (parent != null && !VfsUtil.isAncestor(parent, pChosenFile, false)) {
            levels++;
            parent = parent.getParent();
        }

        final StringBuilder buf = new StringBuilder();
        buf.append(StringUtils.repeat("../", levels));
        buf.append(VfsUtil.getRelativePath(pChosenFile, parent, '/'));
        relativePath = buf.toString();

        accessor.setText(textField.getChildComponent(), relativePath);
    }
}
