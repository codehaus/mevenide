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

package org.mevenide.repository;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mevenide.project.dependency.DefaultDependencyResolver;
import org.mevenide.project.dependency.IDependencyResolver;
import org.mevenide.project.dependency.URIDependencyResolver;

/**
 * Reader for remote repository
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class HttpRepositoryReader extends AbstractRepositoryReader {
    private URI root;
    /** Creates a new instance of HttpRepositoryReader */
    public HttpRepositoryReader(URI rootUri) {
        root = rootUri;
    }

    public RepoPathElement[] readElements(RepoPathElement element) throws Exception {
        if (element.isLeaf()) {
            return new RepoPathElement[0];
        }
        String part = element.getPartialURIPath();
        StringBuffer complete = new StringBuffer(root.toString());
        if (!root.toString().endsWith("/")) {
            complete.append("/");
        }
        complete.append(part);
        return getChildren(element, URI.create(complete.toString()));
    }
    
    private RepoPathElement[] getChildren(RepoPathElement element, URI uri) throws Exception {
        // TODO code application logic here
        HttpMethod method = new GetMethod(uri.toString());
        try {
            HttpClient client = new HttpClient();
            client.executeMethod(method);
            String response = method.getResponseBodyAsString();
            return createChildren(element, getNames(response), uri);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        
        return new RepoPathElement[0];
    }
    
    StructureWrapper[] getNames(String output) {
        StringTokenizer tok = new StringTokenizer(output, "\n");
        Pattern folderPattern = Pattern.compile(".*<IMG SRC=\"/icons/folder.gif\" ALT=\"\\[DIR\\]\"> <A HREF=\"(.*)/\">(.*)/</A>.*");
        Pattern filePattern = Pattern.compile(".*<IMG SRC=\"/icons/unknown.gif\" ALT=\"\\[   \\]\"> <A HREF=\"(.*)\">(.*)</A>.*");
        Collection items = new ArrayList();
        while (tok.hasMoreTokens()) {
            String line = tok.nextToken();
            Matcher match = folderPattern.matcher(line);
            if (match.matches()) {
                String dir = match.group(1);
                String dir2 = match.group(2);
                items.add(new StructureWrapper(dir, true));
            } else {
                match = filePattern.matcher(line);
                if (match.matches()) {
                    String file = match.group(1);
                    String file2 = match.group(2);
                    items.add(new StructureWrapper(file, false));
                }
            }
        }
        StructureWrapper[] str = new StructureWrapper[items.size()];
        return (StructureWrapper[])items.toArray(str);
    }
    
    private RepoPathElement[] createChildren(RepoPathElement element, StructureWrapper[] files, URI uri) {
        Collection col = new ArrayList();
        URIDependencyResolver resolver = new URIDependencyResolver();
        Collection knownArtifacts = new HashSet();
        for (int i = 0; i < files.length; i++) {
            RepoPathElement elem = null;
            if (element.getLevel() == RepoPathElement.LEVEL_ROOT) {
                // nothing known
                if (files[i].isDirectory() && ! "Global Project".equals(files[i].getName())) {
                    elem = copyElement(element);
                    elem.setGroupId(files[i].getName());
                }
            }
            else if (element.getLevel() == RepoPathElement.LEVEL_GROUP) {
                // groupid known already
                if (files[i].isDirectory() && files[i].getName().endsWith("s")) {
                    elem = copyElement(element);
                    String type = files[i].getName();
                    elem.setType(type.substring(0, type.length() - 1));
                }
            }
            else if (element.getLevel() == RepoPathElement.LEVEL_TYPE) {
                if (files[i].isFile()) {
                    URI path = URI.create(uri.toString() + "/" + files[i].getName());
                    resolver.setURI(path);
                    elem = levelTypeCheck(element, resolver, knownArtifacts);
                }
            }
            else if (element.getLevel() == RepoPathElement.LEVEL_ARTIFACT) {
                if (files[i].isFile() 
                          && files[i].getName().startsWith(element.getArtifactId())) {
                    URI path = URI.create(uri.toString() + "/" + files[i].getName());
                    resolver.setURI(path);
                    elem = levelArtifactCheck(element, resolver);
                }
            }
            if (elem != null) {
                col.add(elem);
            }
        }
        RepoPathElement[] elems = new RepoPathElement[col.size()];
        return (RepoPathElement[])col.toArray(elems);
    }

    private static class StructureWrapper {
        private String name;
        private boolean folder;
        
        public StructureWrapper(String nm, boolean dir) {
            folder = dir;
            name = nm;
        }
        
        public boolean isDirectory() {
            return folder;
        }
        
        public boolean isFile() {
            return !folder;
        }
        
        public String getName() {
            return name;
        }
    }
    
}
