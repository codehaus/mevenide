/*
 * ProjectRunContext.java
 *
 * Created on February 12, 2005, 3:47 PM
 */

package org.mevenide.netbeans.project.exec;

import java.io.File;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author cenda
 */
public class ProjectRunContext implements RunContext {
    private MavenProject project;
    /** Creates a new instance of ProjectRunContext */
    public ProjectRunContext(MavenProject proj) {
        project = proj;
    }

    public File getExecutionDirectory() {
        return FileUtil.toFile(project.getProjectDirectory());
    }

    public String getExecutionName() {
        return project.getName();
    }

    public String getMavenHome() {
        return project.getLocFinder().getMavenHome();
    }
    
    public String[] getAdditionalParams() {
        return new String[0];
    }
    
}
