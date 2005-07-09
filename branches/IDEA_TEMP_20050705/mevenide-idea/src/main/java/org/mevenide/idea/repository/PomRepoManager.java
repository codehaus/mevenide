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
import javax.swing.*;
import org.apache.maven.util.HttpUtils;
import org.mevenide.idea.Res;
import org.mevenide.idea.global.properties.PropertiesManager;
import org.mevenide.idea.project.AbstractPomSettingsManager;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.ProxySettings;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.ui.MultiLineLabel;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;
import org.mevenide.repository.RepositoryReaderFactory;
import static org.mevenide.repository.RepositoryReaderFactory.createRemoteRepositoryReader;

/**
 * @author Arik
 */
public class PomRepoManager extends AbstractPomSettingsManager {
    /**
     * Resources.
     */
    private static final Res RES = Res.getInstance(PomRepoManager.class);

    private static final IRepositoryReader[] EMPTY_REPO_ARRAY = new IRepositoryReader[0];

    private static final SelectFromListDialog.ToStringAspect SIMPLE_TO_STRING_ASPECT = new SelectFromListDialog.ToStringAspect() {
        public String getToStirng(Object obj) {
            return obj.toString();
        }
    };

    public PomRepoManager(final Project pProject) {
        super(pProject);
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
        if(pTitle == null || pTitle.trim().length() == 0)
            title = "Local repository";
        else
            title = pTitle;

         final String label;
        if(pLabel == null || pLabel.trim().length() == 0)
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
        final Map<String,IRepositoryReader> repos = new HashMap<String, IRepositoryReader>();
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
        if(value == null || value.trim().length() == 0)
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
            repos[i] = createRemoteRepositoryReader(uri, host, portStr);
        }

        return repos;
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
        final IRepositoryReader[] all = new IRepositoryReader[remote.length + 1];
        System.arraycopy(remote, 0, all, 0, remote.length);
        all[all.length - 1] = local;
        return all;
    }

    public boolean isInstalled(final String pPomUrl, final RepoPathElement pElement) {
        return isInstalled(pPomUrl,
                           pElement.getGroupId(),
                           pElement.getArtifactId(),
                           pElement.getType(),
                           pElement.getVersion(),
                           pElement.getExtension());
    }

    public boolean isInstalled(final String pPomUrl,
                               final String pGroupId,
                               final String pArtifactId,
                               final String pType,
                               final String pVersion,
                               final String pExtension) {
        final IRepositoryReader localRepo = getLocalRepositoryReader(pPomUrl);
        if (localRepo == null)
            return false;

        final URI localRepoRootUri = localRepo.getRootURI();
        try {
            final VirtualFile localRepoFile = VfsUtil.findFileByURL(localRepoRootUri.toURL());
            final FileSearcher searcher = new FileSearcher(localRepoFile,
                                                           pGroupId,
                                                           pArtifactId,
                                                           pType,
                                                           pVersion,
                                                           pExtension);
            IDEUtils.runCommand(project, searcher);
            return searcher.isFound();
        }
        catch (MalformedURLException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public VirtualFile download(final String pPomUrl, final RepoPathElement pathElement)
            throws ArtifactNotFoundException {
        return download(pPomUrl,
                        pathElement.getGroupId(),
                        pathElement.getType(),
                        pathElement.getArtifactId(),
                        pathElement.getVersion(),
                        pathElement.getExtension());
    }

    public VirtualFile download(final String pPomUrl,
                                final String pGroupId,
                                final String pArtifactId,
                                final String pType,
                                final String pVersion,
                                final String pExtension) throws ArtifactNotFoundException {
        //
        //iterate over the POM's remote repositories, and try each one. The first one
        //that succeeds is returned.
        //
        final IRepositoryReader[] remoteRepos = getRemoteRepositoryReaders(pPomUrl);
        final Set<Throwable> errors = new HashSet<Throwable>(remoteRepos.length);
        for (final IRepositoryReader reader : remoteRepos) {
            try {
                return download(pPomUrl,
                                reader,
                                pGroupId,
                                pType,
                                pArtifactId,
                                pVersion,
                                pExtension);
            }
            catch (IOException e) {
                errors.add(e);
            }
        }

        //
        //if we got here, errors occured - throw an exception specifying the error(s)
        //
        final Throwable[] buffer = new Throwable[errors.size()];
        throw new ArtifactNotFoundException(pGroupId,
                                            pType,
                                            pArtifactId,
                                            pVersion,
                                            pExtension,
                                            errors.toArray(buffer));
    }

    public VirtualFile download(final String pPomUrl,
                                final IRepositoryReader pRemoteRepo,
                                final String pGroupId,
                                final String pArtifactId,
                                final String pType,
                                final String pVersion,
                         final String pExtension) throws IOException {
        return download(pPomUrl, pRemoteRepo, convertToRelativePath(pGroupId,
                                                                    pArtifactId,
                                                                    pType,
                                                                    pVersion,
                                                                    pExtension));
    }

    private VirtualFile download(final String pPomUrl,
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

        //
        //find output file
        //
        final String path = destFile.getAbsolutePath().replace(File.separatorChar, '/');
        final String ideaUrl = "file://" + path;
        return VirtualFileManager.getInstance().refreshAndFindFileByUrl(ideaUrl);
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
            indicator.setText2("Saving to " + pDstFile.getAbsolutePath());
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
            indicator.setText2("");
        }
    }

    public static String getPresentableName(final String pGroupId,
                                            final String pArtifactId,
                                            String pType,
                                            String pVersion,
                                            String pExtension) {
        if (pGroupId == null || pGroupId.trim().length() == 0)
            throw new IllegalArgumentException(RES.get("null.arg", "pGroupId"));

        if (pType == null || pType.trim().length() == 0)
            pType = "jar";

        if (pArtifactId == null || pArtifactId.trim().length() == 0)
            throw new IllegalArgumentException(RES.get("null.arg", "pArtifactId"));

        if (pVersion == null || pVersion.trim().length() == 0)
            pVersion = "SNAPSHOT";

        if (pExtension == null || pExtension.trim().length() == 0)
            pExtension = pType;

        final StringBuilder buf = new StringBuilder(100);
        buf.append(pArtifactId).append('-').append(pVersion).append('.').append(pExtension);
        return buf.toString();
    }

    public static String convertToRelativePath(final String pGroupId,
                                               final String pArtifactId,
                                               String pType,
                                               String pVersion,
                                               String pExtension) {
        if (pGroupId == null || pGroupId.trim().length() == 0)
            throw new IllegalArgumentException(RES.get("null.arg", "pGroupId"));

        if (pType == null || pType.trim().length() == 0)
            pType = "jar";

        if (pArtifactId == null || pArtifactId.trim().length() == 0)
            throw new IllegalArgumentException(RES.get("null.arg", "pArtifactId"));

        if (pVersion == null || pVersion.trim().length() == 0)
            pVersion = "SNAPSHOT";

        if (pExtension == null || pExtension.trim().length() == 0)
            pExtension = pType;

        final StringBuilder buf = new StringBuilder(100);
        buf.append(pGroupId).
                append('/').
                append(pType).append('s').
                append('/').
                append(pArtifactId).append('-').append(pVersion).
                append('.').append(pExtension);
        return buf.toString();
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
            path = pPath;
        }

        public boolean isFound() {
            return file != null;
        }

        public VirtualFile getFile() {
            return file;
        }

        public void run() {
            final String url = "file://" + path.replace(File.separatorChar, '/');
            file = VirtualFileManager.getInstance().refreshAndFindFileByUrl(url);
        }
    }

    private static class FileSearcher extends PathSearcher {
        public FileSearcher(final VirtualFile pLocalRepo, final RepoPathElement pElement) {
            this(pLocalRepo,
                 pElement.getGroupId(),
                 pElement.getArtifactId(),
                 pElement.getType(),
                 pElement.getVersion(),
                 pElement.getExtension());
        }

        public FileSearcher(final VirtualFile pLocalRepo,
                            final String pGroupId,
                            final String pArtifactId,
                            final String pType,
                            final String pVersion,
                            final String pExtension) {
            super(pLocalRepo, convertToRelativePath(pGroupId, pArtifactId, pType, pVersion, pExtension));
        }
    }
}
