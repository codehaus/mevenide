package org.codehaus.mevenide.idea;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import org.apache.log4j.Logger;
import org.codehaus.mevenide.idea.action.CoreConfigurationActionListener;
import org.codehaus.mevenide.idea.form.CoreConfigurationForm;
import org.codehaus.mevenide.idea.model.MavenConfiguration;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * The core Mevenide IDEA plugin provides methods generally available to all
 * other Mevenide IDEA plugins which depends on this plugin.
 * </br>
 * A plugin which depends on this core plugin has to implement the
 * {@link org.codehaus.mevenide.idea.IMevenideIdeaComponent} interface
 * and register itself into the core plugin by calling the method
 * {@link #registerMevenideComponent}
 *
 * @author Ralf Quebbemann (ralfq@codehaus.org)
 */
public class CorePlugin implements ProjectComponent, Configurable, JDOMExternalizable {
    private static final Logger LOG = Logger.getLogger(CorePlugin.class);

    private final Project corePlugin;
    private Set<IMevenideIdeaComponent> mevenideIdeaComponents =
            new TreeSet<IMevenideIdeaComponent>(new MevenideIdeaComponentComparator());
    private CoreConfigurationForm form;
    private MavenConfiguration mavenConfiguration = new MavenConfiguration();
    private int selectedTabIndex = 0;

    private class MevenideIdeaComponentComparator implements Comparator {

        public int compare(Object mevenideComponent, Object mevenideComponent1) {
            if (mevenideComponent instanceof IMevenideIdeaComponent &&
                    mevenideComponent1 instanceof IMevenideIdeaComponent) {
                return ((IMevenideIdeaComponent) mevenideComponent).getMevenideComponentName()
                        .compareTo(((IMevenideIdeaComponent) mevenideComponent1).getMevenideComponentName());
            }
            return -1;
        }
    }

    public CorePlugin(Project project) {
        corePlugin = project;
        LOG.info("Mevenide2 Core Plugin constructed!");
    }

    public void registerMevenideComponent(IMevenideIdeaComponent mevenideIdeaComponent) {
        if (mevenideIdeaComponent != null) {
            LOG.info("Registering mevenide component: " + mevenideIdeaComponent.getMevenideComponentName());
            mevenideIdeaComponents.add(mevenideIdeaComponent);
        }
    }

    /**
     * Return the maven configuration, so other plugins may use it.
     *
     * @return The maven configuration
     */
    public MavenConfiguration getMavenConfiguration() {
        return mavenConfiguration;
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return "CorePlugin";
    }


    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }

    public JComponent createComponent() {
        if (form == null) {
            createCoreForm();
        }
        LOG.info("Instantiated Core Configuration Form!");
        // add all mevenide idea configuration forms to the core form
        for (IMevenideIdeaComponent mevenideIdeaComponent : mevenideIdeaComponents) {
            JPanel panel = new JPanel();
            panel.add(mevenideIdeaComponent.getMevenideConfigurationComponent());
            form.getTabbedPane().add(mevenideIdeaComponent.getMevenideComponentName(), mevenideIdeaComponent.getMevenideConfigurationComponent());
            LOG.info("Adding Mevenide2 component: " + mevenideIdeaComponent.getMevenideComponentName());
        }
        return form.getRootComponent();
    }

    private void createCoreForm() {
        form = new CoreConfigurationForm();
        JButton buttonBrowseLocalRepository = form.getMavenCoreConfigurationForm().getButtonBrowseLocalRepository();
        buttonBrowseLocalRepository
                .addActionListener(new CoreConfigurationActionListener(corePlugin,
                        form.getMavenCoreConfigurationForm()));
    }

    public boolean isModified() {
        boolean isModified = form != null && form.isModified(mavenConfiguration);
        for (IMevenideIdeaComponent mevenideIdeaComponent : mevenideIdeaComponents) {
            isModified = isModified || mevenideIdeaComponent.isMevenideConfigurationModified();
        }
        return isModified;
    }

    public void apply() throws ConfigurationException {
        if (form != null) {
            // Get data from form to component
            form.getData(mavenConfiguration);
            for (IMevenideIdeaComponent mevenideIdeaComponent : mevenideIdeaComponents) {
                mevenideIdeaComponent.applyMevenideConfiguration();
            }
            selectedTabIndex = form.getTabbedPane().getSelectedIndex();
        }
    }

    public void reset() {
        if (form != null) {
            // Reset form data from component
            form.setData(mavenConfiguration);
            for (IMevenideIdeaComponent mevenideIdeaComponent : mevenideIdeaComponents) {
                mevenideIdeaComponent.resetMevenideConfiguration();
            }
            form.getTabbedPane().setSelectedIndex(selectedTabIndex);
        }
    }

    public void disposeUIResources() {
        form = null;
    }

    @Nls
    public String getDisplayName() {
        return "Mevenide2 IDEA";
    }

    public Icon getIcon() {
        return null;
    }

    @Nullable
    @NonNls
    public String getHelpTopic() {
        return null;
    }

    public void readExternal(Element element) throws InvalidDataException {
        mavenConfiguration.setChecksumPolicy(JDOMExternalizerUtil.readField(element,
                MavenConfiguration.CONFIG_ELEMENT_CHECKSUM_POLICY));
        mavenConfiguration.setFailureBehavior(JDOMExternalizerUtil.readField(element,
                MavenConfiguration.CONFIG_ELEMENT_FAILURE_BEHAVIOR));
        mavenConfiguration.setLocalRepository(JDOMExternalizerUtil.readField(element,
                MavenConfiguration.CONFIG_ELEMENT_LOCAL_REPOSITORY));
        mavenConfiguration.setNonRecursive(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                MavenConfiguration.CONFIG_ELEMENT_NON_RECURSIVE)));
        mavenConfiguration.setOutputLevel(Integer.valueOf(JDOMExternalizerUtil.readField(element,
                MavenConfiguration.CONFIG_ELEMENT_OUTPUT_LEVEL)));
        mavenConfiguration.setPluginUpdatePolicy(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                MavenConfiguration.CONFIG_ELEMENT_PLUGIN_UPDATE_POLICY)));
        mavenConfiguration.setProduceExceptionErrorMessages(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                MavenConfiguration.CONFIG_ELEMENT_EXCEPTION_ERROR_MESSAGES)));
        mavenConfiguration.setUsePluginRegistry(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                MavenConfiguration.CONFIG_ELEMENT_USE_PLUGIN_REGISTRY)));
        mavenConfiguration.setWorkOffline(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                MavenConfiguration.CONFIG_ELEMENT_WORK_OFFLINE)));
    }

    public void writeExternal(Element element) throws WriteExternalException {
        JDOMExternalizerUtil.writeField(element, MavenConfiguration.CONFIG_ELEMENT_CHECKSUM_POLICY,
                mavenConfiguration.getChecksumPolicy());
        JDOMExternalizerUtil.writeField(element, MavenConfiguration.CONFIG_ELEMENT_EXCEPTION_ERROR_MESSAGES,
                String.valueOf(mavenConfiguration.isProduceExceptionErrorMessages()));
        JDOMExternalizerUtil.writeField(element, MavenConfiguration.CONFIG_ELEMENT_FAILURE_BEHAVIOR,
                mavenConfiguration.getFailureBehavior());
        JDOMExternalizerUtil.writeField(element, MavenConfiguration.CONFIG_ELEMENT_LOCAL_REPOSITORY,
                mavenConfiguration.getLocalRepository());
        JDOMExternalizerUtil.writeField(element, MavenConfiguration.CONFIG_ELEMENT_NON_RECURSIVE,
                String.valueOf(mavenConfiguration.isNonRecursive()));
        JDOMExternalizerUtil.writeField(element, MavenConfiguration.CONFIG_ELEMENT_OUTPUT_LEVEL,
                String.valueOf(mavenConfiguration.getOutputLevel()));
        JDOMExternalizerUtil.writeField(element, MavenConfiguration.CONFIG_ELEMENT_PLUGIN_UPDATE_POLICY,
                String.valueOf(mavenConfiguration.isPluginUpdatePolicy()));
        JDOMExternalizerUtil.writeField(element, MavenConfiguration.CONFIG_ELEMENT_USE_PLUGIN_REGISTRY,
                String.valueOf(mavenConfiguration.isUsePluginRegistry()));
        JDOMExternalizerUtil.writeField(element, MavenConfiguration.CONFIG_ELEMENT_WORK_OFFLINE,
                String.valueOf(mavenConfiguration.isWorkOffline()));
    }
}
