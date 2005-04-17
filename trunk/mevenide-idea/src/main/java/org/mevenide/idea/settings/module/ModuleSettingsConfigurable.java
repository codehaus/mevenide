package org.mevenide.idea.settings.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.support.ui.UIConstants;
import org.mevenide.idea.util.Res;
import org.mevenide.idea.util.images.Images;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

/**
 * @author Arik
 */
public class ModuleSettingsConfigurable implements ModuleComponent,
                                                   Configurable {
    private static final Log LOG = LogFactory.getLog(ModuleSettingsConfigurable.class);

    /**
     * The name of this configurable.
     */
    private static final String NAME = ModuleSettingsConfigurable.class.getName();

    /**
     * Used for resource loading.
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

    public void moduleAdded() {
    }

    public void projectClosed() {
    }

    public void projectOpened() {
    }

    public void disposeComponent() {
    }

    public String getComponentName() {
        return NAME;
    }

    public void initComponent() {
    }

    public String getDisplayName() {
        return RES.get("module.settings.display.name");
    }

    public String getHelpTopic() {
        return null;
    }

    public Icon getIcon() {
        return new ImageIcon(Images.MAVEN_ICON);
    }

    public void apply() throws ConfigurationException {
        try {
            final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
            moduleSettings.setPomFile(ui.getPomFile());
            moduleSettings.setFavoriteGoals(ui.getFavoriteGoals());
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
        final IGoalsGrabber goalsGrabber;
        try {
            goalsGrabber = new DefaultGoalsGrabber(new ModuleLocationFinder(module));
            goalsGrabber.refresh();
            ui.loadMavenGoals(goalsGrabber);
        }
        catch (Exception e) {
            Messages.showErrorDialog(module.getProject(), e.getMessage(), UIConstants.ERROR_TITLE);
            LOG.error(e.getMessage(), e);
        }

        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);

        ui.setFavoriteGoals(moduleSettings.getFavoriteGoals());
        ui.setPomFile(moduleSettings.getPomFile());
    }

    public static ModuleSettingsConfigurable getInstance(final Module pModule) {
        return (ModuleSettingsConfigurable) pModule.getComponent(NAME);
    }
}
