/*
 *  Copyright 2008 Mevenide Team.
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


package org.netbeans.modules.maven.spring;

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.LookupProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author mkleint
 */
public class MavenSpringLookupProvider implements LookupProvider {

    public Lookup createAdditionalLookup(Lookup baseContext) {
        Project project = baseContext.lookup(Project.class);
        if (project == null) {
            throw new IllegalStateException("Lookup " + baseContext + " does not contain a Project");
        }
        return Lookups.singleton(new MavenSpringConfigProviderImpl(project));
    }



}
