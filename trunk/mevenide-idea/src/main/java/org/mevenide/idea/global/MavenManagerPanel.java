package org.mevenide.idea.global;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.io.File;

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
        initComponents();
    }

    /**
     * Creates an instance using (or not using) double buffering.
     *
     * @param isDoubleBuffered whether to use double buffering or not
     */
    public MavenManagerPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        initComponents();
        layoutComponents();
    }

    /**
     * Initializes the panel by creating the required components and laying them out on the
     * panel.
     */
    private void initComponents() {
        mavenHomeField.addBrowseFolderListener(
                RES.get("choose.maven.home"),
                RES.get("choose.maven.home.desc"),
                null,
                FileChooserDescriptorFactory.createSingleFolderDescriptor());
    }

    private void layoutComponents() {
        final String cols = "right:min, 2dlu, fill:min:grow";
        final FormLayout layout = new FormLayout(cols);
        final DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
        builder.setComponentFactory(CustomFormsComponentFactory.getInstance());

        builder.append(RES.get("maven.home.label"), mavenHomeField);
        builder.append(RES.get("maven.options.label"), mavenOptionsField);
        builder.append(" ", offlineCheckBox);
    }

    public void readOptions(final MavenManager pManager) {
        final File mavenHome = pManager.getMavenHome();

        mavenHomeField.setText(mavenHome == null ? null : mavenHome.getAbsolutePath());
        mavenOptionsField.setText(pManager.getMavenOptions());
        offlineCheckBox.setSelected(pManager.isOffline());
    }

    public File getMavenHome() {
        final String text = mavenHomeField.getText();
        if (text == null)
            return null;
        else if (text.trim().length() == 0)
            return null;
        else
            return new File(text).getAbsoluteFile();
    }

    public String getMavenOptions() {
        return mavenOptionsField.getText();
    }

    public boolean isOffline() {
        return offlineCheckBox.isSelected();
    }
}