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
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;

/**
 * j2ee specific part of RecommendedTemplates and PrivilegedTemplates,
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class J2eeRecoPrivTemplates implements RecommendedTemplates, PrivilegedTemplates {
    
    private NbMavenProject project;
    
    J2eeRecoPrivTemplates(NbMavenProject proj) {
        project = proj;
    }
    
    
    private static final String[] EAR_TYPES = new String[] {
                "XML",            //NOPMD      // NOI18N
                "ear-types",                 // NOI18N
                "wsdl",          //NOPMD       // NOI18N
                "simple-files"   //NOPMD       // NOI18N
    };
    
    private static final String[] EAR_PRIVILEGED_NAMES = new String[] {
                "Templates/XML/XMLWizard",
                "Templates/Other/Folder"
    };
    
    private static final String[] EJB_TYPES = new String[] {
                "java-classes",         // NOI18N
                "ejb-types",            // NOI18N
                "web-services",         // NOI18N
                "wsdl",                 // NOI18N
                "j2ee-types",           // NOI18N
                "java-beans",           // NOI18N
                "java-main-class",      // NOI18N
                "oasis-XML-catalogs",   // NOI18N
                "XML",                  // NOI18N
                "ant-script",           // NOI18N
                "ant-task",             // NOI18N
                "junit",                // NOI18N
                "simple-files"          // NOI18N
    };
    
    private static final String[] EJB_PRIVILEGED_NAMES = new String[] {
        
                "Templates/J2EE/Session", // NOI18N
                "Templates/J2EE/Entity",  // NOI18N
                "Templates/J2EE/RelatedCMP", // NOI18N
                "Templates/J2EE/Message", //NOI18N
//                "Templates/WebServices/WebService", // NOI18N
                "Templates/WebServices/MessageHandler", // NOI18N
                "Templates/Classes/Class.java" // NOI18N
    };
    
    private static final String[] WEB_TYPES = new String[] {
                "java-classes",         // NOI18N
                "java-main-class",      // NOI18N
                "java-beans",           // NOI18N
                "oasis-XML-catalogs",   // NOI18N
                "XML",                  // NOI18N
                "ant-script",           // NOI18N
                "ant-task",             // NOI18N
                "servlet-types",        // NOI18N
                "web-types",            // NOI18N
//                "web-services",         // NOI18N
//                "web-service-clients",  // NOI18N
                "wsdl",                 // NOI18N
                "j2ee-types",           // NOI18N
                "junit",                // NOI18N
                "simple-files"          // NOI18N
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
        String packaging = project.getOriginalMavenProject().getPackaging();
        if (packaging == null) {
            packaging = "jar";
        }
        packaging = packaging.trim();
        if ("ejb".equals(packaging)) {
            return EJB_TYPES;
        }
        if ("ear".equals(packaging)) {
            return EAR_TYPES;
        }
        if ("war".equals(packaging)) {
            return WEB_TYPES;
        }
        return new String[0];
    }
    
    public String[] getPrivilegedTemplates() {
        String packaging = project.getOriginalMavenProject().getPackaging();
        if (packaging == null) {
            packaging = "jar";
        }
        packaging = packaging.trim();
        if ("ejb".equals(packaging)) {
            return EJB_PRIVILEGED_NAMES;
        }
        if ("ear".equals(packaging)) {
            return EAR_PRIVILEGED_NAMES;
        }
        if ("war".equals(packaging)) {
            return WEB_PRIVILEGED_NAMES;
        }
        
        return new String[0];
    }
    
}
