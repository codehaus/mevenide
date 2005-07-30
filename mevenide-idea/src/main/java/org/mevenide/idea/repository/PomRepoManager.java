package org.mevenide.idea.repository;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SelectFromListDialog;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.*;
import org.apache.maven.util.HttpUtils;
import org.mevenide.idea.project.*;
import org.mevenide.idea.project.properties.PropertiesManager;
import org.mevenide.idea.repository.browser.RepoToolWindow;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.ui.MultiLineLabel;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;
import org.mevenide.repository.RepositoryReaderFactory;
import static org.mevenide.repository.RepositoryReaderFactory.createRemoteRepositoryReader;

/**
 * @author Arik
 */
public class PomRepoManager extends AbstractPomSettingsManager implements PomManagerListener {
    private static final IRepositoryReader[] EMPTY_REPO_ARRAY = new IRepositoryReader[0];

    private static final SelectFromListDialog.ToStringAspect SIMPLE_TO_STRING_ASPECT = new SelectFromListDialog.ToStringAspect() {
        public String getToStirng(Object obj) {
            return obj.toString();
        }
    };

    public PomRepoManager(final Project pProject) {
        super(pProject);
    }

    public VirtualFile findFile(final String pPomUrl, final Artifact pArtifact) {
        final VirtualFile localRepo = getLocalRepositoryDirectory(pPomUrl);
        if (localRepo == null)
            return null;

        final FileFinder finder = new FileFinder(localRepo, pArtifact);
        IDEUtils.runCommand(project, finder);
        if (!finder.isFound())
            return null;

        return finder.getFile();
    }

    /**
     * Displays a list of available local repositories to the user, and returns the selected
     * repository url.
     *
     * <p>If the {@link org.mevenide.idea.project.PomManager#getFileUrls()} method reports that only
     * one POM is registered, no dialog is displayed to the user.</p>
     *
     * @return the selected repository's url, {@code null} if no url is selected, or no POMs are
     *         registered
     */
    public String selectDestinationRepo() {
        return selectDestinationRepo(null, null);
    }

    /**
     * Displays a list of available local repositories to the user, and returns the selected
     * repository url.
     *
     * <p>If the {@link org.mevenide.idea.project.PomManager#getFileUrls()} method reports that only
     * one POM is registered, no dialog is displayed to the user.</p>
     *
     * @param pTitle the title to use for the dialog, if displayed
     *
     * @return the selected repository's url, {@code null} if no url is selected, or no POMs are
     *         registered
     */
    public String selectDestinationRepo(final String pTitle) {
        return selectDestinationRepo(pTitle, null);
    }

    /**
     * Displays a list of available local repositories to the user, and returns the selected
     * repository url.
     *
     * <p>If the {@link org.mevenide.idea.project.PomManager#getFileUrls()} method reports that only
     * one POM is registered, no dialog is displayed to the user.</p>
     *
     * @param pTitle the title to use for the dialog, if displayed
     * @param pLabel the label to display near the list of urls in the dialog
     *
     * @return the selected repository's url, {@code null} if no url is selected, or no POMs are
     *         registered
     */
    public String selectDestinationRepo(final String pTitle, final String pLabel) {
        final PomManager pomMgr = PomManager.getInstance(project);
        final String[] pPomUrls = pomMgr.getFileUrls();
        if (pPomUrls == null || pPomUrls.length == 0)
            return null;

        if (pPomUrls.length == 1)
            return pPomUrls[0];

        final String[] repoUrls = new String[pPomUrls.length];
        for (int i = 0; i < pPomUrls.length; i++) {
            final IRepositoryReader localRepo = getLocalRepositoryReader(pPomUrls[i]);
            final URI uri = localRepo.getRootURI();
            final File rootFile = new File(uri);
            repoUrls[i] = rootFile.getAbsolutePath();
        }

        final String title;
        if (pTitle == null || pTitle.trim().length() == 0)
            title = "Local repository";
        else
            title = pTitle;

        final String label;
        if (pLabel == null || pLabel.trim().length() == 0)
            label = "Please select the local repository to use:";
        else
            label = pLabel;

        final SelectFromListDialog dlg = new SelectFromListDialog(
                project,
                repoUrls,
                SIMPLE_TO_STRING_ASPECT,
                title,
                ListSelectionModel.SINGLE_SELECTION);

        dlg.addToDialog(new MultiLineLabel(label), BorderLayout.PAGE_START);
        dlg.setModal(true);
        dlg.setResizable(true);
        dlg.show();

        if (!dlg.isOK())
            return null;

        return dlg.getSelection()[0].toString();
    }

    public IRepositoryReader[] getRemoteRepositoryReaders() {
        final String[] urls = PomManager.getInstance(project).getFileUrls();
        final Map<String, IRepositoryReader> repos = new HashMap<String, IRepositoryReader>();
        for (String url : urls) {
            final IRepositoryReader[] readers = getRemoteRepositoryReaders(url);
            for (IRepositoryReader reader : readers)
                repos.put(reader.getRootURI().toString(), reader);
        }

        return repos.values().toArray(new IRepositoryReader[repos.size()]);
    }

    public IRepositoryReader[] getRemoteRepositoryReaders(final String pPomUrl) {
        final PropertiesManager mgr = PropertiesManager.getInstance(project);
        final String value = mgr.getProperty(pPomUrl, "maven.repo.remote");
        if (value == null || value.trim().length() == 0)
            return EMPTY_REPO_ARRAY;

        final String[] repoList = value.split(",");
        final Set<String> urls = new HashSet<String>(repoList.length);
        for (String url : repoList)
            urls.add(url);

        final IRepositoryReader[] repos = new IRepositoryReader[urls.size()];
        for (int i = 0; i < repoList.length; i++) {
            final String host = ProxySettings.getInstance(project).getProxyHost(pPomUrl);
            final Integer port = ProxySettings.getInstance(project).getProxyPort(pPomUrl);
            final String portStr = port == null ? null : port.toString();

            //TODO: incorporate username/password for proxies
//            final String username = ProxySettings.getInstance(project).getProxyUsername(pPomUrl);
//            final String password = ProxySettings.getInstance(project).getProxyPassword(pPomUrl);

            //TODO: report URI errors on 'create' via errors pane
            final URI uri = URI.create(repoList[i]);
            if (host != null && portStr != null)
                repos[i] = createRemoteRepositoryReader(uri, host, portStr);
            else
                repos[i] = createRemoteRepositoryReader(uri);
        }

        return repos;
    }

    public VirtualFile getLocalRepositoryDirectory(final String pPomUrl) {
        final IRepositoryReader localRepo = getLocalRepositoryReader(pPomUrl);
        if (localRepo == null)
            return null;

        final URI localRepoRootUri = localRepo.getRootURI();
        try {
            return VfsUtil.findFileByURL(localRepoRootUri.toURL());
        }
        catch (MalformedURLException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public IRepositoryReader getLocalRepositoryReader(final String pPomUrl) {
        final PropertiesManager mgr = PropertiesManager.getInstance(project);
        final String value = mgr.getProperty(pPomUrl, "maven.repo.local");
        if (value == null || value.trim().length() == 0)
            return null;

        final File root = new File(value);
        return RepositoryReaderFactory.createLocalRepositoryReader(root);
    }

    public IRepositoryReader[] getRepositoryReaders() {
        final String[] urls = PomManager.getInstance(project).getFileUrls();
        final Map<String, IRepositoryReader> repos = new HashMap<String, IRepositoryReader>();
        for (String url : urls) {
            final IRepositoryReader[] readers = getRepositoryReaders(url);
            for (IRepositoryReader reader : readers)
                repos.put(reader.getRootURI().toString(), reader);
        }

        return repos.values().toArray(new IRepositoryReader[repos.size()]);
    }

    public IRepositoryReader[] getRepositoryReaders(final String pPomUrl) {
        final IRepositoryReader[] remote = getRemoteRepositoryReaders(pPomUrl);
        final IRepositoryReader local = getLocalRepositoryReader(pPomUrl);
        if (local == null)
            return remote;

        final IRepositoryReader[] all = new IRepositoryReader[remote.length + 1];
        System.arraycopy(remote, 0, all, 0, remote.length);
        all[all.length - 1] = local;
        return all;
    }

    public boolean isInstalled(final String pPomUrl, final RepoPathElement pElement) {
        return isInstalled(pPomUrl, Artifact.fromRepoPathElement(pElement).getCompleteArtifact());
    }

    public boolean isInstalled(final String pPomUrl, final Artifact pArtifact) {
        final IRepositoryReader localRepo = getLocalRepositoryReader(pPomUrl);
        if (localRepo == null)
            return false;

        final URI localRepoRootUri = localRepo.getRootURI();
        try {
            final VirtualFile localRepoFile = VfsUtil.findFileByURL(localRepoRootUri.toURL());
            final FileFinder finder = new FileFinder(localRepoFile, pArtifact);
            IDEUtils.runCommand(project, finder);
            return finder.isFound();
        }
        catch (MalformedURLException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public void download(final String pPomUrl, final RepoPathElement pathElement)
            throws ArtifactNotFoundException {
        download(pPomUrl, Artifact.fromRepoPathElement(pathElement));
    }

    public void download(final String pPomUrl, final Artifact pArtifact)
            throws ArtifactNotFoundException {
        boolean anySuccesses = false;

        //
        //iterate over the POM's remote repositories, and try each one. The first one
        //that succeeds is returned.
        //
        final IRepositoryReader[] remoteRepos = getRemoteRepositoryReaders(pPomUrl);
        final Set<Throwable> errors = new HashSet<Throwable>(remoteRepos.length);
        for (final IRepositoryReader reader : remoteRepos) {
            try {
                download(pPomUrl, reader, pArtifact);
                anySuccesses = true;
            }
            catch (IOException e) {
                errors.add(e);
            }
        }

        //
        //if we got here, errors occured - throw an exception specifying the error(s)
        //
        if (!anySuccesses) {
            final Throwable[] buffer = new Throwable[errors.size()];
            throw new ArtifactNotFoundException(pArtifact, errors.toArray(buffer));
        }
    }

    private void download(final String pPomUrl,
                          final IRepositoryReader pRemoteRepo,
                          final Artifact pArtifact) throws IOException {
        if (pArtifact.isComplete())
            download(pPomUrl, pRemoteRepo, pArtifact.getRelativePath(true));
        else {
            final ChildrenFetchService service = ChildrenFetchService.getInstance();
            final RepoPathElement elt = pArtifact.toRepoPathElement(pRemoteRepo);

            final ProgressIndicator prg = IDEUtils.getProgressIndicator();
            if (prg != null) {
                if (prg.isCanceled())
                    return;
                prg.setText2("Searching for children of '" + elt.getRelativeURIPath() + "'...");
            }
            final Future<RepoPathElement[]> result = service.fetch(elt);

            try {
                final RepoPathElement[] elements = result.get(30, TimeUnit.SECONDS);
                if (prg != null)
                    prg.setText2("Found " + elements.length + " artifacts under '" + elt.getRelativeURIPath() + "', downloading...");
                for (int i = 0; i < elements.length; i++) {
                    RepoPathElement element = elements[i];
                    if (prg != null) {
                        if (prg.isCanceled())
                            return;
                        prg.setText2("Download artifact " + (i + 1) + " out of " + elements.length + " for " + elt.getRelativeURIPath());
                    }

                    final Artifact artifact = Artifact.fromRepoPathElement(element);
                    download(pPomUrl, pRemoteRepo, artifact);
                }
            }
            catch (InterruptedException e) {
                //TODO: report errors to the user
                LOG.error(e, e);
            }
            catch (ExecutionException e) {
                //TODO: report errors to the user
                LOG.error(e, e);
            }
            catch (TimeoutException e) {
                //TODO: report errors to the user
                LOG.error(e, e);
            }
        }
    }

    private void download(final String pPomUrl,
                          final IRepositoryReader pRemoteRepo,
                          final String pPath) throws IOException {
        //
        //calculated destination file location
        //
        final PomRepoManager pomRepoMgr = PomRepoManager.getInstance(project);
        final IRepositoryReader localRepo = pomRepoMgr.getLocalRepositoryReader(pPomUrl);
        final File localRepoRootDir = new File(localRepo.getRootURI());
        final File destFile = new File(localRepoRootDir, pPath);

        //
        //calculate remote URL
        //
        String base = pRemoteRepo.getRootURI().toString();
        if (!base.endsWith("/")) base = base + "/";
        final String url = base + pPath;

        //
        //download
        //
        final ProxySettings proxy = ProxySettings.getInstance(project);
        download(url,
                 destFile,
                 proxy.getProxyHost(pPomUrl),
                 proxy.getProxyPort(pPomUrl),
                 proxy.getProxyUsername(pPomUrl),
                 proxy.getProxyPassword(pPomUrl));
    }

    private static void download(final String pUrl,
                                 final File pDstFile,
                                 final String pProxyHost,
                                 final Integer pProxyPort,
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
            indicator.startNonCancelableSection();
        }

        //
        //download the file
        //
        try {
            final String port = pProxyPort == null ? null : pProxyPort.toString();
            HttpUtils.getFile(pUrl,                                 //url to download
                              pDstFile,                             //destination file
                              false,                                //ignore errors?
                              true,                                 //use timestamp
                              pProxyHost,                           //proxy host
                              port,                                 //proxy port
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
        }
    }

    @Override
    public void projectOpened() {
        RepoToolWindow.register(project);
        PomManager.getInstance(project).addPomManagerListener(this);
    }

    public void pomAdded(PomManagerEvent pEvent) {
        
    }

    public void pomRemoved(PomManagerEvent pEvent) {
    }

    public void pomValidityChanged(PomManagerEvent pEvent) {
    }

    public static PomRepoManager getInstance(final Project pProject) {
        return pProject.getComponent(PomRepoManager.class);
    }

    private static class PathSearcher implements Runnable {
        protected final VirtualFile localRepo;
        private final String path;
        protected VirtualFile file = null;

        public PathSearcher(final VirtualFile pLocalRepo,
                            final String pPath) {
            localRepo = pLocalRepo;
            path = pPath.replace(File.separatorChar, '/');
        }

        public boolean isFound() {
            return file != null;
        }

        public VirtualFile getFile() {
            return file;
        }

        public void run() {
            final StringBuilder buf =
                    new StringBuilder(100)
                            .append("file://")
                            .append(localRepo.getPath())
                            .append('/')
                            .append(path);

            final String url = buf.toString();
            file = VirtualFileManager.getInstance().refreshAndFindFileByUrl(url);
        }
    }

    private static class FileFinder extends PathSearcher {
        public FileFinder(final VirtualFile pLocalRepo, final RepoPathElement pElement) {
            this(pLocalRepo, Artifact.fromRepoPathElement(pElement).getCompleteArtifact());
        }

        public FileFinder(final VirtualFile pLocalRepo, final Artifact pArtifact) {
            super(pLocalRepo, pArtifact.getRelativePath(true));
        }
    }
}
