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
package org.codehaus.mevenide.netbeans.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;

/**
 * Various maven model related utilities.
 * @author mkleint
 * @author Anuradha G
 */
public final class ModelUtils {

    /**
     * Get all possible profiles defined .
     * 
     * @param pom
     * @return
     */
    public static List<String> retrieveAllProfiles(MavenProject mavenProject) {
        Set<String> values = new HashSet<String>();
        MavenProject root = getRootMavenProject(mavenProject);

        /** BEGIN
         * Sometimes Need load project again  to get getCollectedProjects correctly 
         * may be project.getParent() related. to: Milos any Idea ?
         */
        MavenExecutionRequest req = new DefaultMavenExecutionRequest();
        req.setPomFile(root.getFile().getAbsolutePath());
        MavenExecutionResult res = EmbedderFactory.getProjectEmbedder().readProjectWithDependencies(req);
        root = res.getProject();
        /*END*************************************************************/

        List<MavenProject> mps = root.getCollectedProjects();
        for (MavenProject mp : mps) {

            Model model = mp.getModel();
            List<Profile> profiles = model.getProfiles();
            for (Profile profile : profiles) {
                values.add(profile.getId());
            }
        }
        return new ArrayList<String>(values);
    }

    private static MavenProject getRootMavenProject(MavenProject mavenProject) {
        if (mavenProject.getParent() != null) {

            return getRootMavenProject(mavenProject.getParent());
        }

        return mavenProject;
    }
}
