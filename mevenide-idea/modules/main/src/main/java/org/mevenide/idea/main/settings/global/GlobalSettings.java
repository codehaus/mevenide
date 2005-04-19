package org.mevenide.idea.main.settings.global;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.application.ApplicationManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.mevenide.idea.common.settings.global.GlobalSettingsModel;
import org.mevenide.idea.common.ui.UI;
import org.mevenide.idea.common.settings.global.GlobalSettingsListener;
import org.mevenide.idea.main.ModelDelegatingComponent;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Arik
 */
public class GlobalSettings extends ModelDelegatingComponent implements ApplicationComponent,
                                                                        GlobalSettingsModel,
                                                                        JDOMExternalizable {

    private static final Log LOG = LogFactory.getLog(GlobalSettings.class);

    /**
     * The maven home element name.
     */
    private static final String MAVEN_HOME_ELT_NAME = "mavenHome";

    /**
     * The model instance.
     */
    private GlobalSettingsModel model;

    /**
     * Creates an instance.
     */
    public GlobalSettings() {
        super("org.mevenide.idea.model.settings.global.GlobalSettingsModelImpl");
    }

    protected void setModel(final Object pModelInstance) {
        model = (GlobalSettingsModel) pModelInstance;
    }

    public String getComponentName() {
        return GlobalSettings.class.getName();
    }

    public void initComponent() {
        model.initComponent();
    }

    public File getMavenHome() {
        return model.getMavenHome();
    }

    public void setMavenHome(final File pMavenHome) throws FileNotFoundException {
        model.setMavenHome(pMavenHome);
    }

    public void addGlobalSettingsListener(final GlobalSettingsListener pListener) {
        model.addGlobalSettingsListener(pListener);
    }

    public void removeGlobalSettingsListener(final GlobalSettingsListener pListener) {
        model.removeGlobalSettingsListener(pListener);
    }

    public void disposeComponent() {
    }

    public void readExternal(final Element pElement) throws InvalidDataException {

        final Element mavenHomeElt = pElement.getChild(MAVEN_HOME_ELT_NAME);
        File lMavenHome = null;

        if (mavenHomeElt != null) {
            final String mavenHomeText = mavenHomeElt.getTextTrim();
            if (mavenHomeText != null && mavenHomeText.trim().length() > 0)
                lMavenHome = new File(mavenHomeText);
        }

        try {
            setMavenHome(lMavenHome);
        }
        catch (FileNotFoundException e) {
            Messages.showErrorDialog(e.getMessage(), UI.ERR_TITLE);
            LOG.error(e.getMessage(), e);
        }
    }

    public void writeExternal(final Element pElement) throws WriteExternalException {
        final Element mavenHomeElt = new Element(MAVEN_HOME_ELT_NAME);
        final File mavenHome = getMavenHome();
        if (mavenHome != null)
            mavenHomeElt.setText(mavenHome.getAbsolutePath());

        pElement.addContent(mavenHomeElt);
    }

    public static GlobalSettings getInstance() {
        return (GlobalSettings) ApplicationManager.getApplication().getComponent(GlobalSettings.class);
    }
}
