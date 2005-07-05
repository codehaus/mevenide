package org.mevenide.idea.global;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import javax.swing.*;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;

/**
 * @author Arik
 */
public class MavenManagerPanel extends JPanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(MavenManagerPanel.class);

    /**
     * The text field for selecting (or browsing) the Maven home.
     */
    private final TextFieldWithBrowseButton mavenHomeField = new TextFieldWithBrowseButton();

    /**
     * JVM command line options for Maven processes.
     */
    private final JTextField mavenOptionsField = new JTextField();

    /**
     * The online/offline mode field.
     */
    private final JCheckBox offlineCheckBox = new JCheckBox(RES.get("offline.mode.label"));

    /**
     * Creates an instance.
     */
    public MavenManagerPanel() {
        //
        //configure the "..." browse button
        //
        mavenHomeField.addBrowseFolderListener(
                RES.get("choose.maven.home.dlg.title"),
                RES.get("choose.maven.home.dlg.desc"),
                null,
                FileChooserDescriptorFactory.createSingleFolderDescriptor());

        //
        //layout components
        //
        final String cols = "right:min, 2dlu, fill:min:grow";
        final FormLayout layout = new FormLayout(cols);
        final DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
        builder.setComponentFactory(CustomFormsComponentFactory.getInstance());

        builder.append(RES.get("maven.home.label"), mavenHomeField);
        builder.append(RES.get("maven.options.label"), mavenOptionsField);
        builder.append(" ", offlineCheckBox);
    }

    public String getMavenHome() {
        final String text = mavenHomeField.getText();
        if (text == null || text.trim().length() == 0)
            return null;
        else
            return text;
    }

    public void setMavenHome(final String pMavenHome) {
        mavenHomeField.setText(pMavenHome);
    }

    public String getMavenOptions() {
        return mavenOptionsField.getText();
    }

    public void setMavenOptions(final String pMavenOptions) {
        mavenOptionsField.setText(pMavenOptions);
    }

    public boolean isOffline() {
        return offlineCheckBox.isSelected();
    }

    public void setOffline(final boolean pOffline) {
        offlineCheckBox.setSelected(pOffline);
    }
}