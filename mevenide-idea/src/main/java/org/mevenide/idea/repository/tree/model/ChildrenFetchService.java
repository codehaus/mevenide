package org.mevenide.idea.repository.tree.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Arik
 */
public final class ChildrenFetchService {
    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog(ChildrenFetchService.class);

    /**
     * The singleton instance.
     */
    private static final ChildrenFetchService INSTANCE = new ChildrenFetchService();

    /**
     * Available executors
     */
    private final Map<IRepositoryReader, ExecutorService> executors;

    private ChildrenFetchService() {
        final HashMap<IRepositoryReader, ExecutorService> buffer;
        buffer = new HashMap<IRepositoryReader, ExecutorService>(3);
        executors = Collections.synchronizedMap(buffer);
    }

    public static ChildrenFetchService getInstance() {
        return INSTANCE;
    }

    public Future<RepoPathElement[]> fetch(final RepoPathElement pPathElement) {
        final IRepositoryReader repo = pPathElement.getReader();

        if (!executors.containsKey(repo))
            executors.put(repo, Executors.newSingleThreadExecutor());

        final ExecutorService service = executors.get(repo);
        LOG.trace("Submitting fetch task for repo executor " + repo.getRootURI());
        return service.submit(new ChildFetcher(pPathElement));
    }

    private class ChildFetcher implements Callable<RepoPathElement[]> {
        private final RepoPathElement pathElement;

        public ChildFetcher(final RepoPathElement pPathElement) {
            pathElement = pPathElement;
        }

        public RepoPathElement[] call() throws Exception {
            try {
                LOG.trace("Fetching RepoPathElement's children");
                return pathElement.getChildren();
            }
            catch (Exception e) {
                LOG.error(e, e);
                throw e;
            }
            finally {
                LOG.trace("Fetched RepoPathElement's children");
            }
        }
    }
}
