package org.mevenide.idea.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.GoalGrabbingException;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.idea.util.ui.UIUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

/**
 * A module component that handles user interface for the module. Acquires data
 * from various module components into a user interface panel and displays to
 * the user. Applies the data back if user chooses to.
 *
 * @author Arik
 */
public class ModuleSettingsConfigurable implements ModuleComponent,
                                                   Configurable {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(ModuleSettingsConfigurable.class);

    /**
     * Resources.
     */
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
        return Icons.MAVEN;
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

            final ModuleGoalsProvider provider = ModuleGoalsProvider.getInstance(module);
            provider.setFavoriteGoals(ui.getFavoriteGoals());
        }
        catch (FileNotFoundException e) {
            final ConfigurationException confEx = new ConfigurationException(e.getMessage(), UIUtils.ERROR_TITLE);
            throw (ConfigurationException) confEx.initCause(e);
        }
        catch (GoalGrabbingException e) {
            final ConfigurationException confEx =
                    new ConfigurationException(e.getMessage(), UIUtils.ERROR_TITLE);
            throw (ConfigurationException) confEx.initCause(e);
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

        final ModuleGoalsProvider provider = ModuleGoalsProvider.getInstance(module);
        final Collection currentFavorites = provider.getFavoriteGoals();
        final Collection favorites = ui.getFavoriteGoals();

        return !currentFavorites.equals(favorites);
    }

    public void reset() {
        try {
            final ModuleGoalsProvider provider = ModuleGoalsProvider.getInstance(module);
            ui.loadMavenGoals(provider.getFullGoalsProvider());
        }
        catch (Exception e) {
            UIUtils.showError(module, e);
            LOG.error(e.getMessage(), e);
        }

        final ModuleGoalsProvider provider = ModuleGoalsProvider.getInstance(module);
        ui.setFavoriteGoals(provider.getFavoriteGoals());

        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
        ui.setPomFile(moduleSettings.getPomFile());
    }

    public void projectClosed() {
    }

    public void disposeComponent() {
    }
}
