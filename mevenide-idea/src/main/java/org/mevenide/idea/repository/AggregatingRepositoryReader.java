package org.mevenide.idea.repository;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mevenide.context.IQueryContext;
import org.mevenide.idea.Res;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;
import static org.mevenide.repository.RepositoryReaderFactory.createRemoteRepositoryReader;

/**
 * @author Arik
 */
public class AggregatingRepositoryReader implements IRepositoryReader {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(AggregatingRepositoryReader.class);

    private final IRepositoryReader[] repoReaders;

    public AggregatingRepositoryReader(final IQueryContext pContext) {
        final String value = pContext.getPropertyValue("maven.repo.remote");
        final String[] repoUris = value.split(",");
        final Set<IRepositoryReader> repoSet = new HashSet<IRepositoryReader>(repoUris.length);
        for (String uri : repoUris) {
            if (uri == null || uri.trim().length() <= 0)
                continue;

            final IRepositoryReader repoReader = createRemoteRepositoryReader(URI.create(uri));
            repoSet.add(repoReader);
        }

        repoReaders = repoSet.toArray(new IRepositoryReader[repoSet.size()]);
    }

    public AggregatingRepositoryReader(final List<IRepositoryReader> pRepoReaders) {
        this(pRepoReaders.toArray(new IRepositoryReader[pRepoReaders.size()]));
    }

    public AggregatingRepositoryReader(final IRepositoryReader... pRepoReaders) {
        repoReaders = pRepoReaders;
    }

    public URI getRootURI() {
        throw new UnsupportedOperationException(
                RES.get("unsupp.op",
                        "getRootURI",
                        "aggregating repository readers have no URI"));
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
