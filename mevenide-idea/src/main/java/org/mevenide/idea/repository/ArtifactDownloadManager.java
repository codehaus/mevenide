package org.mevenide.idea.repository;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import org.apache.maven.project.Dependency;
import org.apache.maven.util.HttpUtils;
import org.mevenide.context.IQueryContext;
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

    private void downloadArtifact(final String pUrl,
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
        try {
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
        }
        catch (IOException e) {
            if (indicator != null) {
                indicator.finishNonCancelableSection();
                indicator.setText(e.getMessage());
                indicator.setText2("");
            }
            throw e;
        }

        if (indicator != null) {
            indicator.finishNonCancelableSection();
            indicator.setText("Finished downloading file.");
            indicator.setText2("");
        }
    }


    public void downloadArtifact(final Module pModule,
                                 final IRepositoryReader pReader,
                                 final String pGroupId,
                                 final String pType,
                                 final String pArtifactId,
                                 final String pVersion,
                                 final String pExtension) throws IOException {
        final ProgressIndicator indicator = IDEUtils.getProgressIndicator();
        if (indicator.isCanceled())
            return;

        final String relativePath = RepositoryUtils.getDependencyRelativePath(pGroupId,
                                                                              pType,
                                                                              pArtifactId,
                                                                              pVersion,
                                                                              pExtension);

        final ModuleSettings settings = ModuleSettings.getInstance(pModule);
        final IQueryContext ctx = settings.getQueryContext();
        if (ctx == null)
            throw new IllegalArgumentException(RES.get("pom.not.defined"));

        //
        //calculated destination file location
        //
        final ModuleLocationFinder finder = new ModuleLocationFinder(pModule);
        final IPropertyResolver resolver = ctx.getResolver();
        final File destFile = new File(finder.getMavenLocalRepository(), relativePath);

        //
        //use proxy if needed
        //
        String host = resolver.getResolvedValue("maven.proxy.host");
        String port = resolver.getResolvedValue("maven.proxy.port");
        String user = resolver.getResolvedValue("maven.proxy.username");
        String passwd = resolver.getResolvedValue("maven.proxy.password");
        if (host != null && host.trim().length() == 0) host = null;
        if (port != null && port.trim().length() == 0) port = null;
        if (user != null && user.trim().length() == 0) user = null;
        if (passwd != null && passwd.trim().length() == 0) passwd = null;

        //
        //calculate remote URL
        //
        String base = pReader.getRootURI().toString();
        if (!base.endsWith("/"))
            base = base + "/";

        final String url = base + relativePath;
        downloadArtifact(url, destFile, host, port, user, passwd);
    }

    public void downloadArtifact(final Module pModule,
                                 final String pGroupId,
                                 final String pType,
                                 final String pArtifactId,
                                 final String pVersion,
                                 final String pExtension) throws ArtifactNotFoundException {
        final ProgressIndicator indicator = IDEUtils.getProgressIndicator();
        if (indicator.isCanceled())
            return;

        //
        //acquire the remote repositories
        //
        final IRepositoryReader[] remoteRepos = RepositoryUtils.createRepoReaders(
            false, pModule);

        //
        //iterate the dependencies to download
        //
        Set<Throwable> errors = null;
        for (final IRepositoryReader reader : remoteRepos) {
            try {
                downloadArtifact(pModule, reader, pGroupId, pType, pArtifactId, pVersion, pExtension);
                return;
            }
            catch (IOException e) {
                if(errors == null)
                    errors = new HashSet<Throwable>(remoteRepos.length);
                errors.add(e);
            }
        }

        //
        //if an IO error occured, throw it
        //
        if (errors != null && errors.size() > 0) {
            final Throwable[] buffer = new Throwable[errors.size()];
            throw new ArtifactNotFoundException(pGroupId,
                                                pType,
                                                pArtifactId,
                                                pVersion,
                                                pExtension,
                                                errors.toArray(buffer));
        }
    }

    public void downloadArtifact(final Module pModule,
                                 final Dependency pDependency) throws ArtifactNotFoundException {
        downloadArtifact(pModule,
                         pDependency.getGroupId(),
                         pDependency.getType(),
                         pDependency.getArtifactId(),
                         pDependency.getVersion(),
                         pDependency.getExtension());
    }

    public void downloadArtifact(final Module pModule,
                                 final RepoPathElement pathElement) throws IOException {
        final ProgressIndicator indicator = IDEUtils.getProgressIndicator();
        if (indicator != null && indicator.isCanceled())
            return;

        if (!pathElement.isRemote()) {
            LOG.warn("Repository element " + pathElement + " is not a remote artifact.");
            return;
        }

        if (!pathElement.isLeaf()) {
            try {
                if (indicator != null)
                    indicator.startNonCancelableSection();
                final RepoPathElement[] children = pathElement.getChildren();
                if (indicator != null)
                    indicator.finishNonCancelableSection();
                for (RepoPathElement e : children)
                    downloadArtifact(pModule, e);
            }
            catch(IOException e) {
                if (indicator != null) {
                    indicator.setText(e.getMessage());
                    indicator.setText2("");
                }
                throw e;
            }
            catch (Exception e) {
                if(indicator != null) {
                    indicator.setText("Error fetching children.");
                    indicator.setText2("");
                }
                LOG.warn(e.getMessage(), e);
            }
        }
        else {
            downloadArtifact(pModule,
                             pathElement.getReader(),
                             pathElement.getGroupId(),
                             pathElement.getType(),
                             pathElement.getArtifactId(),
                             pathElement.getVersion(),
                             pathElement.getExtension());
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
