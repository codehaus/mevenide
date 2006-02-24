/*
 * ModelHandle.java
 *
 * Created on February 22, 2006, 11:53 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.customizer;

import java.util.Map;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;

/**
 *
 * @author mkleint
 */
public final class ModelHandle {

    private Model model;
    private MavenProject project;

    private ActionToGoalMapping mapping;
    
    /** Creates a new instance of ModelHandle */
    ModelHandle(Model mdl, MavenProject proj, ActionToGoalMapping mapping) {
        model = mdl;
        project = proj;
        this.mapping = mapping;
    }
    
    public Model getPOMModel() {
        return model;
    }
    
    public MavenProject getProject() {
        return project;
    }
    
    public ActionToGoalMapping getActionMappings() {
        return mapping;
    }
    
}
