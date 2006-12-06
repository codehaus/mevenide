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

package org.codehaus.mevenide.netbeans.newproject;

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
        Archetype simple = new Archetype();
        simple.setArtifactId("maven-archetype-quickstart");
        simple.setGroupId("org.apache.maven.archetypes");
        simple.setVersion("1.0");
        simple.setName("Maven Quickstart Archetype");
        simple.setDescription("An archetype which contains a sample Maven project.");
        toRet.add(simple);
        simple = new Archetype();
        simple.setArtifactId("maven-archetype-webapp");
        simple.setGroupId("org.apache.maven.archetypes");
        simple.setVersion("1.0");
        simple.setName("Maven Webapp Archetype");
        simple.setDescription("An archetype which contains a sample Maven Webapp project");
        toRet.add(simple);
        simple = new Archetype();
        simple.setArtifactId("maven-archetype-mojo");
        simple.setGroupId("org.apache.maven.archetypes");
        simple.setVersion("1.0");
        simple.setName("Maven Mojo Archetype");
        simple.setDescription("An archetype which contains a sample Maven plugin.");
        toRet.add(simple);
        return toRet;
    }
    
}
