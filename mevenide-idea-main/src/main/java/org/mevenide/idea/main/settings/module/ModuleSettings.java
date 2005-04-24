package org.mevenide.idea.main.settings.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.JDOMExternalizableStringList;
import com.intellij.openapi.util.WriteExternalException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.mevenide.idea.common.settings.module.ModuleSettingsListener;
import org.mevenide.idea.common.settings.module.ModuleSettingsModel;
import org.mevenide.idea.common.ui.UI;
import org.mevenide.idea.main.ModelDelegatingComponent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.SortedMap;

/**
 * @author Arik
 */
public class ModuleSettings extends ModelDelegatingComponent implements ModuleComponent,
                                                                        ModuleSettingsModel,
                                                                        JDOMExternalizable {
    private static final Log LOG = LogFactory.getLog(ModuleSettings.class);

    private final Module module;
    private ModuleSettingsModel model;
    private static final String FAVORITE_GOALS_ELT_NAME = "favoriteGoals";
    private static final String POM_FILE_ATTR_NAME = "pomFile";

    public ModuleSettings(final Module pModule) {
        super("org.mevenide.idea.model.settings.module.ModuleSettingsModelImpl");
        module = pModule;
        model.setModule(module);
    }

    protected void setModel(final Object pModelInstance) {
        model = (ModuleSettingsModel) pModelInstance;
    }

    public String getComponentName() {
        return ModuleSettings.class.getName();
    }

    public void initComponent() {
        model.initComponent();
    }

    public void projectOpened() {
    }

    public void moduleAdded() {
    }

    public void projectClosed() {
    }

    public void disposeComponent() {
    }

    public void readExternal(Element pElement) throws InvalidDataException {

        //
        //load favorite goals list
        //
        final Collection favoriteGoals = new HashSet(10);
        final Element favoritesElt = pElement.getChild(FAVORITE_GOALS_ELT_NAME);
        if (favoritesElt != null) {
            JDOMExternalizableStringList list = new JDOMExternalizableStringList();
            list.readExternal(favoritesElt);
            favoriteGoals.addAll(list);
        }
        setFavoriteGoals(favoriteGoals);

        //
        //load POM file
        //
        final String mavenProjectFilename = pElement.getAttributeValue(POM_FILE_ATTR_NAME);
        if (mavenProjectFilename != null && mavenProjectFilename.trim().length() > 0)
            try {
                setPomFile(new File(mavenProjectFilename));
            }
            catch (FileNotFoundException e) {
                Messages.showErrorDialog(module.getProject(), e.getMessage(), UI.ERR_TITLE);
                LOG.error(e.getMessage(), e);
            }
    }

    public void writeExternal(Element pElement) throws WriteExternalException {

        //
        //save favorite goals list
        //
        final Collection favoriteGoals = getFavoriteGoals();
        if (favoriteGoals.size() > 0) {
            final Element favoritesElt = new Element(FAVORITE_GOALS_ELT_NAME);
            JDOMExternalizableStringList list = new JDOMExternalizableStringList();
            list.addAll(favoriteGoals);
            list.writeExternal(favoritesElt);
            pElement.addContent(favoritesElt);
        }

        //
        //save POM file
        //
        final File pomFile = getPomFile();
        if (pomFile != null)
            pElement.setAttribute(POM_FILE_ATTR_NAME, pomFile.getAbsolutePath());
    }

    public void addModuleSettingsListener(ModuleSettingsListener pListener) {
        model.addModuleSettingsListener(pListener);
    }

    public Collection getFavoriteGoals() {
        return model.getFavoriteGoals();
    }

    public ProjectJdk getJdk() {
        return model.getJdk();
    }

    public Module getModule() {
        return model.getModule();
    }

    public void setModule(Module pModule) {
        model.setModule(pModule);
    }

    public File getPomFile() {
        return model.getPomFile();
    }

    public void removeModuleSettingsListener(final ModuleSettingsListener pListener) {
        model.removeModuleSettingsListener(pListener);
    }

    public void setFavoriteGoals(final Collection pFavoriteGoals) {
        model.setFavoriteGoals(pFavoriteGoals);
    }

    public void setPomFile(File pPomFile) throws FileNotFoundException {
        model.setPomFile(pPomFile);
    }

    public static ModuleSettings getInstance(final Module pModule) {
        return (ModuleSettings) pModule.getComponent(ModuleSettings.class);
    }

    public String[] getPlugins() {
        return model.getPlugins();
    }

    public String[] getGoals(String pPlugin) {
        return model.getGoals(pPlugin);
    }

    public SortedMap getPluginsMap() {
        return model.getPluginsMap();
    }

    public String getDescription(String fullyQualifiedGoalName) {
        return model.getDescription(fullyQualifiedGoalName);
    }

    public String getOrigin(String fullyQualifiedGoalName) {
        return model.getOrigin(fullyQualifiedGoalName);
    }

    public String[] getPrereqs(String fullyQualifiedGoalName) {
        return model.getPrereqs(fullyQualifiedGoalName);
    }

    public void refresh() throws Exception {
        model.refresh();
    }
}
