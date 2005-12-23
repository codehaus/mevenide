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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.codehaus.mevenide.grammar.AbstractSchemaBasedGrammar.MyElement;
import org.codehaus.mevenide.grammar.AbstractSchemaBasedGrammar.MyTextElement;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.HintContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * xml completion grammar based on xsd, additionally allowing more to be added.
 */
public class MavenProjectGrammar extends AbstractSchemaBasedGrammar {
    
    
    public MavenProjectGrammar() {
    }
    
    protected InputStream getSchemaStream() {
        return getClass().getResourceAsStream("/org/codehaus/mevenide/grammar/maven-4.0.0.xsd");
    }
    
    public boolean isSupported(GrammarEnvironment env) {
        if (env.getFileObject().getNameExt().equals("pom.xml")) {
            return true;
        }
        return false;
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
            PluginInfoHolder info = findPluginInfo(previous, embedder, true);
            Document pluginDoc = loadDocument(info, embedder);
            if (pluginDoc != null) {
                return collectPluginParams(pluginDoc, hintCtx);
            }
        }
        return Collections.EMPTY_LIST;
    }
    
    private PluginInfoHolder findPluginInfo(Node previous, MavenEmbedder embedder, boolean checkLocalRepo) {
        String artifact = null;
        String group = null;
        String version = null;
        PluginInfoHolder holder = new PluginInfoHolder();
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
        if (checkLocalRepo && holder.getVersion() == null && holder.getArtifactId() != null && holder.getGroupId() != null) {
            File lev1 = new File(MavenSettingsSingleton.getInstance().getSettings().getLocalRepository(), holder.getGroupId().replace('.', File.separatorChar));
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
        if ("/project/build/plugins/plugin/executions/execution/goals/goal".equals(path) ||
            "/project/build/pluginManagement/plugins/plugin/executions/execution/goals/goal".equals(path)) {
            Node previous = virtualTextCtx.getParentNode().getParentNode().getParentNode();
            previous = previous.getPreviousSibling();
            MavenEmbedder embedder = EmbedderFactory.getOnlineEmbedder();
            PluginInfoHolder info = findPluginInfo(previous, embedder, true);
            Document pluginDoc = loadDocument(info, embedder);
            if (pluginDoc != null) {
                return collectGoals(pluginDoc, virtualTextCtx);
            }
        }
        if ("/project/build/plugins/plugin/executions/execution/phase".equals(path) ||
            "/project/build/pluginManagement/plugins/plugin/executions/execution/phase".equals(path)) {
            Node previous = virtualTextCtx.getParentNode().getParentNode().getParentNode();
            previous = previous.getPreviousSibling();
            MavenEmbedder embedder = EmbedderFactory.getOnlineEmbedder();
            try {
                List phases = embedder.getLifecyclePhases();
                Iterator it = phases.iterator();
                Collection col = new ArrayList();
                while (it.hasNext()) {
                    String str = (String)it.next();
                    if (str.startsWith(virtualTextCtx.getCurrentPrefix())) {
                        col.add(new MyTextElement(str));
                    }
                }
                return Collections.enumeration(col);
            } catch (MavenEmbedderException ex) {
                ex.printStackTrace();
            }
        }
        if ("/project/dependencies/dependency/version".equals(path) ||
            "/project/dependencyManagement/dependencies/dependency/version".equals(path) || 
            "/project/build/plugins/plugin/version".equals(path) ||
            "/project/build/pluginManagement/plugins/plugin/version".equals(path) || 
            "/project/parent/version".equals(path)) {
            
            //poor mans solution, just check local repository for possible versions..
            // in future would be nice to include remote repositories somehow..
            PluginInfoHolder hold = findPluginInfo(virtualTextCtx.getPreviousSibling(), null, false);
            if (hold.getGroupId() != null && hold.getArtifactId() != null) {
                File lev1 = new File(MavenSettingsSingleton.getInstance().getSettings().getLocalRepository(), hold.getGroupId().replace('.', File.separatorChar));
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
                        elems.add(new MyTextElement(versions[i].getName()));
                    }
                }
                return Collections.enumeration(elems);
            }
        }
        if ("/project/dependencyManagement/dependencies/dependency/scope".equals(path) ||
            "/project/dependencies/dependency/scope".equals(path)) {
            String[] scopes = new String[] {
                "compile",
                "test",
                "runtime",
                "provided",
                "system"
            };
            Collection elems = new ArrayList();
            for (int i = 0; i < scopes.length; i++) {
                if (scopes[i].startsWith(virtualTextCtx.getCurrentPrefix())) {
                    elems.add(new MyTextElement(scopes[i]));
                }
            }
            return Collections.enumeration(elems);
        }
        return null;
    }

    private Document loadDocument(PluginInfoHolder info, MavenEmbedder embedder) {
        if (info.getArtifactId() != null && info.getGroupId() != null && info.getVersion() != null) {
            Artifact art = embedder.createArtifact(info.getGroupId(), info.getArtifactId(), info.getVersion(), null, "jar");
            String repopath = embedder.getLocalRepository().pathOf(art);
            
            File fil = new File(MavenSettingsSingleton.getInstance().getSettings().getLocalRepository(), repopath);
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
               toReturn.add(new MyTextElement(name));
            }
        }
        return Collections.enumeration(toReturn);
    }

    
    private static class PluginInfoHolder  {
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
