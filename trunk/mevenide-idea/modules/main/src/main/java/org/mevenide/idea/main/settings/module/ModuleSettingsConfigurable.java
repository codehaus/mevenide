package org.mevenide.idea.main.settings.module;

import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.common.util.Res;
import org.mevenide.idea.common.ui.Images;
import org.mevenide.idea.common.ui.UI;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Collection;

/**
 * @author Arik
 */
public class ModuleSettingsConfigurable implements ModuleComponent,
                                                   Configurable {
    private static final Log LOG = LogFactory.getLog(ModuleSettingsConfigurable.class);
    private static final Res RES = Res.getInstance(ModuleSettingsConfigurable.class);

    /**
     * The module this configurable manages.
     */
    private final Module module;

    /**
     * The user interface component.
     */
    private final ModuleSettingsPanel ui = new ModuleSettingsPanel();

    /**
     * Creates an instance that manages the given module.
     *
     * @param pModule the module
     */
    public ModuleSettingsConfigurable(final Module pModule) {
        module = pModule;
    }

    public String getComponentName() {
        return ModuleSettingsConfigurable.class.getName();
    }

    public String getDisplayName() {
        return RES.get("display.name");
    }

    public String getHelpTopic() {
        return null;
    }

    public Icon getIcon() {
        return new ImageIcon(Images.MAVEN_ICON);
    }

    public void initComponent() {
    }

    public void projectOpened() {
    }

    public void moduleAdded() {
    }

    public void apply() throws ConfigurationException {
        try {
            final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
            moduleSettings.setPomFile(ui.getPomFile());
            moduleSettings.setFavoriteGoals(ui.getFavoriteGoals());
        }
        catch (FileNotFoundException e) {
            final ConfigurationException confEx = new ConfigurationException(e.getMessage(), UI.ERR_TITLE);
            throw (ConfigurationException)confEx.initCause(e);
        }
    }

    public JComponent createComponent() {
        return ui;
    }

    public void disposeUIResources() {
    }

    public boolean isModified() {
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);

        final File selectedPomFile = ui.getPomFile();
        final File pomFile = moduleSettings.getPomFile();

        if (selectedPomFile == pomFile)
            return false;

        if (selectedPomFile == null || pomFile == null)
            return true;

        if (!selectedPomFile.equals(pomFile))
            return true;

        final Collection currentFavorites = moduleSettings.getFavoriteGoals();
        final Collection favorites = ui.getFavoriteGoals();

        return !currentFavorites.equals(favorites);
    }

    public void reset() {
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);

        try {
            ui.loadMavenGoals(moduleSettings.getPluginsMap());
        }
        catch (Exception e) {
            Messages.showErrorDialog(module.getProject(), e.getMessage(), UI.ERR_TITLE);
            LOG.error(e.getMessage(), e);
        }

        ui.setFavoriteGoals(moduleSettings.getFavoriteGoals());
        ui.setPomFile(moduleSettings.getPomFile());
    }

    public void projectClosed() {
    }

    public void disposeComponent() {
    }
}
