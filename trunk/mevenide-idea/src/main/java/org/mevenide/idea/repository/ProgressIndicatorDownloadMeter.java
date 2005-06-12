package org.mevenide.idea.repository;

import com.intellij.openapi.progress.ProgressIndicator;
import org.apache.maven.util.DownloadMeter;
import org.mevenide.idea.util.IDEUtils;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Arik
 */
public class ProgressIndicatorDownloadMeter implements DownloadMeter {

    private AtomicBoolean started = new AtomicBoolean(false);

    public void finish(final int pTotal) {
        final ProgressIndicator indicator = IDEUtils.getProgressIndicator();
        if (indicator != null)
            indicator.setFraction(1);
    }

    public void update(final int pComplete, final int pTotal) {
        final ProgressIndicator indicator = IDEUtils.getProgressIndicator();
        if (indicator != null) {
            if(started.compareAndSet(false, true)) 
                indicator.setIndeterminate(false);

            final double fraction = (double)pComplete / (double)pTotal;
            indicator.setFraction(fraction);
        }
    }

}
