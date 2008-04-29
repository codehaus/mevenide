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

package org.codehaus.mevenide.netbeans.j2ee;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.api.ejbjar.EjbProjectConstants;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;

/**
 * j2ee specific part of RecommendedTemplates and PrivilegedTemplates,
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class J2eeRecoPrivTemplates implements RecommendedTemplates, PrivilegedTemplates {
    
    private Project project;
    
    J2eeRecoPrivTemplates(Project proj) {
        project = proj;
    }
    
    private static final String[] EAR_TYPES = new String[] {
                "ear-types",                 // NOI18N
    };
    
    private static final String[] EAR_PRIVILEGED_NAMES = new String[] {
                "Templates/J2EE/ApplicationXml", //NOI18N
                "Templates/Other/Folder" //NOI18N
    };
    
    private static final String[] EJB_TYPES_4 = new String[] {
                "ejb-types",            // NOI18N
                "ejb-types_2_1",        // NOI18N
                "j2ee-14-types",        // NOI18N
                "ejb-types-server",     // NOI18N
                "web-services",         // NOI18N
                "j2ee-types",           // NOI18N
    };
    
    private static final String[] EJB_TYPES_5 = new String[] {
                "ejb-types",            // NOI18N
                "ejb-types_3_0",        // NOI18N
                "ejb-types-server",     // NOI18N
                "web-services",         // NOI18N
                "j2ee-types",           // NOI18N
    };
    
    private static final String[] EJB_PRIVILEGED_NAMES_4 = new String[] {
        
                "Templates/J2EE/Session", // NOI18N
                "Templates/J2EE/Entity",  // NOI18N
                "Templates/J2EE/RelatedCMP", // NOI18N
                "Templates/J2EE/Message", //NOI18N
                "Templates/Classes/Class.java", //NOI18N
                "Templates/Classes/Package", //NOI18N
    };
    
    private static final String[] EJB_PRIVILEGED_NAMES_5 = new String[] {
        
                "Templates/J2EE/Session", // NOI18N
                "Templates/J2EE/Message", //NOI18N
                "Templates/Classes/Class.java",// NOI18N
                "Templates/Classes/Package",// NOI18N
                "Templates/Persistence/Entity.java",// NOI18N
                "Templates/Persistence/RelatedCMP"// NOI18N
    };
    
    private static final String[] WEB_TYPES = new String[] {
                "servlet-types",        // NOI18N
                "web-types",            // NOI18N
                "web-types-server",     // NOI18N
//                "web-services",         // NOI18N
//                "web-service-clients",  // NOI18N
    };
    
    private static final String[] WEB_PRIVILEGED_NAMES = new String[] {
                "Templates/JSP_Servlet/JSP.jsp",            // NOI18N
                "Templates/JSP_Servlet/Html.html",          // NOI18N
                "Templates/JSP_Servlet/Servlet.java",       // NOI18N
                "Templates/Classes/Class.java",             // NOI18N
                "Templates/Classes/Package",                // NOI18N
//                "Templates/WebServices/WebService",         // NOI18N
//                "Templates/WebServices/WebServiceClient",   // NOI18N
                "Templates/Other/Folder",                   // NOI18N
    };
    
    public String[] getRecommendedTypes() {
        ProjectURLWatcher watcher = project.getLookup().lookup(ProjectURLWatcher.class);
        String packaging = watcher.getPackagingType();
        if (packaging == null) {
            packaging = ProjectURLWatcher.TYPE_JAR;
        }
        packaging = packaging.trim();
        if (ProjectURLWatcher.TYPE_EJB.equals(packaging)) {
            EjbJar jar = EjbJar.getEjbJar(project.getProjectDirectory());
            if (jar != null) {
                if (EjbProjectConstants.JAVA_EE_5_LEVEL.equals(jar.getJ2eePlatformVersion())) {
                    return EJB_TYPES_5;
                }
            }
            return EJB_TYPES_4;
        }
        if (ProjectURLWatcher.TYPE_EAR.equals(packaging)) {
            return EAR_TYPES;
        }
        if (ProjectURLWatcher.TYPE_WAR.equals(packaging)) {
            return WEB_TYPES;
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
        if (ProjectURLWatcher.TYPE_EJB.equals(packaging)) {
            EjbJar jar = EjbJar.getEjbJar(project.getProjectDirectory());
            if (jar != null) {
                if (EjbProjectConstants.JAVA_EE_5_LEVEL.equals(jar.getJ2eePlatformVersion())) {
                    return EJB_PRIVILEGED_NAMES_5;
                }
            }
            return EJB_PRIVILEGED_NAMES_4;
        }
        if (ProjectURLWatcher.TYPE_EAR.equals(packaging)) {
            return EAR_PRIVILEGED_NAMES;
        }
        if (ProjectURLWatcher.TYPE_WAR.equals(packaging)) {
            return WEB_PRIVILEGED_NAMES;
        }
        
        return new String[0];
    }
    
}
