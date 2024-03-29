/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */

package org.mevenide.netbeans.project.dependencies;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.project.Dependency;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.providers.http.HttpWagon;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.repository.Repository;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.project.ProxyUtilities;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;
import org.mevenide.repository.RepositoryReaderFactory;

/**
 * remote repository related utilities 
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class RepositoryUtilities {
    
    private static final Logger LOGGER = Logger.getLogger(RepositoryUtilities.class.getName());
    
    /** Creates a new instance of RepositoryUtilities */
    private RepositoryUtilities() {
    }
    
    public static IRepositoryReader createLocalReader(ILocationFinder finder) {
        File fil = new File(finder.getMavenLocalRepository());
        return  RepositoryReaderFactory.createLocalRepositoryReader(fil);
    }
    
    public static IRepositoryReader[] createRemoteReaders(IPropertyResolver resolver) {
        String host = resolver.getResolvedValue("maven.proxy.host");
        String port = resolver.getResolvedValue("maven.proxy.port");
        String user = resolver.getResolvedValue("maven.proxy.username");
        String passwd = resolver.getResolvedValue("maven.proxy.password");
        if (host == null) {
            host = ProxyUtilities.getProxyHost();
        }
        if (port == null) {
            port = ProxyUtilities.getProxyPort();
        }
        if (host != null && host.length() == 0) {
            host = null;
        } 
        if (port != null && port.length() == 0) {
            port = null;
        } 
        if (user != null && user.length() == 0) {
            user = null;
        } 
        if (passwd != null && passwd.length() == 0) {
            passwd = null;
        } 
        Collection cols = new ArrayList();
        URI[] remotes = createRemoteRepositoryURIs(resolver);
        for (int i = 0; i < remotes.length; i++) {
            IRepositoryReader reader = null;
            if (port != null && host != null) {
                if (user != null && passwd != null) {
                    reader = RepositoryReaderFactory.createRemoteRepositoryReader(remotes[i], host, port, user, passwd);
                } else {
                    reader = RepositoryReaderFactory.createRemoteRepositoryReader(remotes[i], host, port);
                }
            } else {
                reader = RepositoryReaderFactory.createRemoteRepositoryReader(remotes[i]);
            }
            cols.add(reader);
        }
        return (IRepositoryReader[])cols.toArray(new IRepositoryReader[cols.size()]);
    }
    
    public static URI[] createRemoteRepositoryURIs(IPropertyResolver resolver) {
        String repos = resolver.getResolvedValue("maven.repo.remote"); //NOI18N
        Collection cols = new ArrayList();
        IRepositoryReader reader;
        if (repos != null) {
            StringTokenizer tokens = new StringTokenizer(repos, ",");
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken().trim();
                if (token.endsWith("/")) {
                    token = token.substring(0, token.length() - 1);
                }
                URI uri = URI.create(token);
                cols.add(uri);
            }
        }
        return (URI[])cols.toArray(new URI[cols.size()]);
    }
    
    public static boolean downloadArtifact(IRepositoryReader[] readers,
            MavenProject project,
            Dependency dependency) throws Exception {
        Exception fileNotFound = new Exception();
        for (int i = 0; i < readers.length; i++) {
            String groupId = dependency.getGroupId() != null ? dependency.getGroupId() : dependency.getId();
            String artId = dependency.getArtifactId() != null ? dependency.getArtifactId() : dependency.getId();
            String type = dependency.getType() != null ? dependency.getType() : "jar";
            String ext = dependency.getExtension();
            final RepoPathElement el = new RepoPathElement(readers[i], null,
                    groupId,
                    type,
                    dependency.getVersion(),
                    artId,
                    ext);
            File localRepo = new File(project.getLocFinder().getMavenLocalRepository());
            File destinationFile = new File(URI.create(localRepo.toURI().toString() + el.getRelativeURIPath()));
            if (!destinationFile.exists() || destinationFile.getName().indexOf("SNAPSHOT") >= 0) {
                try {
                    return RepositoryUtilities.downloadArtifact(project.getLocFinder(),
                            project.getPropertyResolver(),
                            el);
                } catch (FileNotFoundException exc) {
                    // well can happen, definitely if having multiple repositories
                    fileNotFound = exc;
                } 
            } else {
                return false;
            }
        }
        // could be a hack.. if all repositories throw filenotfound, report it by rethrowing the last one.
        throw fileNotFound;
    }
    
    public static boolean downloadArtifact(ILocationFinder finder, 
                                        IPropertyResolver resolver,
                                        RepoPathElement repoElement) throws Exception {
        if (!repoElement.isRemote()) {
            return false;
        }
        if (!repoElement.isLeaf()) {
            RepoPathElement[] elements = repoElement.getChildren();
            for (int i = 0; i < elements.length; i++) {
                downloadArtifact(finder, resolver, elements[i]);
            }
            return true;
        }
        URI uri = repoElement.getURI();
        String relPath = repoElement.getRelativeURIPath();
        File localRepo = new File(finder.getMavenLocalRepository());
        File destinationFile = new File(URI.create(localRepo.toURI().toString() + relPath));
        destinationFile.getParentFile().mkdirs();
        String host = resolver.getResolvedValue("maven.proxy.host");
        String port = resolver.getResolvedValue("maven.proxy.port");
        String user = resolver.getResolvedValue("maven.proxy.username");
        String passwd = resolver.getResolvedValue("maven.proxy.password");
        if (host == null) {
            host = ProxyUtilities.getProxyHost();
        }
        if (port == null) {
            port = ProxyUtilities.getProxyPort();
        }
        if (host != null && host.length() == 0) {
            host = null;
        } 
        if (port != null && port.length() == 0) {
            port = null;
        } 
        if (user != null && user.length() == 0) {
            user = null;
        } 
        if (passwd != null && passwd.length() == 0) {
            passwd = null;
        } 
        ProxyInfo proxyInfo = null;
        if (host != null) {
            proxyInfo = new ProxyInfo();
            proxyInfo.setHost(host);
            proxyInfo.setPort(Integer.valueOf(port).intValue());
            proxyInfo.setUserName(user);
            proxyInfo.setPassword(passwd);
        }
        
        String url = uri.toURL().toString();
        int index = url.lastIndexOf( "/" );
        String file = url.substring( index + 1 );
        url = url.substring( 0, index );

        Repository repository = new Repository( "httputils", url );
        
        TransferListener meter = new StatusBarTransferListener();
        HttpWagon wagon = new HttpWagon();
        wagon.addTransferListener(meter);
        try {
            wagon.connect(repository, proxyInfo);
            wagon.getIfNewer(file, destinationFile, -1);
        } finally {
            try {
                wagon.disconnect();
            } catch (ConnectionException ex) {
                LOGGER.log(Level.FINE, "Failed to disconnect", ex);
            }
        }
        return true;
    }        
}
