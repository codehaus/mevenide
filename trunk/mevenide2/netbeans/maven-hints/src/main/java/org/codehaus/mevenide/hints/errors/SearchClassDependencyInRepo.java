/*
 *  Copyright 2008 Anuradha.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.hints.errors;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.hints.ui.SearchDependencyUI;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.RepositoryQueries;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.java.hints.spi.ErrorRule;
import org.netbeans.modules.java.hints.spi.ErrorRule.Data;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.EnhancedFix;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Anuradha G
 */
public class SearchClassDependencyInRepo implements ErrorRule<Void> {

    public SearchClassDependencyInRepo() {
    }

    public Set<String> getCodes() {
        return new HashSet<String>(Arrays.asList(
                "compiler.err.cant.resolve",//NOI18N
                "compiler.err.cant.resolve.location",//NOI18N
                "compiler.err.doesnt.exist",//NOI18N
                "compiler.err.not.stmt"));//NOI18N
    }

    public List<Fix> run(final CompilationInfo info, String diagnosticKey,
            final int offset, TreePath treePath, Data<Void> data) {
        
        if (!SearchClassDependencyHint.isHintEnabled()) {
            return Collections.emptyList();
        }
        //copyed from ImportClass
        int errorPosition = offset + 1; //TODO: +1 required to work OK, rethink

        if (errorPosition == (-1)) {

            return Collections.<Fix>emptyList();
        }
        //copyed from ImportClass-end
        FileObject fileObject = info.getFileObject();
        Project project = FileOwnerQuery.getOwner(fileObject);
        if (project == null) {
            return Collections.emptyList();
        }
        NbMavenProject mavProj = project.getLookup().lookup(NbMavenProject.class);
        if (mavProj == null) {
            return Collections.emptyList();
        }


        //copyed from ImportClass
        TreePath path = info.getTreeUtilities().pathFor(errorPosition);

        if (path.getParentPath() != null && path.getParentPath().getLeaf().getKind() == Kind.METHOD_INVOCATION) {
            //#86313:
            //if the error is in the type parameter, import should be proposed:
            MethodInvocationTree mit = (MethodInvocationTree) path.getParentPath().getLeaf();

            if (!mit.getTypeArguments().contains(path.getLeaf())) {
                return Collections.<Fix>emptyList();
            }
        }

        Token ident = null;

        try {
            ident = findUnresolvedElementToken(info, offset);
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }



        if (ident == null) {
            return Collections.<Fix>emptyList();
        }

        String simpleName = ident.text().toString();
        //copyed from ImportClass-end
        boolean isTestSource = false;

        MavenProject mp = mavProj.getOriginalMavenProject();
        String testSourceDirectory = mp.getBuild().getTestSourceDirectory();
        File testdir = new File(testSourceDirectory);

        FileObject fo = FileUtil.toFileObject(testdir);
        //need check null because Test Dir may null
        if (fo != null) {
            isTestSource = FileUtil.isParentOf(fo, fileObject);
        }

        List<Fix> fixes = new ArrayList<Fix>();
        if (SearchClassDependencyHint.isSearchDialog()) { 

            fixes.add(new MavenSearchFix(mavProj, simpleName, isTestSource));
        } else {
            //mkleint: this option is has rather serious performance impact.
            // we need to work on performance before we enable it..
            Collection<NBVersionInfo> findVersionsByClass = filter(mavProj,
                    RepositoryQueries.findVersionsByClass(simpleName), isTestSource);



            for (NBVersionInfo nbvi : findVersionsByClass) {
                fixes.add(new MavenFixImport(mavProj, nbvi, isTestSource));
            }
        }

        return fixes;
    }

    private Collection<NBVersionInfo> filter(NbMavenProject mavProj, List<NBVersionInfo> nbvis, boolean test) {


        Map<String, NBVersionInfo> items = new HashMap<String, NBVersionInfo>();
        //check dependency already added
        List<Dependency> dependencies = new ArrayList<Dependency>();
        MavenProject prj = mavProj.getOriginalMavenProject();
        if (test) {
            dependencies.addAll(prj.getTestDependencies());
        } else {
            dependencies.addAll(prj.getDependencies());
        }

        for (NBVersionInfo info : nbvis) {
            String key = info.getGroupId() + ":" + info.getArtifactId();

            boolean b = items.containsKey(key);
            if (!b) {
                items.put(key, info);
            }
            for (Dependency dependency : dependencies) {
                //check group id and ArtifactId and Scope even
                if (dependency.getGroupId() != null && dependency.getGroupId().equals(info.getGroupId())) {
                    if (dependency.getArtifactId() != null && dependency.getArtifactId().equals(info.getArtifactId())) {
                        if (!test && dependency.getScope() != null && ("compile".equals(dependency.getScope()))) {//NOI18N

                            return Collections.emptyList();
                        }
                    }
                }
            }

        }
        List<NBVersionInfo> filterd = new ArrayList<NBVersionInfo>(items.values());

        return filterd;

    }
    //copyed from ImportClass

    public static Token findUnresolvedElementToken(CompilationInfo info, int offset) throws IOException {
        TokenHierarchy<?> th = info.getTokenHierarchy();
        TokenSequence<JavaTokenId> ts = th.tokenSequence(JavaTokenId.language());

        if (ts == null) {
            return null;
        }

        ts.move(offset);
        if (ts.moveNext()) {
            Token t = ts.token();

            if (t.id() == JavaTokenId.DOT) {
                ts.moveNext();
                t = ts.token();
            } else {
                if (t.id() == JavaTokenId.LT) {
                    ts.moveNext();
                    t = ts.token();
                } else {
                    if (t.id() == JavaTokenId.NEW) {
                        boolean cont = ts.moveNext();

                        while (cont && ts.token().id() == JavaTokenId.WHITESPACE) {
                            cont = ts.moveNext();
                        }

                        if (!cont) {
                            return null;
                        }
                        t = ts.token();
                    }
                }
            }

            if (t.id() == JavaTokenId.IDENTIFIER) {
                return ts.offsetToken();
            }
        }
        return null;
    }

    /*
     *Copyed from org.codehaus.mevenide.netbeans.nodes.DependenciesNode
     * 
     *  this method should  provided as API. (mkleint)?
     */
    public static void addDependency(NbMavenProject project,
            String group,
            String artifact,
            String version,
            String type,
            String scope,
            String classifier) {
        FileObject fo = project.getProjectDirectory().getFileObject("pom.xml"); //NOI18N

        Model model = WriterUtils.loadModel(fo);
        if (model != null) {
            Dependency dep = PluginPropertyUtils.checkModelDependency(model, group, artifact, true);
            dep.setVersion(version);

            dep.setScope(scope);

            if (type != null) {
                dep.setType(type);
            }
            if (classifier != null) {
                dep.setClassifier(classifier);
            }
            try {
                WriterUtils.writePomModel(fo, model);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getId() {
        return "MAVEN_MISSING_CLASS";//NOI18N
    }

    public String getDisplayName() {
        return NbBundle.getMessage(SearchClassDependencyInRepo.class, "LBL_Class_Search_DisplayName");
    }

    public void cancel() {
        // Does nothing
    }

    static final class MavenFixImport implements EnhancedFix {

        private NbMavenProject mavProj;
        private NBVersionInfo nbvi;
        private boolean test;

        public MavenFixImport(NbMavenProject mavProj, NBVersionInfo nbvi, boolean test) {
            this.mavProj = mavProj;
            this.nbvi = nbvi;
            this.test = test;
        }

        public CharSequence getSortText() {
            return getText();
        }

        public String getText() {
            return NbBundle.getMessage(SearchClassDependencyInRepo.class,
                    "LBL_Class_Search_Fix", nbvi.getGroupId() + " : " + nbvi.getArtifactId() + " : " + nbvi.getVersion());

        }

        public ChangeInfo implement() throws Exception {
            addDependency(mavProj, nbvi.getGroupId(), nbvi.getArtifactId(),
                    nbvi.getVersion(), nbvi.getType(), test ? "test" : null, null);//NOI18N

            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    mavProj.getLookup().lookup(ProjectURLWatcher.class).triggerDependencyDownload();
                }
            });
            return null;
        }
    }

    static final class MavenSearchFix implements EnhancedFix {

        private NbMavenProject mavProj;
        private String clazz;
        private boolean test;

        public MavenSearchFix(NbMavenProject mavProj, String clazz, boolean test) {
            this.mavProj = mavProj;
            this.clazz = clazz;
            this.test = test;
        }

        public CharSequence getSortText() {
            return getText();
        }

        public String getText() {
            return org.openide.util.NbBundle.getMessage(SearchClassDependencyInRepo.class, "LBL_Class_Search_ALL_Fix", clazz);

        }

        public ChangeInfo implement() throws Exception {
            NBVersionInfo nbvi = null;
            SearchDependencyUI dependencyUI = new SearchDependencyUI(clazz);

            DialogDescriptor dd = new DialogDescriptor(dependencyUI,
                    org.openide.util.NbBundle.getMessage(SearchClassDependencyInRepo.class, "LBL_Search_Repo"));
            dd.setClosingOptions(new Object[]{
                        dependencyUI.getAddButton(),
                        DialogDescriptor.CANCEL_OPTION
                    });
            dd.setOptions(new Object[]{
                        dependencyUI.getAddButton(),
                        DialogDescriptor.CANCEL_OPTION
                    });
            Object ret = DialogDisplayer.getDefault().notify(dd);
            if (dependencyUI.getAddButton() == ret) {
                nbvi = dependencyUI.getSelectedVersion();
            }

            if (nbvi != null) {
                addDependency(mavProj, nbvi.getGroupId(), nbvi.getArtifactId(),
                        nbvi.getVersion(), nbvi.getType(), test ? "test" : null, null);//NOI18N

                RequestProcessor.getDefault().post(new Runnable() {

                    public void run() {
                        mavProj.getLookup().lookup(ProjectURLWatcher.class).triggerDependencyDownload();
                    }
                });
            }
            return null;
        }
    }
}