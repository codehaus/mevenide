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
package org.codehaus.mevenide.buildplan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.apache.maven.lifecycle.NoSuchPhaseException;
import org.apache.maven.lifecycle.model.MojoBinding;
import org.apache.maven.lifecycle.model.Phase;
import org.apache.maven.lifecycle.plan.BuildPlan;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.buildplan.nodes.MojoNode;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class BuildPlanUtil {

    public static final String PHASE_NONE_SPECIFIED = NbBundle.getMessage(MojoNode.class, "LBL_None_Specified");

    private BuildPlanUtil() {
    }

    public static BuildPlanGroup getMojoBindingsGroupByPhase(BuildPlan buildPlan) throws NoSuchPhaseException {
        BuildPlanGroup bpg = new BuildPlanGroup();
        List<MojoBinding> bindings = buildPlan.renderExecutionPlan(new Stack());


        for (MojoBinding mb : bindings) {
            Phase phase = mb.getPhase();
            if (mb.getGroupId().equals("org.apache.maven.plugins.internal") //NOi18N
                    && mb.getArtifactId().equals("maven-state-management")) {
                //ignore this
                continue;
            }

            String phaseKey = PHASE_NONE_SPECIFIED;
            if (phase != null) {
                phaseKey = phase.getName();
            }
            bpg.putMojoBinding(phaseKey, mb);
        }





        buildPlan.resetExecutionProgress();
        return bpg;

    }

    /**ref by ModulesNode
     *todo : move to some api utils class
     */
    public static Collection<MavenProject> getSubProjects(MavenProject project) {
        Collection<MavenProject> modules = new ArrayList<MavenProject>();
        File base = project.getBasedir();
        for (Iterator it = project.getModules().iterator(); it.hasNext();) {
            String elem = (String) it.next();
            File projDir = FileUtil.normalizeFile(new File(base, elem));
            FileObject fo = FileUtil.toFileObject(projDir);
            if (fo != null) {
                try {
                    Project prj = ProjectManager.getDefault().findProject(fo);
                    if (prj != null && prj.getLookup().lookup(NbMavenProject.class) != null) {
                        modules.add(((NbMavenProject) prj).getOriginalMavenProject());
                    }
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return modules;
    }
}
