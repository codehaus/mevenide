package org.mevenide.idea.main.settings.global;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.mevenide.idea.common.ui.UI;
import org.mevenide.idea.common.ui.Images;
import org.mevenide.idea.common.util.Res;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Arik
 */
public class GlobalSettingsConfigurable implements ApplicationComponent, Configurable {
    /**
     * Used for resource loading.
     */
    private static final Res RES = Res.getInstance(GlobalSettingsConfigurable.class);

    /**
     * The user interface component.
     */
    private GlobalSettingsPanel ui = new GlobalSettingsPanel();

    public String getComponentName() {
        return GlobalSettingsConfigurable.class.getName();
    }

    public String getDisplayName() {
        return RES.get("configurable.display.name");
    }

    public void initComponent() {
    }

    public Icon getIcon() {
        return new ImageIcon(Images.MAVEN_ICON);
    }

    public String getHelpTopic() {
        return null;
    }

    public void apply() throws ConfigurationException {
        try {
            GlobalSettings.getInstance().setMavenHome(ui.getMavenHome());
        }
        catch (FileNotFoundException e) {
            throw new ConfigurationException(e.getMessage(), UI.ERR_TITLE);
        }
    }

    public JComponent createComponent() {
        return ui;
    }

    public void disposeUIResources() {
    }

    public boolean isModified() {
        final File selectedHome = ui.getMavenHome();
        final File mavenHome = GlobalSettings.getInstance().getMavenHome();

        if (selectedHome == mavenHome)
            return false;

        if (selectedHome == null || mavenHome == null)
            return true;

        return !selectedHome.equals(mavenHome);
    }

    public void reset() {
        ui.setMavenHome(GlobalSettings.getInstance().getMavenHome());
    }

    public void disposeComponent() {
    }
}
