/* ==========================================================================
 * Copyright 2006 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */

package org.codehaus.mevenide.netbeans.j2ee.ear;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import javax.swing.JComponent;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class EarRunPanelProvider implements ProjectCustomizer.CompositeCategoryProvider {
    
    /** Creates a new instance of EjbRunPanelProvider */
    public EarRunPanelProvider() {
    }
    
    public Category createCategory(Lookup context) {
        Project project = context.lookup(Project.class);
        ProjectURLWatcher watcher = project.getLookup().lookup(ProjectURLWatcher.class);
        if (ProjectURLWatcher.TYPE_EAR.equalsIgnoreCase(watcher.getPackagingType())) {
            return ProjectCustomizer.Category.create(
                    ModelHandle.PANEL_RUN,
                    NbBundle.getMessage(EarRunPanelProvider.class, "PNL_Run"),
                    null,
                    (ProjectCustomizer.Category[])null);
        }
        return null;
    }
    
    public JComponent createComponent(Category category, Lookup context) {
        ModelHandle handle = context.lookup(ModelHandle.class);
        NbMavenProject project = context.lookup(NbMavenProject.class);
        final EarRunCustomizerPanel panel =  new EarRunCustomizerPanel(handle, project);
        category.setOkButtonListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.applyChanges();
            }
        });
        return panel;
    }
    
}
