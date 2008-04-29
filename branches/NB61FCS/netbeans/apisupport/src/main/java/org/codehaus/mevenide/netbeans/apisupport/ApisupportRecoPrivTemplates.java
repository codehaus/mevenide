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

package org.codehaus.mevenide.netbeans.apisupport;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;

/**
 * apisupport specific part of RecommendedTemplates and PrivilegedTemplates,
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class ApisupportRecoPrivTemplates implements RecommendedTemplates, PrivilegedTemplates {
    
    private Project project;
    
    ApisupportRecoPrivTemplates(Project proj) {
        project = proj;
    }
    
        private static final String[] NBM_PRIVILEGED_NAMES = new String[] {
            "Templates/Classes/Class.java", // NOI18N
            "Templates/Classes/Package", // NOI18N
            "Templates/Classes/Interface.java", // NOI18N
            //"Templates/GUIForms/JPanel.java", // NOI18N
            "Templates/JUnit/SimpleJUnitTest.java", // NOI18N
            "Templates/NetBeansModuleDevelopment/newAction", // NOI18N
            "Templates/NetBeansModuleDevelopment/newLoader", // NOI18N
            "Templates/NetBeansModuleDevelopment/newWindow", // NOI18N
            "Templates/NetBeansModuleDevelopment/newWizard", // NOI18N
            //"Templates/Other/properties.properties", // NOI18N
        };
        private static final String[] NBM_TYPES = new String[] {         
            "java-classes",         // NOI18N
            "java-main-class",      // NOI18N
            "java-forms",           // NOI18N
            "java-beans",           // NOI18N
            "oasis-XML-catalogs",   // NOI18N
            "XML",                  // NOI18N
            "junit",                // NOI18N                    
            "simple-files",         // NOI18N
            "nbm-specific"         // NOI18N
        };
        
    
    public String[] getRecommendedTypes() {
        ProjectURLWatcher watcher = project.getLookup().lookup(ProjectURLWatcher.class);
        String packaging = watcher.getPackagingType();
        if (packaging == null) {
            packaging = ProjectURLWatcher.TYPE_JAR;
        }
        packaging = packaging.trim();
        if (ProjectURLWatcher.TYPE_NBM.equals(packaging)) {
            return NBM_TYPES;
        }
        return new String[0];
    }
    
    public String[] getPrivilegedTemplates() {
        ProjectURLWatcher watcher = project.getLookup().lookup(ProjectURLWatcher.class);
        String packaging = watcher.getPackagingType();
        if (packaging == null) {
            packaging = ProjectURLWatcher.TYPE_JAR;
        }
        packaging = packaging.trim();
        if (ProjectURLWatcher.TYPE_NBM.equals(packaging)) {
            return NBM_PRIVILEGED_NAMES;
        }
        
        return new String[0];
    }
    
}
