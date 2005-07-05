package org.mevenide.idea.repository.tree.model;

import java.util.concurrent.CancellationException;

/**
 * @author Arik
 */
public interface FetchStatusListener {

    void fetchStarted(final RepoTreeNode pNode);

    void fetchComplete(final RepoTreeNode pNode);

    void fetchCancelled(final RepoTreeNode pNode,
                        final CancellationException pCause);

    void fetchError(final RepoTreeNode pNode, final Exception pCause);

    void fetchInterrupted(final RepoTreeNode pNode,
                          final InterruptedException pCause);

}
