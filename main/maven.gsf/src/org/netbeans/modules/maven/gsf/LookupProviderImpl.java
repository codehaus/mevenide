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

package org.netbeans.modules.maven.gsf;

import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.LookupProvider;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author mkleint
 */
public class LookupProviderImpl implements LookupProvider {

    public Lookup createAdditionalLookup(Lookup baseContext) {

        NbMavenProject nbprj = baseContext.lookup(NbMavenProject.class);
        Project prj = baseContext.lookup(Project.class);
        assert prj != null;
        assert nbprj != null;
        CPProvider cpp = new CPProvider(prj);
        //TODO add the classpathprovider
        return Lookups.fixed(new GSFRecoTemplates(),
                new ProjectOpenedHookImpl(prj), 
                cpp,
                new GsfMavenSourcesImpl(cpp, prj));
    }

    private static class GSFRecoTemplates implements RecommendedTemplates {

        public String[] getRecommendedTypes() {
            return new String[] {
                "scala-classes"
            };
        }

    }

}
