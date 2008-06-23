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

package org.netbeans.maven.newproject;

import org.netbeans.maven.api.archetype.Archetype;
import org.netbeans.maven.api.archetype.ArchetypeProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * Archetype provider that lists the 3 basic ones to have something in the list
 * when the user never used any archetypes before..
 * @author mkleint
 */
public class MockArchetypeProvider implements ArchetypeProvider {
    
    /** Creates a new instance of MockArchetypeProvider */
    public MockArchetypeProvider() {
    }

    public List<Archetype> getArchetypes() {
        List<Archetype> toRet = new ArrayList<Archetype>();
        Archetype simple = new Archetype(false);
        simple.setArtifactId("maven-archetype-quickstart"); //NOI18N
        simple.setGroupId("org.apache.maven.archetypes"); //NOI18N
        simple.setVersion("1.0"); //NOI18N
        simple.setName(org.openide.util.NbBundle.getMessage(MockArchetypeProvider.class, "LBL_Maven_Quickstart_Archetype"));
        simple.setDescription(org.openide.util.NbBundle.getMessage(MockArchetypeProvider.class, "HINT_MavenQuickStart"));
        toRet.add(simple);
        simple = new Archetype(false);
        simple.setArtifactId("maven-archetype-webapp"); //NOI18N
        simple.setGroupId("org.apache.maven.archetypes"); //NOI18N
        simple.setVersion("1.0"); //NOI18N
        simple.setName(org.openide.util.NbBundle.getMessage(MockArchetypeProvider.class, "LBL_Webapp_Archetype"));
        simple.setDescription(org.openide.util.NbBundle.getMessage(MockArchetypeProvider.class, "HINT_Webapp_Archetype"));
        toRet.add(simple);
        simple = new Archetype(false);
        simple.setArtifactId("maven-archetype-mojo"); //NOI18N
        simple.setGroupId("org.apache.maven.archetypes"); //NOI18N
        simple.setVersion("1.0"); //NOI18N
        simple.setName(org.openide.util.NbBundle.getMessage(MockArchetypeProvider.class, "LBL_Mojo_Archetype"));
        simple.setDescription(org.openide.util.NbBundle.getMessage(MockArchetypeProvider.class, "HINT_Mojo_Archetype"));
        toRet.add(simple);
        return toRet;
    }
    
}
