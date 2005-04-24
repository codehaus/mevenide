package org.mevenide.idea.common.settings.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;
import org.mevenide.idea.common.ModelObject;

import java.util.Collection;
import java.util.SortedMap;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Arik
 */
public interface ModuleSettingsModel extends ModelObject {

    Collection getFavoriteGoals();

    void setFavoriteGoals(final Collection pFavoriteGoals);

    void setModule(Module pModule);

    Module getModule();

    ProjectJdk getJdk();

    File getPomFile();

    void setPomFile(File pPomFile) throws FileNotFoundException;

    void addModuleSettingsListener(ModuleSettingsListener pListener);

    void removeModuleSettingsListener(final ModuleSettingsListener pListener);

    SortedMap getPluginsMap();

    String[] getPlugins();

    String[] getGoals(String pPlugin);

    String getDescription(String fullyQualifiedGoalName);

    String getOrigin(String fullyQualifiedGoalName);

    String[] getPrereqs(String fullyQualifiedGoalName);

    void refresh() throws Exception;
}
