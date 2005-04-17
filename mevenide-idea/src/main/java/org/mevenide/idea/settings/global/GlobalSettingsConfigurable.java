package org.mevenide.idea.settings.global;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.mevenide.idea.support.ui.UIConstants;
import org.mevenide.idea.util.Res;
import org.mevenide.idea.util.images.Images;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Arik
 */
public class GlobalSettingsConfigurable implements ApplicationComponent,
                                                   Configurable {

    /**
     * The name of this configurable.
     */
    private static final String NAME = GlobalSettingsConfigurable.class.getName();

    /**
     * Used for resource loading.
     */
    private static final Res RES = Res.getInstance(GlobalSettingsConfigurable.class);

    /**
     * The user interface component.
     */
    private GlobalSettingsPanel ui = new GlobalSettingsPanel();

    public String getComponentName() {
        return NAME;
    }

    public String getDisplayName() {
        return RES.get("global.settings.display.name");
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
            throw new ConfigurationException(e.getMessage(), UIConstants.ERROR_TITLE);
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

    public static GlobalSettingsConfigurable  getInstance() {
        return (GlobalSettingsConfigurable) ApplicationManager.getApplication().getComponent(NAME);
    }
}
