package org.mevenide.idea.repository.browser;

import java.util.Set;
import javax.swing.*;
import org.apache.commons.lang.ArrayUtils;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public abstract class AbstractBrowserAction extends AbstractAnAction {
    private static final int[] ALL_LEVELS = new int[]{
            RepoPathElement.LEVEL_ARTIFACT,
            RepoPathElement.LEVEL_GROUP,
            RepoPathElement.LEVEL_ROOT,
            RepoPathElement.LEVEL_TYPE,
            RepoPathElement.LEVEL_VERSION
    };

    protected final RepositoryBrowser browser;

    protected AbstractBrowserAction(final RepositoryBrowser pBrowser) {
        browser = pBrowser;
    }

    protected AbstractBrowserAction(final RepositoryBrowser pBrowser,
                                    final String pText) {
        super(pText);
        browser = pBrowser;
    }

    protected AbstractBrowserAction(final RepositoryBrowser pBrowser,
                                    final String pText,
                                    final String pDescription,
                                    final Icon pIcon) {
        super(pText, pDescription, pIcon);
        browser = pBrowser;
    }

    protected RepoPathElement[] getSelectedItems(final int... pLevels) {
        final int[] levels;
        if (pLevels == null || pLevels.length == 0)
            levels = ALL_LEVELS;
        else
            levels = pLevels;

        final RepoPathElement[] selectedItems = browser.getSelectedItems();
        final Set<RepoPathElement> items = new java.util.HashSet<RepoPathElement>(selectedItems.length);

        for (RepoPathElement pathElement : selectedItems)
            if (ArrayUtils.contains(levels, pathElement.getLevel()))
                items.add(pathElement);

        return items.toArray(new RepoPathElement[items.size()]);
    }
}
