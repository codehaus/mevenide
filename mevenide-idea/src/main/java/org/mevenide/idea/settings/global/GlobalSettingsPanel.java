package org.mevenide.idea.settings.global;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.mevenide.idea.util.Res;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

/**
 * The global settings panel. Allows Maven home selection.
 *
 * @author Arik
 */
public class GlobalSettingsPanel extends JPanel {
    /**
     * Used for resource loading.
     */
    private static final Res RES = Res.getInstance(GlobalSettingsPanel.class);

    /**
     * The text field for selecting (or browsing) the Maven home.
     */
    private final TextFieldWithBrowseButton mavenHomeField = new TextFieldWithBrowseButton();

    /**
     * Creates an instance.
     */
    public GlobalSettingsPanel() {
        init();
    }

    /**
     * Creates an instance using (or not using) double buffering.
     *
     * @param isDoubleBuffered whether to use double buffering or not
     */
    public GlobalSettingsPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        init();
    }

    /**
     * Initializes the panel by creating the required components and laying them out on the panel.
     *
     * <p>If overriding, make sure you call this super method first.</p>
     */
    protected void init() {
        GridBagConstraints c;


        setLayout(new GridBagLayout());

        //Add maven home label
        add(new JLabel(RES.get("maven.home.label")), new GridBagConstraints());

        //add maven home field
        mavenHomeField.addBrowseFolderListener(RES.get("choose.maven.home"),
                                               RES.get("choose.maven.home.desc"),
                                               null,
                                               new MavenHomeFileChooser());
        c = new GridBagConstraints();
        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        add(mavenHomeField, c);
    }

    public void setMavenHome(final File pMavenHome) {
        mavenHomeField.setText(pMavenHome == null ? null : pMavenHome.getAbsolutePath());
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

    private class MavenHomeFileChooser extends FileChooserDescriptor {
        public MavenHomeFileChooser() {
            super(false,   //prevent file-selection
                  true,    //allow folder-selection
                  false,   //prevent jar selection
                  false,   //prevent jar file selection
                  false,   //prevent jar content selection
                  false    //prevent multiple selection
            );
        }
    }
}
