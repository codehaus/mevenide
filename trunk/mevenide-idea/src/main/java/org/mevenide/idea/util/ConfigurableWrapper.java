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
package org.mevenide.idea.util;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;

/**
 * @author Arik
 */
public class ConfigurableWrapper implements Configurable {
    private final Configurable delegate;
    private String customDisplayName;
    private String customHelpTopic;
    private Icon customIcon;

    public ConfigurableWrapper(final Configurable pDelegate) {
        delegate = pDelegate;
    }

    public Configurable getDelegate() {
        return delegate;
    }

    public String getCustomDisplayName() {
        return customDisplayName;
    }

    public void setCustomDisplayName(final String pCustomDisplayName) {
        customDisplayName = pCustomDisplayName;
    }

    public String getCustomHelpTopic() {
        return customHelpTopic;
    }

    public void setCustomHelpTopic(final String pCustomHelpTopic) {
        customHelpTopic = pCustomHelpTopic;
    }

    public Icon getCustomIcon() {
        return customIcon;
    }

    public void setCustomIcon(final Icon pCustomIcon) {
        customIcon = pCustomIcon;
    }

    public String getDisplayName() {
        return customDisplayName == null ? delegate.getDisplayName() : customDisplayName;
    }

    public String getHelpTopic() {
        return customHelpTopic == null ? delegate.getHelpTopic() : customHelpTopic;
    }

    public Icon getIcon() {
        return customIcon == null ? delegate.getIcon() : customIcon;
    }

    public void apply() throws ConfigurationException {
        delegate.apply();
    }

    public JComponent createComponent() {
        return delegate.createComponent();
    }

    public void disposeUIResources() {
        delegate.disposeUIResources();
    }

    public boolean isModified() {
        return delegate.isModified();
    }

    public void reset() {
        delegate.reset();
    }
}