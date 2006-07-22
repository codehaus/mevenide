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

package org.codehaus.mevenide.grammar;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.WildcardQuery;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.repository.indexing.ArtifactRepositoryIndex;
import org.apache.maven.repository.indexing.RepositoryIndex;
import org.apache.maven.repository.indexing.RepositoryIndexSearchException;
import org.apache.maven.repository.indexing.RepositoryIndexSearchHit;
import org.apache.maven.repository.indexing.query.AndQueryTerm;
import org.apache.maven.repository.indexing.query.CompoundQuery;
import org.apache.maven.repository.indexing.query.Query;
import org.apache.maven.repository.indexing.query.SinglePhraseQuery;
import org.codehaus.mevenide.grammar.AbstractSchemaBasedGrammar.MyElement;
import org.codehaus.mevenide.grammar.AbstractSchemaBasedGrammar.MyTextElement;
import org.codehaus.mevenide.indexer.CustomQueries;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.HintContext;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * xml completion grammar based on xsd, additionally allowing more to be added.
 */
public class MavenProjectGrammar extends AbstractSchemaBasedGrammar {
    
            private static final String[] SCOPES = new String[] {
                "compile",
                "test",
                "runtime",
                "provided",
                "system"
            };
    
    public MavenProjectGrammar(GrammarEnvironment env) {
        super(env);
    }
    
    protected InputStream getSchemaStream() {
        return getClass().getResourceAsStream("/org/codehaus/mevenide/grammar/maven-4.0.0.xsd");
    }
    
    protected List getDynamicCompletion(String path, HintContext hintCtx, org.jdom.Element parent) {
        if ("/project/build/plugins/plugin/configuration".equals(path) ||
            "/project/build/pluginManagement/plugins/plugin/configuration".equals(path) ||
            "/project/build/plugins/plugin/executions/execution/configuration".equals(path) ||
             "/project/build/pluginManagement/plugins/plugin/executions/execution/configuration".equals(path) ||
             "/project/reporting/plugins/plugin/configuration".equals(path)
             ) {
            // assuming we have the configuration node as parent..
            // does not need to be true for complex stuff
            Node previous = path.indexOf("execution") > 0
                ? hintCtx.getParentNode().getParentNode().getParentNode().getPreviousSibling()
                : hintCtx.getParentNode().getPreviousSibling();
            MavenEmbedder embedder = EmbedderFactory.getOnlineEmbedder();
            ArtifactInfoHolder info = findPluginInfo(previous, embedder, true);
            Document pluginDoc = loadDocument(info, embedder);
            if (pluginDoc != null) {
                return collectPluginParams(pluginDoc, hintCtx);
            }
        }
        return Collections.EMPTY_LIST;
    }
    
    private ArtifactInfoHolder findArtifactInfo(Node previous) {
        ArtifactInfoHolder holder = new ArtifactInfoHolder();
        while (previous != null) {
            if (previous instanceof org.w3c.dom.Element) {
                org.w3c.dom.Element el = (org.w3c.dom.Element)previous;
                NodeList lst = el.getChildNodes();
                if (lst.getLength() > 0) {
                    if ("artifactId".equals(el.getNodeName())) {
                        holder.setArtifactId(lst.item(0).getNodeValue());
                    }
                    if ("groupId".equals(el.getNodeName())) {
                        holder.setGroupId(lst.item(0).getNodeValue());
                    }
                    if ("version".equals(el.getNodeName())) {
                        holder.setVersion(lst.item(0).getNodeValue());
                    }
                }
            }
            previous = previous.getPreviousSibling();
        }
        return holder;
    }
    
    private ArtifactInfoHolder findPluginInfo(Node previous, MavenEmbedder embedder, boolean checkLocalRepo) {
        ArtifactInfoHolder holder = findArtifactInfo(previous);
        if (holder.getGroupId() == null) {
            holder.setGroupId("org.apache.maven.plugins");
        }
        if (checkLocalRepo && (holder.getVersion() == null || "LATEST".equals(holder.getVersion())) && holder.getArtifactId() != null && holder.getGroupId() != null) {
            File lev1 = new File(embedder.getLocalRepository().getBasedir(), holder.getGroupId().replace('.', File.separatorChar));
            File dir = new File(lev1, holder.getArtifactId());
            File fil = new File(dir, "maven-metadata-local.xml");
            if (fil.exists()) {
                MetadataXpp3Reader reader = new MetadataXpp3Reader();
                try {
                    Metadata data = reader.read(new InputStreamReader(new FileInputStream(fil)));
                    if (data.getVersion() != null) {
                        holder.setVersion(data.getVersion());
                    } else {
                        Versioning vers = data.getVersioning();
                        if (vers != null) {
                            holder.setVersion(vers.getLatest());
                        }
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (XmlPullParserException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        return holder;
    }
    
    private List collectPluginParams(Document pluginDoc, HintContext hintCtx) {
        Iterator it = pluginDoc.getRootElement().getDescendants(new Filter() {
            public boolean matches(Object object) {
                if (object instanceof Element) {
                    Element el = (Element)object;
                    if ("parameter".equals(el.getName()) &&
                            el.getParentElement() != null && "parameters".equals(el.getParentElement().getName()) &&
                            el.getParentElement().getParentElement() != null && "mojo".equals(el.getParentElement().getParentElement().getName())) {
                        return true;
                    }
                }
                return false;
            }
        });
        List toReturn = new ArrayList();
        Collection params = new HashSet();
        while (it.hasNext()) {
            Element el = (Element)it.next();
            String editable = el.getChildText("editable");
            if ("true".equalsIgnoreCase(editable)) {
                String name = el.getChildText("name");
                if (name.startsWith(hintCtx.getCurrentPrefix()) && !params.contains(name)) {
                    params.add(name);
                    toReturn.add(new MyElement(name));
                }
            }
        }
        return toReturn;
    }

    protected Enumeration getDynamicValueCompletion(String path, HintContext virtualTextCtx, Element el) {
        if (path.endsWith("executions/execution/goals/goal")) {
            Node previous;
            // HACK.. if currentPrefix is zero length, the context is th element, otherwise it's the content inside
            if (virtualTextCtx.getCurrentPrefix().length() == 0) {
                 previous = virtualTextCtx.getParentNode().getParentNode().getParentNode();
            } else {
                previous = virtualTextCtx.getParentNode().getParentNode().getParentNode().getParentNode();
            }
            previous = previous.getPreviousSibling();
            MavenEmbedder embedder = EmbedderFactory.getOnlineEmbedder();
            ArtifactInfoHolder info = findPluginInfo(previous, embedder, true);
            Document pluginDoc = loadDocument(info, embedder);
            if (pluginDoc != null) {
                return collectGoals(pluginDoc, virtualTextCtx);
            }
        }
        if (path.endsWith("executions/execution/phase")) {
            MavenEmbedder embedder = EmbedderFactory.getOnlineEmbedder();
            try {
                List phases = embedder.getLifecyclePhases();
                return super.createTextValueList((String[])phases.toArray(new String[phases.size()]), virtualTextCtx);
            } catch (MavenEmbedderException ex) {
                ex.printStackTrace();
            }
        }
        if (path.endsWith("dependencies/dependency/version") ||
            path.endsWith("plugins/plugin/version") ||
            path.endsWith("/project/parent/version")) {
            
            //poor mans solution, just check local repository for possible versions..
            // in future would be nice to include remote repositories somehow..
            Node previous;
            if (virtualTextCtx.getCurrentPrefix().length() == 0) {
                previous = virtualTextCtx.getPreviousSibling();
            } else {
                previous = virtualTextCtx.getParentNode().getPreviousSibling();
            }
            ArtifactInfoHolder hold = findPluginInfo(previous, null, false);
            if (hold.getGroupId() != null && hold.getArtifactId() != null) {
                File lev1 = new File(EmbedderFactory.getOnlineEmbedder().getLocalRepository().getBasedir(), hold.getGroupId().replace('.', File.separatorChar));
                File dir = new File(lev1, hold.getArtifactId());
                File[] versions = dir.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        if (pathname.isDirectory()) {
                            File[] subdirs = pathname.listFiles(new FileFilter() {
                                public boolean accept(File pathname) {
                                    return  pathname.isDirectory();
                                }
                            });
                            return subdirs.length == 0;
                        }
                        return false;
                    }
                });
                Collection elems = new ArrayList();
                for (int i = 0; i < versions.length; i++) {
                    if (versions[i].getName().startsWith(virtualTextCtx.getCurrentPrefix())) {
                        elems.add(new MyTextElement(versions[i].getName(), virtualTextCtx.getCurrentPrefix()));
                    }
                }
                return Collections.enumeration(elems);
            }
        }
        if (path.endsWith("dependencies/dependency/groupId")) {
            try {
                Set elems = CustomQueries.retrieveGroupIds(virtualTextCtx.getCurrentPrefix());
                Iterator it = elems.iterator();
                ArrayList texts = new ArrayList();
                while (it.hasNext()) {
                    String elem = (String) it.next();
                    texts.add(new MyTextElement(elem, virtualTextCtx.getCurrentPrefix()));
                }
                return Collections.enumeration(texts);
            } catch (RepositoryIndexSearchException ex) {
                ex.printStackTrace();
            }
        }
        if (path.endsWith("plugins/plugin/groupId")) {
            try {
                Set elems = CustomQueries.retrievePluginGroupIds(virtualTextCtx.getCurrentPrefix());
                Iterator it = elems.iterator();
                ArrayList texts = new ArrayList();
                while (it.hasNext()) {
                    String elem = (String) it.next();
                    texts.add(new MyTextElement(elem, virtualTextCtx.getCurrentPrefix()));
                }
                return Collections.enumeration(texts);
            } catch (RepositoryIndexSearchException ex) {
                ex.printStackTrace();
            }
        }
        if (path.endsWith("dependencies/dependency/artifactId")) {
            //poor mans solution, just check local repository for possible versions..
            // in future would be nice to include remote repositories somehow..
            Node previous;
            if (virtualTextCtx.getCurrentPrefix().length() == 0) {
                previous = virtualTextCtx.getPreviousSibling();
            } else {
                previous = virtualTextCtx.getParentNode().getPreviousSibling();
            }
            ArtifactInfoHolder hold = findArtifactInfo(previous);
            if (hold.getGroupId() != null) {
                try {
                    Set elems = CustomQueries.retrieveArtifactIdForGroupId(hold.getGroupId(), virtualTextCtx.getCurrentPrefix());
                    Iterator it = elems.iterator();
                    ArrayList texts = new ArrayList();
                    while (it.hasNext()) {
                        String elem = (String) it.next();
                        texts.add(new MyTextElement(elem, virtualTextCtx.getCurrentPrefix()));
                    }
                    return Collections.enumeration(texts);
                } catch (RepositoryIndexSearchException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (path.endsWith("plugins/plugin/artifactId")) {
            //poor mans solution, just check local repository for possible versions..
            // in future would be nice to include remote repositories somehow..
            Node previous;
            if (virtualTextCtx.getCurrentPrefix().length() == 0) {
                previous = virtualTextCtx.getPreviousSibling();
            } else {
                previous = virtualTextCtx.getParentNode().getPreviousSibling();
            }
            ArtifactInfoHolder hold = findArtifactInfo(previous);
            if (hold.getGroupId() != null) {
                try {
                    Set elems = CustomQueries.retrievePluginArtifactIds(hold.getGroupId(), virtualTextCtx.getCurrentPrefix());
                    Iterator it = elems.iterator();
                    ArrayList texts = new ArrayList();
                    while (it.hasNext()) {
                        String elem = (String) it.next();
                        texts.add(new MyTextElement(elem, virtualTextCtx.getCurrentPrefix()));
                    }
                    return Collections.enumeration(texts);
                } catch (RepositoryIndexSearchException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        if (path.endsWith("dependencies/dependency/scope")) {
            return super.createTextValueList(SCOPES, virtualTextCtx);
        }
        if (path.endsWith("repositories/repository/releases/updatePolicy") ||
            path.endsWith("repositories/repository/snapshots/updatePolicy") ||
            path.endsWith("pluginRepositories/pluginRepository/releases/updatePolicy") ||
            path.endsWith("pluginRepositories/pluginRepository/snapshots/updatePolicy")) {
            return super.createTextValueList(MavenSettingsGrammar.UPDATE_POLICIES, virtualTextCtx);
        }
        if (path.endsWith("repository/releases/checksumPolicy") ||
            path.endsWith("repository/snapshots/checksumPolicy") ||
            path.endsWith("pluginRepository/releases/checksumPolicy") ||
            path.endsWith("pluginRepository/snapshots/checksumPolicy")) {
            return super.createTextValueList(MavenSettingsGrammar.CHECKSUM_POLICIES, virtualTextCtx);
        }
        if (path.endsWith("repositories/repository/layout") ||
            path.endsWith("pluginRepositories/pluginRepository/layout") ||
            path.endsWith("distributionManagement/repository/layout")) {
            return super.createTextValueList(MavenSettingsGrammar.LAYOUTS, virtualTextCtx);
        }
        
        if ("/project/modules/module".equals(path)) {
            FileObject fo = getEnvironment().getFileObject();
            if (fo != null) {
                File dir = FileUtil.toFile(fo).getParentFile();  
            
                File[] modules = dir.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                         return pathname.isDirectory() && new File(pathname, "pom.xml").exists();
                    }
                });
                Collection elems = new ArrayList();
                for (int i = 0; i < modules.length; i++) {
                    if (modules[i].getName().startsWith(virtualTextCtx.getCurrentPrefix())) {
                        elems.add(new MyTextElement(modules[i].getName(), virtualTextCtx.getCurrentPrefix()));
                    }
                }
                return Collections.enumeration(elems);
            }
        }
        return null;
    }

    private Document loadDocument(ArtifactInfoHolder info, MavenEmbedder embedder) {
        if (info.getArtifactId() != null && info.getGroupId() != null && info.getVersion() != null) {
            Artifact art = embedder.createArtifact(info.getGroupId(), info.getArtifactId(), info.getVersion(), null, "jar");
            String repopath = embedder.getLocalRepository().pathOf(art);
            
            File fil = new File(embedder.getLocalRepository().getBasedir(), repopath);
            if (fil.exists()) {
                try {
                    JarFile jf = new JarFile(fil);
                    JarEntry entry = jf.getJarEntry("META-INF/maven/plugin.xml");
                    if (entry != null) {
                        InputStream str = jf.getInputStream(entry);
                        SAXBuilder builder = new SAXBuilder();
                        return builder.build(str);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (JDOMException ex) {
                    ex.printStackTrace();
                }
            }
            
        }
        return null;
    }

    private Enumeration collectGoals(Document pluginDoc, HintContext virtualTextCtx) {
        Iterator it = pluginDoc.getRootElement().getDescendants(new Filter() {
            public boolean matches(Object object) {
                if (object instanceof Element) {
                    Element el = (Element)object;
                    if ("goal".equals(el.getName()) &&
                            el.getParentElement() != null && "mojo".equals(el.getParentElement().getName())) {
                        return true;
                    }
                }
                return false;
            }
        });
        Collection toReturn = new ArrayList();
        while (it.hasNext()) {
            Element el = (Element)it.next();
            String name = el.getText();
            if (name.startsWith(virtualTextCtx.getCurrentPrefix())) {
               toReturn.add(new MyTextElement(name, virtualTextCtx.getCurrentPrefix()));
            }
        }
        return Collections.enumeration(toReturn);
    }

    
    private static class ArtifactInfoHolder  {
        private String artifactId;
        private String groupId;
        private String version;
        
        public String getArtifactId() {
            return artifactId;
        }
        
        public void setArtifactId(String artifactId) {
            this.artifactId = artifactId;
        }
        
        public String getGroupId() {
            return groupId;
        }
        
        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
        
        public String getVersion() {
            return version;
        }
        
        public void setVersion(String version) {
            this.version = version;
        }
        
    }
    
}
