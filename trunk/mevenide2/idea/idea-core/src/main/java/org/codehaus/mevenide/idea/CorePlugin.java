package org.codehaus.mevenide.idea;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.apache.log4j.Logger;
import org.codehaus.mevenide.idea.form.CoreConfigurationForm;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.List;

/**
 * Todo: Describe what this class does!
 *
 * @author Ralf Quebbemann (ralfq@codehaus.org)
 */
public class CorePlugin implements ProjectComponent, Configurable {
    private static final Logger LOG = Logger.getLogger(CorePlugin.class);

    private final Project corePlugin;
    private List<IMevenideIdeaComponent> mevenideIdeaComponents = new ArrayList<IMevenideIdeaComponent>();
    private CoreConfigurationForm form;
    private boolean scanForExistingPoms;
    private boolean checkBoxText;
    // the logger that corresponds to this instance of the plugin


    public CorePlugin(Project project) {
        corePlugin = project;
    }

    public void registerMevenideComponent(IMevenideIdeaComponent mevenideIdeaComponent) {
        mevenideIdeaComponents.add(mevenideIdeaComponent);
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    public String getComponentName() {
        return "CorePlugin";
    }


    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }

    public boolean isScanForExistingPoms() {
        return scanForExistingPoms;
    }

    public void setScanForExistingPoms(final boolean scanForExistingPoms) {
        this.scanForExistingPoms = scanForExistingPoms;
    }

    public boolean isCheckBoxText() {
        return checkBoxText;
    }

    public void setCheckBoxText(final boolean checkBoxText) {
        this.checkBoxText = checkBoxText;
    }

    public JComponent createComponent() {
        if (form == null) {
            form = new CoreConfigurationForm();
        }

        for (IMevenideIdeaComponent mevenideIdeaComponent : mevenideIdeaComponents) {
            JPanel panel = new JPanel();
            panel.add(mevenideIdeaComponent.getMevenideConfigurationComponent());
            form.getTabbedPane().add(mevenideIdeaComponent.getMevenideComponentName(), panel);
        }
        return form.getRootComponent();
    }

    public boolean isModified() {
        boolean isModified = form != null && form.isModified(this);
        for (IMevenideIdeaComponent mevenideIdeaComponent : mevenideIdeaComponents) {
            isModified = isModified || mevenideIdeaComponent.isMevenideConfigurationModified();
        }
        return isModified;
    }

    public void apply() throws ConfigurationException {
        if (form != null) {
            // Get data from form to component
            form.getData(this);
            for (IMevenideIdeaComponent mevenideIdeaComponent : mevenideIdeaComponents) {
                mevenideIdeaComponent.applyMevenideConfiguration();
            }
        }
    }

    public void reset() {
        if (form != null) {
            // Reset form data from component
            form.setData(this);
            for (IMevenideIdeaComponent mevenideIdeaComponent : mevenideIdeaComponents) {
                mevenideIdeaComponent.resetMevenideConfiguration();
            }
        }
    }

    public void disposeUIResources() {
        form = null;
    }

    @Nls
    public String getDisplayName() {
        return "Mevenide2 IDEA";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Icon getIcon() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nullable
    @NonNls
    public String getHelpTopic() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
