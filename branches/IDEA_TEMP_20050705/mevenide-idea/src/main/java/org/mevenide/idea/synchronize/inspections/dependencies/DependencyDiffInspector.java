package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ModuleUtils;
import org.mevenide.idea.project.util.PomUtils;
import org.mevenide.idea.repository.Artifact;
import org.mevenide.idea.repository.PomRepoManager;
import org.mevenide.idea.synchronize.*;
import org.mevenide.idea.synchronize.inspections.AbstractInspector;

/**
 * @author Arik
 */
public class DependencyDiffInspector extends AbstractInspector implements ModuleProblemInspector {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(DependencyDiffInspector.class);

    public DependencyDiffInspector() {
        super(RES.get("dep.diff.inspector.name"),
              RES.get("dep.diff.inspector.desc"));
    }

    public ProblemInfo[] inspect(String pPomUrl, Module pModule) {

        //
        //buffer for the set of problems we'll encounter
        //
        final Set<ProblemInfo> problems = new HashSet<ProblemInfo>();

        //
        //find dependencies defined in POM and not in the IDEA module
        //
        findDepsMissingFromIdea(problems, pPomUrl, pModule);

        //
        //find libraries defined in IDEA module and missing from POM
        //
        findLibsMissingFromPom(problems, pPomUrl, pModule);

        //
        //return the problems found
        //
        return problems.toArray(new ProblemInfo[problems.size()]);
    }

    protected final void findLibsMissingFromPom(final Set<ProblemInfo> pProblemBuffer,
                                                final String pPomUrl,
                                                final Module pModule) {
        final VirtualFile[] pomLibs = PomUtils.getPomClassPathFiles(pModule.getProject(), pPomUrl);
        final VirtualFile[] ideaLibs = ModuleUtils.getModuleClasspath(pModule);

        for (VirtualFile ideaFile : ideaLibs) {
            if(!ideaFile.isValid())
                continue;

            boolean found = false;
            for (VirtualFile pomFile : pomLibs)
                if(pomFile.isValid() && pomFile.equals(ideaFile)) {
                    found = true;
                    break;
                }

            if(!found)
                pProblemBuffer.add(new LibraryMissingFromPomProblem(pPomUrl, pModule, ideaFile));
        }
    }

    protected final void findDepsMissingFromIdea(final Set<ProblemInfo> pProblemBuffer,
                                                 final String pPomUrl,
                                                 final Module pModule) {
        final Artifact[] pomLibs = PomUtils.getPomClassPathArtifacts(pModule.getProject(), pPomUrl);
        final VirtualFile[] ideaLibs = ModuleUtils.getModuleClasspath(pModule);

        final PomRepoManager repoMgr = PomRepoManager.getInstance(pModule.getProject());
        for (Artifact artifact : pomLibs) {
            final VirtualFile file = repoMgr.findFile(pPomUrl, artifact);
            if(file == null || !file.isValid())
                continue;

            boolean found = false;
            for (VirtualFile ideaFile : ideaLibs)
                if (ideaFile.isValid() && ideaFile.equals(file)) {
                    found = true;
                    break;
                }

            if (!found)
                pProblemBuffer.add(new DependencyMissingInIdeaProblem(pModule, artifact));
        }
    }

    private class DependencyMissingInIdeaProblem extends AbstractModuleProblemInfo implements
                                                                                   ModuleArtifactProblemInfo {
        private final Artifact artifact;

        public DependencyMissingInIdeaProblem(final Module pModule, final Artifact pArtifact) {
            super(DependencyDiffInspector.this,
                  pModule,
                  RES.get("dep.missing.from.idea.problem",
                          pArtifact,
                          pModule.getName()));
            artifact = pArtifact;

            addFixAction(new AddDependencyToIdeaAction(this));
            addFixAction(new RemoveDependencyFromPomAction(this));
        }

        public Artifact getArtifact() {
            return artifact;
        }

        public boolean isValid() {
            if (!PomUtils.isArtifactDeclared(getProject(), pomUrl, artifact))
                return false;

            final PomRepoManager repoMgr = PomRepoManager.getInstance(getProject());
            final VirtualFile file = repoMgr.findFile(pomUrl, artifact);
            if(file == null || !file.isValid())
                return false;

            return !ModuleUtils.isFileInClasspath(module, file);
        }
    }

    private class LibraryMissingFromPomProblem extends AbstractModuleProblemInfo implements
                                                                                 FileProblemInfo {
        private final VirtualFile libraryFile;

        public LibraryMissingFromPomProblem(final String pPomUrl,
                                            final Module pModule,
                                            final VirtualFile pLibraryFile) {
            super(DependencyDiffInspector.this,
                  pPomUrl,
                  RES.get("lib.missing.from.pom.problem",
                          pLibraryFile.getPresentableUrl(),
                          pModule.getName()),
                  pModule);
            libraryFile = pLibraryFile;

            //
            //find the local repository - if the file is not under the
            //local repo, we cannot derive the group and artifact ids,
            //and therefor we cannot fix the problem (return empty array)
            //
            final PomRepoManager pomMgr = PomRepoManager.getInstance(module.getProject());
            final VirtualFile localRepo = pomMgr.getLocalRepositoryDirectory(pPomUrl);
            if (localRepo != null && VfsUtil.isAncestor(localRepo, libraryFile, true)) {
                addFixAction(new AddLibraryToPomAction(this, module));
                addFixAction(new RemoveLibraryFromModuleAction(this, module));
            }
        }

        public VirtualFile getFile() {
            return libraryFile;
        }

        public boolean isValid() {
            final VirtualFile[] ideaLibs = ModuleUtils.getModuleClasspath(module);
            if(!ArrayUtils.contains(ideaLibs, libraryFile))
                return false;

            //
            //search for the file in the POM dependencies - if found, then
            //the problem is no longer relevant, and return false. Otherwise
            //return true
            //
            final VirtualFile[] pomLibs = PomUtils.getPomClassPathFiles(project, pomUrl);
            return ArrayUtils.contains(pomLibs, libraryFile);
        }
    }
}
