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

package org.mevenide.netbeans.project;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import org.apache.maven.util.DownloadMeter;
import org.apache.maven.util.HttpUtils;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.repository.RepoPathElement;
import org.openide.util.Lookup;
import org.openide.util.SharedClassObject;

/**
 * utilities for proxy settings and artifact downloads.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class ProxyUtilities {
    
    private static final String PROXY_HOST = "http.proxyHost"; //NOI18N
    private static final String PROXY_PORT = "http.proxyPort"; //NOI18N
    
    private static Method mGetProxyPort;
    private static Method mGetProxyHost;
    private static SharedClassObject settingsInst;
    private static boolean tryReflection = true;
    /** Creates a new instance of ProxyUtilities */
    private ProxyUtilities() {
    }
    
    
    private static synchronized void reflectIDESettings() {
        try {
            ClassLoader l = (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class);
            Class clazz = l.loadClass("org.netbeans.core.IDESettings"); // NOI18N
            settingsInst = SharedClassObject.findObject(clazz, true);
            mGetProxyHost = clazz.getMethod("getUserProxyHost", (Class[])null); // NOI18N
            mGetProxyPort = clazz.getMethod("getUserProxyPort", (Class[])null); // NOI18N
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tryReflection = false;
        }
    } 
    
    public static String getProxyHost() {
        if (tryReflection) {
            reflectIDESettings();
        }
        String toRet = reflValue(mGetProxyHost);
        return toRet != null ? toRet : System.getProperty(PROXY_HOST);
    }
    
    public static String getProxyPort() {
        if (tryReflection) {
            reflectIDESettings();
        }
        String toRet = reflValue(mGetProxyPort);
        return toRet != null ? toRet : System.getProperty(PROXY_PORT);
    }
    
    private static String reflValue(Method method) {
        if (method != null) {
            try {
                return (String)method.invoke(settingsInst, new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
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
        DownloadMeter meter = new StatusBarDownloadMeter(repoElement.getRelativeURIPath());
        HttpUtils.getFile(uri.toURL().toString(), destinationFile, 
                          false, true, host, port, user, passwd, null, null, meter);
        return true;
    }    
}
