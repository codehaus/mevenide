package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.project.Project;
import java.util.HashSet;
import java.util.Set;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.util.PomUtils;
import org.mevenide.idea.repository.Artifact;
import org.mevenide.idea.repository.PomRepoManager;
import org.mevenide.idea.synchronize.AbstractProblemInfo;
import org.mevenide.idea.synchronize.ArtifactProblemInfo;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.synchronize.ProjectProblemInspector;
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
        final Artifact[] artifacts = PomUtils.getPomClassPathArtifacts(pProject, pPomUrl);
        for (Artifact artifact : artifacts) {
            if(!repoMgr.isInstalled(pPomUrl, artifact))
                problems.add(new DependencyNotDownloadedProblemInfo(pProject,
                                                                    pPomUrl,
                                                                    artifact));
        }

        return problems.toArray(new ProblemInfo[problems.size()]);
    }

    private class DependencyNotDownloadedProblemInfo extends AbstractProblemInfo implements ArtifactProblemInfo {
        private final Artifact artifact;

        public DependencyNotDownloadedProblemInfo(final Project pProject,
                                                  final String pPomUrl,
                                                  final Artifact pArtifact) {
            super(DependencyNotDownloadedInspector.this,
                  pProject,
                  pPomUrl,
                  RES.get("dep.missing.problem.desc", pArtifact));

            artifact = pArtifact;
            addFixAction(new DownloadDependencyAction(this));
            addFixAction(new RemoveDependencyFromPomAction(this));
        }

        public Artifact getArtifact() {
            return artifact;
        }

        public boolean isValid() {
            return !PomRepoManager.getInstance(project).isInstalled(pomUrl, artifact);
        }
    }
}
