package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.project.Project;
import com.sun.java_cup.internal.version;
import java.util.HashSet;
import java.util.Set;
import org.mevenide.idea.Res;
import org.mevenide.idea.repository.PomRepoManager;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.PsiDependencies;
import org.mevenide.idea.psi.PomModelManager;
import org.mevenide.idea.synchronize.*;
import org.mevenide.idea.synchronize.inspections.AbstractInspector;

/**
 * @author Arik
 */
public class DependencyNotDownloadedInspector extends AbstractInspector implements ProjectProblemInspector {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(DependencyNotDownloadedInspector.class);

    public DependencyNotDownloadedInspector() {
        super(RES.get("dep.missing.inspector.name"), RES.get("dep.missing.inspector.desc"));
    }

    public ProblemInfo[] inspect(String pPomUrl, Project pProject) {
        //
        //buffer for the set of problems we'll encounter
        //
        final Set<ProblemInfo> problems = new HashSet<ProblemInfo>();

        //
        //iterate over the project dependencies, and check each one if it
        //exists in the local repository
        //
        final PomRepoManager repoMgr = PomRepoManager.getInstance(pProject);
        final PomModelManager modelMgr = PomModelManager.getInstance(pProject);

        final PsiProject psi = modelMgr.getPsiProject(pPomUrl);
        final PsiDependencies deps = psi.getDependencies();
        for(int row = 0; row < deps.getRowCount(); row++) {
            String type = deps.getType(row);
            if(type == null || type.trim().length() == 0)
                type = "jar";

            if(!"jar".equalsIgnoreCase(type) || !"ejb".equalsIgnoreCase(type))
                continue;

            final String groupId = deps.getGroupId(row);
            final String artifactId = deps.getArtifactId(row);
            final String version = deps.getVersion(row);
            final String extension = deps.getExtension(row);
            if(!repoMgr.isInstalled(pPomUrl, groupId, artifactId, type, version, extension))
                problems.add(new DependencyNotDownloadedProblemInfo(pProject,
                                                                    this,
                                                                    pPomUrl,
                                                                    groupId,
                                                                    artifactId,
                                                                    type,
                                                                    version,
                                                                    extension));
        }

        return problems.toArray(new ProblemInfo[problems.size()]);
    }

    private class DependencyNotDownloadedProblemInfo extends AbstractArtifactProblemInfo {

        public DependencyNotDownloadedProblemInfo(final Project pProject,
                                                  final ProblemInspector pInspector,
                                                  final String pPomUrl,
                                                  final String pGroupId,
                                                  final String pArtifactId,
                                                  final String pType,
                                                  final String pVersion,
                                                  final String pExtension) {
            super(pInspector,
                  pProject,
                  pPomUrl,
                  RES.get("dep.missing.problem.desc",
                          PomRepoManager.getPresentableName(pGroupId,
                                                            pArtifactId,
                                                            pType,
                                                            pVersion,
                                                            pExtension)),
                  pGroupId,
                  pArtifactId,
                  pType,
                  pVersion,
                  pExtension);

            addFixAction(new DownloadDependencyAction(this));
            addFixAction(new RemoveDependencyFromPomAction(this));
        }

        public boolean isValid() {
            return PomRepoManager.getInstance(project).isInstalled(pomUrl,
                                                                   groupId,
                                                                   artifactId,
                                                                   type,
                                                                   version,
                                                                   extension);
        }
    }
}
