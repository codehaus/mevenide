/*
 * ProjectRunContext.java
 *
 * Created on February 12, 2005, 3:47 PM
 */

package org.mevenide.netbeans.project.exec;

import java.io.File;

/**
 *
 * @author cenda
 */
public class BeanRunContext implements RunContext {
    private String name;
    private String homeDir;
    private File directory;
    private String[] additionalParams;
    /** Creates a new instance of ProjectRunContext */
    public BeanRunContext(String nm, String home, File dir, String[] params) {
        name = nm;
        homeDir = home;
        directory = dir;
        additionalParams = params;
    }

    public File getExecutionDirectory() {
        return directory;
    }

    public String getExecutionName() {
        return name;
    }

    public String getMavenHome() {
        return homeDir;
    }
    
    public String[] getAdditionalParams() {
        return additionalParams;
    }
    
}
