/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.project.dependency;

import org.mevenide.project.io.IContentProvider;
import org.mevenide.project.io.ProxyContentProvider;

/**  
 * implementation of IDependencyReplacer and IDependencyPattern that requires a complete match
 * in dependency's artifactId, groupId and version. 
 *
 * @author Milos Kleint
 * 
 */
public class ExactDependencyReplacer extends ExactDependencyPattern implements IDependencyReplacer {
	
    private String artifact;
    private String group;
    private String version;
    private String newArtifact;
    private String newGroup;
    private String newVersion;
    
    public ExactDependencyReplacer(String artifactId, String groupId, String currentversion,
                                   String nArt, String nGrp, String nVer) {
        super(artifactId, groupId, currentversion);
        version = currentversion;
        artifact = artifactId;
        group = groupId;
        newArtifact = nArt;
        newGroup = nGrp;
        newVersion = nVer;
    }
    
    public IContentProvider replace(IContentProvider original) {
        String id = original.getValue("id"); //NOI18N
        String art = original.getValue("artifactId"); //NOI18N
        String gr = original.getValue("groupId"); //NOI18N
        String ver = original.getValue("version"); //NOI18N
        
        if (matchOne(version, ver)) 
        {
            if ((       matchOne(artifact, art)
                     && matchOne(group, gr))
                ||  matchOne(id, group + ":" + artifact)) 
            {
                return new OneDepContent(original);
            }
            
        }
        return original;
        
    }
    
    private class OneDepContent extends ProxyContentProvider {
        public OneDepContent(IContentProvider original) {
            super(original);
        }

        public String getValue(String key) {
            if ("artifactId".equals(key)) { //NOI18N
                return newArtifact;
            }
            if ("groupId".equals(key)) { //NOI18N
                return newGroup;
            }
            if ("version".equals(key)) { //NOI18N
                return newVersion;
            }
            if ("id".equals(key)) {
                // ignore id, convert to correct groupId+artifactId
                return null;
            }
            return super.getValue(key);
        }
        
    }

}