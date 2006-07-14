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
 *
 * @author mkleint
 */
public class MockArchetypeProvider implements ArchetypeProvider {
    
    /** Creates a new instance of MockArchetypeProvider */
    public MockArchetypeProvider() {
    }

    public List getArchetypes() {
        List toRet = new ArrayList();
        Archetype simple = new Archetype();
        simple.setArtifactId("maven-archetype-quickstart");
        simple.setGroupId("org.apache.maven.archetypes");
        simple.setVersion("1.0-alpha-4");
        simple.setName("Quickstart project");
        simple.setDescription("The very basic setup.");
        toRet.add(simple);
        simple = new Archetype();
        simple.setArtifactId("maven-archetype-webapp");
        simple.setGroupId("org.apache.maven.archetypes");
        simple.setVersion("1.0-alpha-4");
        simple.setName("Simple webapp project");
        simple.setDescription("The basic setup for a webapp project.");
        toRet.add(simple);
        simple = new Archetype();
        simple.setArtifactId("maven-archetype-mojo");
        simple.setGroupId("org.apache.maven.archetypes");
        simple.setVersion("1.0-alpha-4");
        simple.setName("Mojo (maven plugin) project");
        simple.setDescription("The basic setup for a maven plugin project.");
        toRet.add(simple);
        return toRet;
    }
    
}
