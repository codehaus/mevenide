package org.mevenide.idea.repository;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import org.apache.maven.project.Dependency;
import org.apache.maven.util.HttpUtils;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.idea.module.ModuleLocationFinder;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.components.AbstractApplicationComponent;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class ArtifactDownloadManager extends AbstractApplicationComponent {
    public void downloadArtifact(final URL pUrl,
                                 final File pDstFile,
                                 final String pProxyHost,
                                 final String pProxyPort,
                                 final String pProxyUser,
                                 final String pProxyPassword) throws IOException {
        downloadArtifact(pUrl.toExternalForm(), pDstFile, pProxyHost, pProxyPort, pProxyUser, pProxyPassword);
    }

    public void downloadArtifact(final String pUrl,
                                 final File pDstFile,
                                 final String pProxyHost,
                                 final String pProxyPort,
                                 final String pProxyUser,
                                 final String pProxyPassword) throws IOException {
        final ProgressIndicator indicator = IDEUtils.getProgressIndicator();
        if (indicator != null && indicator.isCanceled())
            return;

        //
        //make sure the directory for the file exists
        //
        pDstFile.getParentFile().mkdirs();

        //
        //setup the progress indicator, if available
        //
        if (indicator != null) {
            indicator.setText("Downloading from " + pUrl);
            indicator.setText2("Saving to " + pDstFile.getAbsolutePath());
            indicator.startNonCancelableSection();
        }

        //
        //download the file
        //
        HttpUtils.getFile(pUrl,                                 //url to download
                          pDstFile,                             //destination file
                          false,                                //ignore errors?
                          true,                                 //use timestamp
                          pProxyHost,                           //proxy host
                          pProxyPort,                           //proxy port
                          pProxyUser,                           //proxy username
                          pProxyPassword,                       //proxy password
                          null, null,                           //login settings
                          new ProgressIndicatorDownloadMeter()  //download meter
        );

        if (indicator != null) {
            indicator.finishNonCancelableSection();
            indicator.setText("Finished downloading file.");
            indicator.setText2("");
        }
    }


    public void downloadArtifact(final Module pModule,
                                 final Dependency... pDependency) {
        final ModuleSettings settings = ModuleSettings.getInstance(pModule);
        final IQueryContext ctx = settings.getQueryContext();
        if (ctx == null)
            throw new IllegalArgumentException(RES.get("pom.not.defined"));

        //
        //acquire the remote repositories
        //
        final IRepositoryReader[] remoteRepos = RepositoryUtils.createRepoReaders(
            false, pModule);

        //
        //iterate the dependencies to download
        //
        final ModuleLocationFinder finder = new ModuleLocationFinder(pModule);
        for (Dependency dep : pDependency) {
            for (IRepositoryReader reader : remoteRepos) {
                final RepoPathElement repoPath = new RepoPathElement(reader,
                                                                     null,
                                                                     dep.getGroupId(),
                                                                     dep.getType(),
                                                                     dep.getVersion(),
                                                                     dep.getArtifactId(),
                                                                     dep.getExtension());
                try {
                    downloadArtifact(finder, ctx.getResolver(), repoPath);
                }
                catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public void downloadArtifact(final IQueryContext pContext,
                                 final ILocationFinder pFinder,
                                 final RepoPathElement... pPathElements) throws IOException {
        downloadArtifact(pFinder, pContext.getResolver(), pPathElements);
    }

    public void downloadArtifact(final ILocationFinder pFinder,
                                 final IPropertyResolver pResolver,
                                 final RepoPathElement... pPathElements) throws IOException {
        final ProgressIndicator indicator = IDEUtils.getProgressIndicator();
        if (indicator.isCanceled())
            return;

        for (RepoPathElement pathElement : pPathElements) {
            if (!pathElement.isRemote()) {
                LOG.warn("Repository element " + pathElement + " is not a remote artifact.");
                continue;
            }

            if (!pathElement.isLeaf()) {
                try {
                    if (indicator != null)
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

                downloadArtifact(url, destinationFile, host, port, user, passwd);
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
