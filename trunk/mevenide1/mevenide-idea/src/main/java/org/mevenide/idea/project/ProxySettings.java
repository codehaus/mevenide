package org.mevenide.idea.project;

import com.intellij.openapi.project.Project;
import org.mevenide.idea.project.properties.PropertiesManager;

/**
 * Created by IntelliJ IDEA. User: Arik Date: 08/07/2005 Time: 18:44:02 To change this template use
 * File | Settings | File Templates.
 */
public class ProxySettings extends AbstractPomSettingsManager {
    public ProxySettings(final Project pProject) {
        super(pProject);
    }

    public String getProxyHost(final String pPomUrl) {
        final PropertiesManager mgr = PropertiesManager.getInstance(project);
        final String value = mgr.getProperty(pPomUrl, "maven.proxy.host");
        if (value != null && value.trim().length() == 0)
            return null;
        return value;
    }

    public Integer getProxyPort(final String pPomUrl) {
        final PropertiesManager mgr = PropertiesManager.getInstance(project);
        final String portValue = mgr.getProperty(pPomUrl, "maven.proxy.port");
        if (portValue == null || portValue.trim().length() == 0)
            return null;

        return Integer.valueOf(portValue);
    }

    public String getProxyUsername(final String pPomUrl) {
        final PropertiesManager mgr = PropertiesManager.getInstance(project);
        final String value = mgr.getProperty(pPomUrl, "maven.proxy.username");
        if (value != null && value.trim().length() == 0)
            return null;
        return value;
    }

    public String getProxyPassword(final String pPomUrl) {
        final PropertiesManager mgr = PropertiesManager.getInstance(project);
        final String value = mgr.getProperty(pPomUrl, "maven.proxy.password");
        if (value != null && value.trim().length() == 0)
            return null;
        return value;
    }

    public static ProxySettings getInstance(final Project pProject) {
        return pProject.getComponent(ProxySettings.class);
    }
}
