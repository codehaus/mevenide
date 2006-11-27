/*
 * ActionMappingsPanel.java
 *
 * Created on November 3, 2006, 10:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.customizer;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import javax.swing.JComponent;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;

/**
 *
 * @author mkleint
 */
public class ActionMappingsPanelProvider implements ProjectCustomizer.CompositeCategoryProvider {
    
    /** Creates a new instance of ActionMappingsPanel */
    public ActionMappingsPanelProvider() {
    }
    
    public Category createCategory(Lookup context) {
        return ProjectCustomizer.Category.create(
                ModelHandle.PANEL_MAPPING, 
                "Action Mappings", 
                null,
                (ProjectCustomizer.Category[])null);
    }
    
    public JComponent createComponent(Category category, Lookup context) {
        ModelHandle handle = context.lookup(ModelHandle.class);
        NbMavenProject project = context.lookup(NbMavenProject.class);
        return new ActionMappings(handle, project);
    }
    
}
