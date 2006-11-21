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

import java.beans.FeatureDescriptor;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.PluginPropertyUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarQueryManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.w3c.dom.Node;

public final class MavenQueryProvider extends GrammarQueryManager {

    private List<GrammarFactory> grammars;
    public MavenQueryProvider() {
        grammars = new ArrayList<GrammarFactory>();
        // TODO make regitrable/pluggable somehow
        grammars.add(new DefaultGrammarFactory());
    }
    
    public Enumeration enabled(GrammarEnvironment ctx) {
        Enumeration en = ctx.getDocumentChildren();
        while (en.hasMoreElements()) {
            Node next = (Node)en.nextElement();
            if (next.getNodeType() == next.ELEMENT_NODE) {
                return Collections.enumeration(Collections.singletonList(next));
            }
        }
        return null;
    }
    
    public FeatureDescriptor getDescriptor() {
        return new FeatureDescriptor();
    }
    
    public GrammarQuery getGrammar(GrammarEnvironment env) {
        for (GrammarFactory gr : grammars) {
            GrammarQuery query = gr.isSupported(env);
            if (query != null) {
                return query;
            }
        }
        return null;
    }
    
    private static class DefaultGrammarFactory extends GrammarFactory {
        
        public GrammarQuery isSupported(GrammarEnvironment env) {
            FileObject fo = env.getFileObject();
            Project owner = FileOwnerQuery.getOwner(fo);
            if (fo.getNameExt().equals("pom.xml") &&  //NOI18N
                owner.getProjectDirectory().equals(fo.getParent())) {
                //TODO also locate by namespace??
                return new MavenProjectGrammar(env);
            }
            if (fo.getNameExt().equals("settings.xml") &&  //NOI18N
                fo.getParent() != null && ".m2".equalsIgnoreCase(fo.getParent().getNameExt())) { //NOI18N
                //TODO also locate by namespace??
                //TODO more proper condition
                return new MavenSettingsGrammar(env);
            }
            if (fo.getNameExt().equals("profiles.xml") &&  //NOI18N
                owner.getProjectDirectory().equals(fo.getParent())) {
                //TODO also locate by namespace??
                //TODO more proper condition
                return new MavenProfilesGrammar(env);
            }
            File file = FileUtil.toFile(fo);
            if (owner instanceof NbMavenProject) {
                if ("src/main/resources/META-INF/archetype.xml".equals(FileUtil.getRelativePath(owner.getProjectDirectory(), env.getFileObject()))) { //NOI18N
                    return new MavenArchetypeGrammar(env);
                }
                //TODO also locate by namespace??
                NbMavenProject mProject = (NbMavenProject)owner;
                String desc = PluginPropertyUtils.getPluginProperty(mProject, 
                        "org.apache.maven.plugins",  //NOI18N
                        "maven-assembly-plugin",  //NOI18N
                        "descriptor", "assembly"); //NOI18N
                if (desc == null) {
                    desc = PluginPropertyUtils.getPluginProperty(mProject, 
                            "org.apache.maven.plugins",  //NOI18N
                            "maven-assembly-plugin",  //NOI18N
                            "descriptor", "directory"); //NOI18N
                }
                if (desc != null) {
                    URI uri = FileUtilities.getDirURI(mProject.getProjectDirectory(), desc);
                    if (uri != null && new File(uri).equals(file)) {
                        return new MavenAssemblyGrammar(env);
                    }
                }
                desc = PluginPropertyUtils.getPluginProperty(mProject, 
                        "org.codehaus.mojo",  //NOI18N
                        "nbm-maven-plugin",  //NOI18N
                        "descriptor", "jar"); //NOI18N
                if (desc == null) {
                    desc = PluginPropertyUtils.getPluginProperty(mProject, 
                            "org.codehaus.mevenide.plugins",  //NOI18N
                            "maven-nbm-plugin",  //NOI18N
                            "descriptor", "jar"); //NOI18N
                }
                if (desc != null) {
                    URI uri = FileUtilities.getDirURI(mProject.getProjectDirectory(), desc);
                    if (uri != null && new File(uri).equals(file)) {
                        return new MavenNbmGrammar(env);
                    }
                }
            }
            return null;
        }
        
        
    }
    
}
