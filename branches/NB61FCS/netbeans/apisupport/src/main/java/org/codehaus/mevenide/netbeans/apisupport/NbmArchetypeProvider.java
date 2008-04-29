/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.netbeans.apisupport;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.mevenide.netbeans.api.archetype.Archetype;
import org.codehaus.mevenide.netbeans.api.archetype.ArchetypeProvider;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
public class NbmArchetypeProvider implements ArchetypeProvider {

    public List<Archetype> getArchetypes() {
        List<Archetype> lst = new ArrayList<Archetype>();
        Archetype a = new Archetype(false, true);
        a.setGroupId("org.codehaus.mojo.archetypes"); //NOI18N
        a.setArtifactId("nbm-archetype"); //NOI18N
        a.setVersion("1.0.1"); //NOI18N
        a.setName(NbBundle.getMessage(NbmArchetypeProvider.class, "TIT_NBM_Archetype"));
        a.setDescription(NbBundle.getMessage(NbmArchetypeProvider.class, "DESC_NBM_Archetype"));
        lst.add(a);
        a = new Archetype(false, true);
        a.setGroupId("org.codehaus.mojo.archetypes"); //NOI18N
        a.setArtifactId("netbeans-platform-app-archetype"); //NOI18N
        a.setVersion("1.0.1"); //NOI18N
        a.setName(NbBundle.getMessage(NbmArchetypeProvider.class, "TIT_Platform_Application_Archetype"));
        a.setDescription(NbBundle.getMessage(NbmArchetypeProvider.class, "DESC_Platform_Application_Archetype"));
        lst.add(a);
        return lst;
    }

}
