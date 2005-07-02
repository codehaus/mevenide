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

/**
 * @author Arik
 */
public final class ChildrenFetchService {
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
        return service.submit(new ChildFetcher(pPathElement));
    }

    private class ChildFetcher implements Callable<RepoPathElement[]> {
        private final RepoPathElement pathElement;

        public ChildFetcher(final RepoPathElement pPathElement) {
            pathElement = pPathElement;
        }

        public RepoPathElement[] call() throws Exception {
            return pathElement.getChildren();
        }
    }
}
