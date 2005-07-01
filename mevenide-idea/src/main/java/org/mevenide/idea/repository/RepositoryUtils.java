package org.mevenide.idea.repository;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.project.Dependency;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.idea.module.ModuleLocationFinder;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.repository.IRepositoryReader;
import static org.mevenide.repository.RepositoryReaderFactory.createLocalRepositoryReader;
import static org.mevenide.repository.RepositoryReaderFactory.createRemoteRepositoryReader;

/**
 * @author Arik
 */
public abstract class RepositoryUtils {
    public static boolean isArtifactInstalled(final VirtualFile pLocalRepo,
                                              final Dependency dep) {
        final String relPath = RepositoryUtils.getDependencyRelativePath(dep);
        final VirtualFile depFile = pLocalRepo.findFileByRelativePath(relPath);
        return depFile != null;
    }

    public static IRepositoryReader[] createRepoReaders(final Project pProject) {
        final Module[] modules = ModuleManager.getInstance(pProject).getModules();
        return createRepoReaders(modules);
    }

    public static IRepositoryReader createLocalRepoReader(final Module pModule) {
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(pModule);
        final IQueryContext context = moduleSettings.getQueryContext();
        if (context == null)
            return null;

        final ILocationFinder finder = new ModuleLocationFinder(pModule);
        final String localRepoPath = finder.getMavenLocalRepository();
        final File localRepoFile = new File(localRepoPath);
        final URI localRepoUri = localRepoFile.toURI();
        String localRepo = localRepoUri.toString();

        if (localRepo.startsWith("file://"))
            localRepo = localRepo.substring(7);
        else
            localRepo = localRepo.substring(6);

        final File repoFile = new File(localRepo);
        return createLocalRepositoryReader(repoFile);
    }

    public static IRepositoryReader[] createRepoReaders(final Module... pModules) {
        return createRepoReaders(true, pModules);
    }

    public static IRepositoryReader[] createRepoReaders(final boolean pIncludeLocal,
                                                        final Module... pModules) {
        final Set<String> repoUris = new HashSet<String>();

        for (final Module module : pModules) {
            final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
            final IQueryContext context = moduleSettings.getQueryContext();
            if (context == null)
                continue;

            final String remoteRepos = context.getPropertyValue("maven.repo.remote");
            repoUris.add(remoteRepos);

            if (pIncludeLocal) {
                final ILocationFinder finder = new ModuleLocationFinder(module);
                final String localRepoPath = finder.getMavenLocalRepository();
                final File localRepoFile = new File(localRepoPath);
                final URI localRepoUri = localRepoFile.toURI();
                final String localRepo = localRepoUri.toString();
                repoUris.add(localRepo);
            }
        }

        final Set<String> finalReposSet = new HashSet<String>(repoUris.size());
        for (final String repo : repoUris) {
            final String[] repos = repo.split(",");
            for (String singleRepo : repos)
                finalReposSet.add(singleRepo.trim());
        }

        final IRepositoryReader[] readers = new IRepositoryReader[finalReposSet.size()];
        int index = 0;
        for (String repo : finalReposSet) {
            if (repo.startsWith("file:/")) {
                if (repo.startsWith("file://"))
                    repo = repo.substring(7);
                else
                    repo = repo.substring(6);

                final File repoFile = new File(repo);
                readers[index++] = createLocalRepositoryReader(repoFile);
            }
            else {
                final URI uri = URI.create(repo);
                readers[index++] = createRemoteRepositoryReader(uri);
            }
        }

        return readers;
    }

    public static String getDependencyRelativePath(final String pGroupId,
                                                   final String pType,
                                                   final String pArtifactId,
                                                   final String pVersion,
                                                   final String pExtension) {
        final String type =
            pType == null || pType.trim().length() == 0 ?
                "jar" :
                pType;

        final String ext =
            pExtension == null || pExtension.trim().length() == 0 ?
                type :
                pExtension;

        final StringBuilder buf = new StringBuilder(100);
        buf.append(pGroupId).append('/');
        buf.append(type).append("s/");
        buf.append(pArtifactId).append('-');
        buf.append(pVersion).append('.');
        buf.append(ext);

        return buf.toString();
    }

    public static String getDependencyRelativePath(final Dependency pDep) {
        return getDependencyRelativePath(pDep.getGroupId(),
                                         pDep.getType(),
                                         pDep.getArtifactId(),
                                         pDep.getVersion(),
                                         pDep.getExtension());
    }

    public static Dependency cloneDependency(final Dependency pDependency) {
        final Dependency dep = new Dependency();
        dep.setArtifactId(pDependency.getArtifactId());
        dep.setGroupId(pDependency.getGroupId());
        dep.setJar(pDependency.getJar());
        dep.setName(pDependency.getName());
        if (pDependency.getType() == null || pDependency.getType().trim().length() == 0)
            dep.setType("jar");
        else
            dep.setType(pDependency.getType());
        dep.setUrl(pDependency.getUrl());
        dep.setVersion(pDependency.getVersion());

        //noinspection UNCHECKED_WARNING
        dep.setProperties(new ArrayList(pDependency.getProperties()));

        return dep;
    }

    public static VirtualFile getLocalRepository(final Module pModule) {

        //
        //make sure this module is "mavenized"
        //
        final ModuleSettings settings = ModuleSettings.getInstance(pModule);
        final IQueryContext ctx = settings.getQueryContext();
        if (ctx == null)
            return null;

        //
        //find the local repository
        //
        final ILocationFinder finder = new ModuleLocationFinder(pModule);
        final String localRepo = finder.getMavenLocalRepository();
        if (localRepo == null || localRepo.trim().length() == 0)
            return null;

        final String url = VfsUtil.pathToUrl(localRepo).replace('\\', '/');
        return VirtualFileManager.getInstance().findFileByUrl(url);
    }

}
