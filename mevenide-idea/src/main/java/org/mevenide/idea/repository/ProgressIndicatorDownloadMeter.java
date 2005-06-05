package org.mevenide.idea.repository;

import com.intellij.openapi.progress.ProgressIndicator;
import org.apache.maven.util.DownloadMeter;
import org.mevenide.idea.util.IDEUtils;

/**
 * @author Arik
 */
public class ProgressIndicatorDownloadMeter implements DownloadMeter {

    public void finish(final int pTotal) {
        final ProgressIndicator indicator = IDEUtils.getProgressIndicator();
        if (indicator != null)
            indicator.setFraction(1);
    }

    public void update(final int pComplete, final int pTotal) {
        final ProgressIndicator indicator = IDEUtils.getProgressIndicator();
        if (indicator != null)
            indicator.setFraction(pComplete / pTotal);
    }

}
