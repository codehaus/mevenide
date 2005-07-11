package org.mevenide.idea.project.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SelectFromListDialog;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.psi.PomModelManager;
import org.mevenide.idea.psi.project.PsiDependencies;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.repository.Artifact;
import org.mevenide.idea.repository.PomRepoManager;
import org.mevenide.idea.util.FileUtils;
import org.mevenide.idea.util.ui.MultiLineLabel;

/**
 * @author Arik
 */
public final class PomUtils {
    private static final String DEFAULT_TITLE = "Please select a project";

    public static boolean isArtifactDeclared(final Project pProject,
                                             final String pPomUrl,
                                             final Artifact pArtifact) {
        //
        //get the pom PSI model
        //
        final PsiProject psi = PomModelManager.getInstance(pProject).getPsiProject(pPomUrl);
        final PsiDependencies deps = psi.getDependencies();

        //
        //iterate the pom dependencies and collect the artifacts that go in the classpath
        //ignoring everything that has a type that differs from "jar" (empty type is
        //ok, since the default is "jar")
        //
        final Artifact fullArtifact = pArtifact.getCompleteArtifact();
        for (int row = 0; row < deps.getRowCount(); row++) {
            final Artifact artifact = new Artifact();
            artifact.setGroupId(deps.getGroupId(row));
            artifact.setArtifactId(deps.getArtifactId(row));
            artifact.setType(deps.getType(row));
            artifact.setVersion(deps.getVersion(row));
            artifact.setExtension(deps.getExtension(row));
            if (artifact.getCompleteArtifact().equals(fullArtifact))
                return true;
        }

        //
        //if the pom extends another pom, aggregate it as well
        //
        final PsiProject parentPsi = psi.getParent();
        if (parentPsi != null) {
            final String path = parentPsi.getVirtualFile().getPath();
            final String url = "file://" + path;
            return isArtifactDeclared(pProject, url, pArtifact);
        }

        return false;
    }

    public static Artifact[] getPomClassPathArtifacts(final Project pProject,
                                                      final String pPomUrl) {
        //
        //get the pom PSI model
        //
        final PsiProject psi = PomModelManager.getInstance(pProject).getPsiProject(pPomUrl);
        if (psi == null)
            return new Artifact[0];
        final PsiDependencies deps = psi.getDependencies();

        //
        //iterate the pom dependencies and collect the artifacts that go in the classpath
        //ignoring everything that has a type that differs from "jar" (empty type is
        //ok, since the default is "jar")
        //
        final Set<Artifact> artifacts = new HashSet<Artifact>(deps.getRowCount());
        for (int row = 0; row < deps.getRowCount(); row++) {
            final String type = deps.getType(row);
            if (type != null && !"jar".equalsIgnoreCase(type))
                continue;

            final Artifact artifact = new Artifact();
            artifact.setGroupId(deps.getGroupId(row));
            artifact.setArtifactId(deps.getArtifactId(row));
            artifact.setType(type);
            artifact.setVersion(deps.getVersion(row));
            artifact.setExtension(deps.getExtension(row));
            artifacts.add(artifact);
        }

        //
        //if the pom extends another pom, aggregate it as well
        //
        final PsiProject parentPsi = psi.getParent();
        if (parentPsi != null) {
            final String path = parentPsi.getVirtualFile().getPath();
            final String url = "file://" + path;
            final Artifact[] parentDeps = getPomClassPathArtifacts(pProject, url);
            if (parentDeps != null && parentDeps.length > 0)
                for (Artifact parentDepFile : parentDeps)
                    artifacts.add(parentDepFile);
        }

        return artifacts.toArray(new Artifact[artifacts.size()]);
    }

    public static VirtualFile[] getPomClassPathFiles(final Project pProject, final String pPomUrl) {
        final PomRepoManager repoMgr = PomRepoManager.getInstance(pProject);

        final Artifact[] artifacts = getPomClassPathArtifacts(pProject, pPomUrl);
        final Set<VirtualFile> files = new HashSet<VirtualFile>(artifacts.length);

        for (Artifact artifact : artifacts) {
            final VirtualFile file = repoMgr.findFile(pPomUrl, artifact);
            if (file != null && file.isValid() && !file.isDirectory() && FileUtils.exists(file))
                files.add(file);
        }

        return files.toArray(new VirtualFile[files.size()]);
    }

    public static String selectPom(final Project pProject) {
        return selectPom(pProject, null, null, null);
    }

    public static String selectPom(final Project pProject,
                                   final String pTitle) {
        return selectPom(pProject, null, pTitle, null);
    }

    public static String selectPom(final Project pProject,
                                   final String[] pPomUrls) {
        return selectPom(pProject, pPomUrls, null, null);
    }

    public static String selectPom(final Project pProject,
                                   final String[] pPomUrls,
                                   final String pTitle) {
        return selectPom(pProject, pPomUrls, pTitle, null);
    }

    public static String selectPom(final Project pProject,
                                   final String pTitle,
                                   final String pLabel) {
        return selectPom(pProject, null, pTitle, pLabel);
    }

    public static String selectPom(final Project pProject,
                                   String[] pPomUrls,
                                   String pTitle,
                                   final String pLabel) {
        if (pPomUrls == null || pPomUrls.length == 0) {
            final PomManager mgr = PomManager.getInstance(pProject);
            pPomUrls = mgr.getFileUrls();
        }

        if (pPomUrls == null || pPomUrls.length == 0)
            return null;
        else {
            final Set<String> urls = new HashSet<String>(pPomUrls.length);
            for (String url : pPomUrls)
                if (url != null && url.trim().length() > 0)
                    urls.add(url);

            pPomUrls = urls.toArray(new String[urls.size()]);
            if (pPomUrls.length == 1)
                return pPomUrls[0];
        }

        if (pTitle == null || pTitle.trim().length() == 0)
            pTitle = DEFAULT_TITLE;

        final SelectFromListDialog dlg = new SelectFromListDialog(
                pProject,
                pPomUrls,
                new SelectFromListDialog.ToStringAspect() {
                    public String getToStirng(Object obj) {
                        return PathUtil.toPresentableUrl(obj.toString());
                    }
                },
                pTitle,
                ListSelectionModel.SINGLE_SELECTION);

        if (pLabel != null && pLabel.trim().length() > 0)
            dlg.addToDialog(new MultiLineLabel(pLabel), BorderLayout.PAGE_START);

        dlg.setModal(true);
        dlg.setResizable(true);
        dlg.show();

        if (!dlg.isOK())
            return null;

        return dlg.getSelection()[0].toString();
    }
}
