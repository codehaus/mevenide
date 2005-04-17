package org.mevenide.idea.goalstoolwindow;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.util.Icons;
import org.apache.commons.lang.StringUtils;
import org.mevenide.idea.settings.module.ModuleSettingsConfigurable;
import org.mevenide.idea.support.ui.ConfigurableWrapper;
import org.mevenide.idea.support.ui.UIConstants;
import org.mevenide.idea.util.Res;
import org.mevenide.idea.util.images.Images;

import javax.swing.*;

/**
 * @author Arik
 */
public class ShowModuleSettingsAction extends AnAction {
    private static final Res RES = Res.getInstance(ShowModuleSettingsAction.class);

    public ShowModuleSettingsAction() {
        super(RES.get("show.settings.action.text"),
              RES.get("show.settings.action.desc"),
              new ImageIcon(Images.OPTIONS));
    }

    public boolean displayTextInToolbar() {
        return true;
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Project project = (Project) pEvent.getDataContext().getData(DataConstants.PROJECT);


        final ConfigurableGroup[] configurableGroup =
                new ConfigurableGroup[]{new ModulesConfigurableGroup(project)};
        ShowSettingsUtil.getInstance().showSettingsDialog(project, configurableGroup);
    }

    private static class ModulesConfigurableGroup implements ConfigurableGroup {
        private final Configurable[] configurables;

        public ModulesConfigurableGroup(final Project pProject) {
            final Module[] modules = ModuleManager.getInstance(pProject).getModules();
            configurables = new Configurable[modules.length];
            for (int i = 0; i < modules.length; i++) {
                final Module module = modules[i];
                final ConfigurableWrapper wrapper = new ConfigurableWrapper(ModuleSettingsConfigurable.getInstance(module));
                wrapper.setCustomDisplayName(StringUtils.capitalize(module.getName()));
                if(module.getModuleType().equals(ModuleType.WEB))
                    wrapper.setCustomIcon(Icons.WEB_ICON);
                else if (module.getModuleType().equals(ModuleType.EJB))
                    wrapper.setCustomIcon(Icons.EJB_ICON);
                else if (module.getModuleType().equals(ModuleType.J2EE_APPLICATION))
                    wrapper.setCustomIcon(Icons.PACKAGE_ICON);
                else if (module.getModuleType().equals(ModuleType.JAVA))
                    wrapper.setCustomIcon(Icons.CLASS_ICON);

                configurables[i] = wrapper;
            }
        }

        public String getDisplayName() {
            return UIConstants.MODULE_SETTINGS_TITLE;
        }

        public String getShortName() {
            return UIConstants.MODULE_SETTINGS_TITLE;
        }

        public Configurable[] getConfigurables() {
            return configurables;
        }
    }
}
