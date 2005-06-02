package org.mevenide.idea.repository;

import java.net.URI;
import java.util.*;

import org.mevenide.context.IQueryContext;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;
import static org.mevenide.repository.RepositoryReaderFactory.createRemoteRepositoryReader;

/**
 * A repository reader which aggregates other readers into one virtual repository.
 *
 * <p>This is useful when the user should be presented with a repository tree which
 * consists of several underlying repositories, without displaying several parallel
 * trees.</p>
 *
 * @author Arik
 */
public class AggregatingRepositoryReader implements IRepositoryReader {
    private final IRepositoryReader[] repoReaders;

    public AggregatingRepositoryReader(final IQueryContext... pContext) {
        this(parseRepoList(pContext));
    }

    public AggregatingRepositoryReader(final String... pRepositories) {
        final String[] repoUris = parseRepoList(pRepositories);
        final Set<IRepositoryReader> repoSet = new HashSet<IRepositoryReader>(repoUris.length);
        for (String uri : repoUris) {
            final IRepositoryReader repoReader = createRemoteRepositoryReader(URI.create(uri));
            repoSet.add(repoReader);
        }

        repoReaders = repoSet.toArray(new IRepositoryReader[repoSet.size()]);
    }

    public AggregatingRepositoryReader(final IRepositoryReader... pRepoReaders) {
        repoReaders = pRepoReaders;
    }

    private static String[] parseRepoList(final IQueryContext... pRepositories) {
        if(pRepositories == null)
            return new String[0];

        final Set<String> repoUris = new HashSet<String>(pRepositories.length);
        for (IQueryContext context : pRepositories)
            if(context != null)
                repoUris.add(context.getPropertyValue("maven.repo.remote"));

        return parseRepoList(repoUris.toArray(new String[repoUris.size()]));
    }

    private static String[] parseRepoList(final String... pRepositories) {
        final Set<String> repoUris = new HashSet<String>(pRepositories.length);
        for (String token : pRepositories) {
            if (token == null || token.trim().length() == 0)
                continue;

            if (token.indexOf(',') >= 0) {
                final String[] repos = parseRepoList(token.split(","));
                for(String repo : repos)
                    repoUris.add(repo);
            }
            else
                repoUris.add(token);
        }

        return repoUris.toArray(new String[repoUris.size()]);
    }

    public URI getRootURI() {
        return URI.create("aggregator");
    }

    public RepoPathElement[] readElements(RepoPathElement element) throws Exception {
        final List<RepoPathElement> nodes = new ArrayList<RepoPathElement>(10);
        for (IRepositoryReader reader : repoReaders) {
            final RepoPathElement[] children = reader.readElements(element);
            for (RepoPathElement child : children)
                nodes.add(new RepoPathElement(this,
                                              element,
                                              child.getGroupId(),
                                              child.getType(),
                                              child.getVersion(),
                                              child.getArtifactId(),
                                              child.getExtension()));
        }

        return nodes.toArray(new RepoPathElement[nodes.size()]);
    }
}
