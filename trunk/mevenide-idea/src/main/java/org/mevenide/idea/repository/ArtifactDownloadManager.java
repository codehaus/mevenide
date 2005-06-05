package org.mevenide.idea.repository;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import org.apache.maven.util.HttpUtils;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.components.AbstractApplicationComponent;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.repository.RepoPathElement;

/**
 * @todo this should be an idea component, and not a custom singleton
 * @author Arik
 */
public class ArtifactDownloadManager extends AbstractApplicationComponent {

    public void downloadArtifact(final ILocationFinder pFinder,
                                 final IPropertyResolver pResolver,
                                 final RepoPathElement... pPathElements) throws IOException {
        final ProgressIndicator indicator = IDEUtils.getProgressIndicator();
        if(indicator.isCanceled())
            return;

        for (RepoPathElement pathElement : pPathElements) {
            if (!pathElement.isRemote()) {
                LOG.warn("Repository element " + pathElement + " is not a remote artifact.");
                continue;
            }

            if (!pathElement.isLeaf()) {
                try {
                    if(indicator != null)
                        indicator.startNonCancelableSection();
                    final RepoPathElement[] children = pathElement.getChildren();
                    if (indicator != null)
                        indicator.finishNonCancelableSection();
                    downloadArtifact(pFinder, pResolver, children);
                }
                catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            else {
                //
                //calculated destination file location
                //
                final File localRepo = new File(pFinder.getMavenLocalRepository());
                final String localFileUri = localRepo.toURI() + pathElement.getRelativeURIPath();
                final File destinationFile = new File(URI.create(localFileUri));
                final String url = pathElement.getURI().toURL().toString();

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
                //setup the progress indicator, if available
                //
                if (indicator != null) {
                    if (indicator.isCanceled())
                        return;
                    indicator.setText("Downloading from " + url);
                    indicator.setText2("Saving to " + destinationFile.getAbsolutePath());
                    indicator.startNonCancelableSection();
                }

                //
                //download the file
                //
                HttpUtils.getFile(url,                                  //url to download
                                  destinationFile,                      //destination file
                                  false,                                //ignore errors?
                                  true,                                 //use timestamp
                                  host, port, user, passwd,             //proxy settings
                                  null, null,                           //login settings
                                  new ProgressIndicatorDownloadMeter()  //download meter
                );
                if (indicator != null) {
                    indicator.finishNonCancelableSection();
                    indicator.setText("Finished downloading file.");
                    indicator.setText2("");
                }
            }
        }

    }

    /**
     * Returns the artifact download manager.
     *
     * @return instance
     */
    public static ArtifactDownloadManager getInstance() {
        return ApplicationManager.getApplication().getComponent(ArtifactDownloadManager.class);
    }
}
