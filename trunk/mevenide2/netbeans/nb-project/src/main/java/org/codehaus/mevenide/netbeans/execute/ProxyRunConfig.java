/*
 * ProxyRunConfig.java
 *
 * Created on July 23, 2006, 11:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.execute;

import java.io.File;
import java.util.List;
import java.util.Properties;
import org.codehaus.mevenide.netbeans.NbMavenProject;

/**
 *
 * @author mkleint
 */
public class ProxyRunConfig implements RunConfig {

    private RunConfig delegate;
    
    /** Creates a new instance of ProxyRunConfig */
    public ProxyRunConfig(RunConfig delegate) {
        this.delegate = delegate;
    }

    public File getExecutionDirectory() {
        return delegate.getExecutionDirectory();
    }

    public NbMavenProject getProject() {
        return delegate.getProject();
    }

    public List getGoals() {
        return delegate.getGoals();
    }

    public String getExecutionName() {
        return delegate.getExecutionName();
    }

    public Properties getProperties() {
        return delegate.getProperties();
    }

    public Boolean isShowDebug() {
        return delegate.isShowDebug();
    }

    public Boolean isShowError() {
        return delegate.isShowError();
    }

    public Boolean isOffline() {
        return delegate.isOffline();
    }

    public List getActiveteProfiles() {
        return delegate.getActiveteProfiles();
    }
    
}
