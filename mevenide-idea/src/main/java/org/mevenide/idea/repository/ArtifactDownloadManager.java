package org.mevenide.idea.repository;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.util.DownloadMeter;
import org.apache.maven.util.HttpUtils;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.repository.RepoPathElement;
import org.mevenide.idea.Res;
import org.mevenide.environment.ILocationFinder;

/**
 * @author Arik
 */
public class ArtifactDownloadManager {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(ArtifactDownloadManager.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(ArtifactDownloadManager.class);

    private static final ArtifactDownloadManager INSTANCE = new ArtifactDownloadManager();

    public static ArtifactDownloadManager getInstance() {
        return INSTANCE;
    }

    public void downloadArtifact(final ILocationFinder pFinder,
                                 final IPropertyResolver pResolver,
                                 final RepoPathElement pRepoElt) throws IOException {
        if (!pRepoElt.isRemote())
            throw new IllegalArgumentException(RES.get("not.remote.element"));

        if (!pRepoElt.isLeaf()) {
            final RepoPathElement[] elements;
            try {
                elements = pRepoElt.getChildren();
            }
            catch (Exception e) {
                final IOException ex = new IOException(e.getMessage());
                throw (IOException) ex.initCause(e);
            }

            for (RepoPathElement element : elements)
                downloadArtifact(pFinder, pResolver, element);
        }
        else {
            //
            //calculated destination file location
            //
            final File localRepo = new File(pFinder.getMavenLocalRepository());
            final String localFileUri = localRepo.toURI() + pRepoElt.getRelativeURIPath();
            final File destinationFile = new File(URI.create(localFileUri));

            //
            //make sure the directory for the file exists
            //
            destinationFile.getParentFile().mkdirs();

            //
            //use proxy if needed
            //
            String host = pResolver.getResolvedValue("maven.proxy.host");
            String port = pResolver.getResolvedValue("maven.proxy.port");
            String user = pResolver.getResolvedValue("maven.proxy.username");
            String passwd = pResolver.getResolvedValue("maven.proxy.password");
            if (host != null && host.trim().length() == 0)
                host = null;
            if (port != null && port.trim().length() == 0)
                port = null;
            if (user != null && user.trim().length() == 0)
                user = null;
            if (passwd != null && passwd.trim().length() == 0)
                passwd = null;

            //
            //download the file
            //
            HttpUtils.getFile(pRepoElt.getURI().toURL().toString(),     //url to download
                              destinationFile,                          //destination file
                              false,                                    //ignore errors?
                              true,                                     //use timestamp
                              host, port, user, passwd,                 //proxy settings
                              null, null,                               //login settings
                              new ProgressIndicatorDownloadMeter()      //download meter
            );
        }
    }

    private class ProgressIndicatorDownloadMeter implements DownloadMeter {
        public void finish(final int pTotal) {
            final ProgressIndicator indicator = getIndicator();
            if (indicator != null)
                indicator.setFraction(1);
        }

        public void update(final int pComplete, final int pTotal) {
            final ProgressIndicator indicator = getIndicator();
            if (indicator != null)
                indicator.setFraction(pComplete / pTotal);
        }

        private ProgressIndicator getIndicator() {
            final ProgressManager mgr = ProgressManager.getInstance();
            if (!mgr.hasProgressIndicator()) {
                LOG.warn("No progress indicator.");
                return null;
            }
            else
                return mgr.getProgressIndicator();
        }
    }
}
