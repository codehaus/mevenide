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

package org.codehaus.mevenide.webframeworks;

import javax.swing.JComponent;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;

/**
 *
 * @author mkleint
 */
public class CustomizerPanelProvider implements ProjectCustomizer.CompositeCategoryProvider {

    public ProjectCustomizer.Category createCategory(Lookup context) {
        Project project = context.lookup(Project.class);
        ProjectURLWatcher watcher = project.getLookup().lookup(ProjectURLWatcher.class);
        
        if (ProjectURLWatcher.TYPE_WAR.equalsIgnoreCase(watcher.getPackagingType())) {
            return ProjectCustomizer.Category.create(
                    "Webframeworks", //NOI18N
                    "Web Frameworks",
                    null,
                    (ProjectCustomizer.Category[])null);
        }
        return null;
    }

    public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
        throw new IllegalStateException("Not implemented yet");
//        ModelHandle handle = context.lookup(ModelHandle.class);
//        Project project = context.lookup(Project.class);
//        PanelSupportedFrameworks wizPanel = new PanelSupportedFrameworks();
//        final PanelSupportedFrameworksVisual panel = new PanelSupportedFrameworksVisual();
//        category.setOkButtonListener(new ActionListener() {
//            public void actionPerformed(ActionEvent arg0) {
////                panel.applyExternalChanges();
//            }
//        });
//        return panel;
    }

}
