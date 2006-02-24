/*
 * DefaultPanelProvider.java
 *
 * Created on February 22, 2006, 2:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.j2ee;

import javax.swing.JComponent;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.customizer.M2CustomizerPanelProvider;
import org.codehaus.mevenide.netbeans.customizer.ModelHandle;
import org.codehaus.mevenide.netbeans.j2ee.web.WebRunCustomizerPanel;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;

/**
 * the default implementation for panels, is a fallback implementation with ordering position of
 * 666, if you want to override the defaults in your impl, just place it to a lower position.
 * @author mkleint
 */
public class J2eePanelProvider implements M2CustomizerPanelProvider {
    
    /** Creates a new instance of DefaultPanelProvider */
    public J2eePanelProvider() {
    }

    public JComponent createPanel(ModelHandle handle, NbMavenProject project, ProjectCustomizer.Category category) {
        if (M2CustomizerPanelProvider.PANEL_RUN.equals(category.getName()) && 
            "war".equalsIgnoreCase(project.getOriginalMavenProject().getPackaging())) {
            return new WebRunCustomizerPanel(handle, project);
        }
        return null;
    }
    
}
