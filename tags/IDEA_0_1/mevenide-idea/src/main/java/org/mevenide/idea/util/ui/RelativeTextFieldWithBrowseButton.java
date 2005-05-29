package org.mevenide.idea.util.ui;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.JTextField;
import java.awt.event.ActionListener;

/**
 * @author Arik
 */
public class RelativeTextFieldWithBrowseButton extends TextFieldWithBrowseButton {

    private final VirtualFile referenceDir;

    public RelativeTextFieldWithBrowseButton(final VirtualFile pReferenceDir) {
        referenceDir = pReferenceDir;
    }

    public RelativeTextFieldWithBrowseButton(final ActionListener browseActionListener) {
        super(browseActionListener);
        referenceDir = null;
    }

    @Override public void addBrowseFolderListener(final String title,
                                        final String description,
                                        final Project project,
                                        final FileChooserDescriptor fileChooserDescriptor,
                                        final TextComponentAccessor<JTextField> accessor) {
        addActionListener(new RelativeBrowseActionListener<JTextField>(
                referenceDir,
                title,
                description,
                this,
                project,
                fileChooserDescriptor,
                accessor));
    }
}
