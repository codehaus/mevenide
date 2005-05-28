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
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.mevenide.idea.module.ui.ModuleSettingsPanel;
import org.mevenide.idea.util.components.AbstractModuleComponent;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;

/**
 * A module component that handles user interface for the module. Acquires data
 * from various module components into a user interface panel and displays to
 * the user. Applies the data back if user chooses to.
 *
 * @author Arik
 */
public class ModuleSettingsConfigurable extends AbstractModuleComponent implements Configurable {

    /**
     * The user interface component.
     */
    private final ModuleSettingsPanel ui;

    /**
     * Creates an instance that manages the given module.
     *
     * @param pModule the module
     */
    public ModuleSettingsConfigurable(final Module pModule) {
        super(pModule);
        ui = new ModuleSettingsPanel(module);
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

    public void apply() throws ConfigurationException {
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
        final Collection<String> selectedGoals = ui.getSelectedGoals();
        final int goalCount = selectedGoals.size();
        final String[] goals = selectedGoals.toArray(new String[goalCount]);
        moduleSettings.setFavoriteGoals(goals);
    }

    public JComponent createComponent() {
        return ui;
    }

    public void disposeUIResources() {
    }

    public boolean isModified() {
        final ModuleSettings settings = ModuleSettings.getInstance(module);
        final String[] moduleGoalsArr = settings.getFavoriteGoals();
        final Collection<String> moduleGoals = Arrays.asList(moduleGoalsArr);

        final Collection<String> uiGoals = ui.getSelectedGoals();

        return !uiGoals.equals(moduleGoals);
    }

    public void reset() {
        try {
            ui.refreshGoals();

            final ModuleSettings settings = ModuleSettings.getInstance(module);
            final String[] moduleGoalsArr = settings.getFavoriteGoals();
            final Collection<String> moduleGoals = Arrays.asList(moduleGoalsArr);
            ui.setSelectedGoals(moduleGoals);
        }
        catch (Exception e) {
            UIUtils.showError(module, "Error grabbing global goals.", e);
            LOG.error(e.getMessage(), e);
        }
    }
}
