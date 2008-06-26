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

package org.netbeans.modules.maven.profiler;

import java.io.InputStream;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.spi.actions.AbstractMavenActionsProvider;
import org.netbeans.api.project.Project;
import org.openide.util.Lookup;

/**
 *
 * @author mkleint
 */
public class ProfilerActionsProvider extends AbstractMavenActionsProvider {


    @Override
    public boolean isActionEnable(String action, Project project, Lookup lookup) {
        if (!action.equals("profile")) {
            return false;
        }
        NbMavenProject mavenprj = project.getLookup().lookup(NbMavenProject.class);
        String type = mavenprj.getPackagingType();
        if (NbMavenProject.TYPE_JAR.equals(type)) {
            //TODO
        }
        return super.isActionEnable(action, project, lookup);
    }

    @Override
    protected InputStream getActionDefinitionStream() {
            String path = "/org/netbeans/modules/maven/profiler/ActionMappings.xml"; //NOI18N
            InputStream in = getClass().getResourceAsStream(path);
            assert in != null : "no instream for " + path; //NOI18N
            return in;
    }

}
