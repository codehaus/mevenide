/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.idea.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.idea.util.goals.grabber.CustomGoalsGrabber;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;

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
            moduleSettings.setFavoriteGoals(ui.getFavoriteGoals());
        }
        catch (FileNotFoundException e) {
            final ConfigurationException confEx = new ConfigurationException(e.getMessage(), UIUtils.ERROR_TITLE);
            throw (ConfigurationException) confEx.initCause(e);
        }
    }

    public JComponent createComponent() {
        return ui;
    }

    public void disposeUIResources() {
    }

    public boolean isModified() {
        return isPomFileModified() || areFavoriteGoalsModified();
    }

    protected boolean areFavoriteGoalsModified() {
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
        final IGoalsGrabber currentFavorites = moduleSettings.getFavoriteGoals();
        final IGoalsGrabber favorites = ui.getFavoriteGoals();

        return !currentFavorites.equals(favorites);
    }

    protected boolean isPomFileModified() {
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);

        final File selectedPomFile = ui.getPomFile();
        final File pomFile = moduleSettings.getPomFile();

        if (selectedPomFile == pomFile)
            return false;

        if (selectedPomFile == null || pomFile == null)
            return true;

        return !selectedPomFile.equals(pomFile);
    }

    public void reset() {
        IGoalsGrabber grabber;

        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
        final File pomFile = moduleSettings.getPomFile();

        if(pomFile == null) {
            grabber = new CustomGoalsGrabber("Goals");
        }
        else {
            final File pomDir = pomFile.getParentFile();
            final IQueryContext queryContext = new DefaultQueryContext(pomDir);
            final ILocationFinder locationFinder = new ModuleLocationFinder(queryContext,
                                                                            module);
            try {
                grabber = new DefaultGoalsGrabber(locationFinder);
            }
            catch (Exception e) {
                grabber = new CustomGoalsGrabber("Goals");
                UIUtils.showError(module, "Error grabbing global goals.", e);
                LOG.error(e.getMessage(), e);
            }
        }

        ui.setMavenGoals(grabber);
        ui.setFavoriteGoals(moduleSettings.getFavoriteGoals());
        ui.setPomFile(moduleSettings.getPomFile());
    }

    public void projectClosed() {
    }

    public void disposeComponent() {
    }
}
