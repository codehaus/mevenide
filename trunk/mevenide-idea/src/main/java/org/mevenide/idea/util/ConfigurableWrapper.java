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