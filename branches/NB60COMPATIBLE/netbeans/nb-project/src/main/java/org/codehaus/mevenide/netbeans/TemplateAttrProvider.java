/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

package org.codehaus.mevenide.netbeans;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.License;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.CreateFromTemplateAttributesProvider;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

/**
 *
 * @author mkleint
 */
public class TemplateAttrProvider implements CreateFromTemplateAttributesProvider {
    private NbMavenProject project;
    /**
     * pom.xml property that hints netbeans to use a given license template.
     */ 
    public static final String HINT_LICENSE = "netbeans.hint.license"; //NOI18N
    
    TemplateAttrProvider(NbMavenProject prj) {
        project = prj;
    }
    
    public Map<String, ?> attributesFor(DataObject template, DataFolder target, String name) {
        String license = project.getOriginalMavenProject().getProperties().getProperty(HINT_LICENSE); //NOI18N
        if (license == null) {
            // try to match the project's license URL and the mavenLicenseURL attribute of license template
            List lst = project.getOriginalMavenProject().getLicenses();
            if (lst != null && lst.size() > 0) {
                String url = ((License)lst.get(0)).getUrl();
                FileObject licenses = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Templates/Licenses");
                if (url != null && licenses != null) {
                    for (FileObject fo : licenses.getChildren()) {
                        String str = (String)fo.getAttribute("mavenLicenseURL"); //NOI18N
                        if (str != null && str.equalsIgnoreCase(url)) {
                            license = fo.getName().substring("license-".length()); //NOI18N
                            break;
                        }
                    }
                }
            }
        }
        if (license != null) {
            return Collections.singletonMap("project", Collections.singletonMap("license", license)); // NOI18N
        }
        return null;
    }
}
