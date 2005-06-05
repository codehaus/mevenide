package org.mevenide.idea.repository;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import org.mevenide.context.IQueryContext;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.repository.IRepositoryReader;
import static org.mevenide.repository.RepositoryReaderFactory.createRemoteRepositoryReader;

/**
 * @author Arik
 */
public abstract class RepositoryUtils {

    public static IRepositoryReader[] createRepoReaders(final Project pProject) {
        final Module[] modules = ModuleManager.getInstance(pProject).getModules();
        return createRepoReaders(modules);
    }

    public static IRepositoryReader[] createRepoReaders(final Module... pModules) {
        final Set<String> repoUris = new HashSet<String>();

        for (final Module module : pModules) {
            final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
            final IQueryContext context = moduleSettings.getQueryContext();
            if (context == null)
                continue;

            final String remoteRepos = context.getPropertyValue("maven.repo.remote");
            repoUris.add(remoteRepos);

//            final ILocationFinder finder = new ModuleLocationFinder(module);
//            repoUris.add(finder.getMavenLocalRepository());
        }

        final Set<String> finalReposSet = new HashSet<String>(repoUris.size());
        for (final String repo : repoUris) {
            final String[] repos = repo.split(",");
            for (String singleRepo : repos)
                finalReposSet.add(singleRepo.trim());
        }

        final IRepositoryReader[] readers = new IRepositoryReader[finalReposSet.size()];
        int index = 0;
        for (final String repo : finalReposSet) {
            final URI uri = URI.create(repo);
            readers[index++] = createRemoteRepositoryReader(uri);
        }

        return readers;
    }
}
