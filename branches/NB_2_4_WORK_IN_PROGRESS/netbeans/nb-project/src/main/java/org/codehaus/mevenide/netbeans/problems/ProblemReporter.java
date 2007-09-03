/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

package org.codehaus.mevenide.netbeans.problems;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.project.InvalidProjectModelException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.validation.ModelValidationResult;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.NbArtifact;
import org.openide.cookies.EditCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author mkleint
 */
public final class ProblemReporter implements Comparator {
    private List listeners = new ArrayList();
    private Set reports;
    private NbMavenProject nbproject;
    
    /** Creates a new instance of ProblemReporter */
    public ProblemReporter(NbMavenProject proj) {
        reports = new TreeSet(this);
        nbproject = proj;
    }
    
    public void addChangeListener(ChangeListener list) {
        listeners.add(list);
    }
    
    public void removeChangeListener(ChangeListener list) {
        listeners.remove(list);
    }
    
    public void addReport(ProblemReport report) {
        reports.add(report);
        fireChange();
    }
    
    public void addReports(ProblemReport[] report) {
        for (int i = 0; i < report.length; i++) {
            reports.add(report);
        }
        fireChange();
    }
    
    public void removeReport(ProblemReport report) {
        reports.add(report);
        fireChange();
    }
    
    private void fireChange() {
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            ChangeListener list = (ChangeListener) it.next();
            list.stateChanged(new ChangeEvent(this));
        }
    }
    
    public Collection getReports() {
        return new ArrayList(reports);
    }
    
    public void clearReports() {
        reports.clear();
        fireChange();
    }
    
    public int compare(Object o1, Object o2) {
        assert o1 instanceof ProblemReport;
        assert o2 instanceof ProblemReport;
        int ret = new Integer(((ProblemReport)o1).getSeverityLevel()).compareTo(
                new Integer(((ProblemReport)o2).getSeverityLevel()));
        if (ret != 0) {
            return ret;
        }
        return 1;
        
    }
    
    public void addValidatorReports(InvalidProjectModelException exc) {
        ModelValidationResult res = exc.getValidationResult();
        if (res == null) {
            return;
        }
        List messages = exc.getValidationResult().getMessages();
        if (messages != null && messages.size() > 0) {
            ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_HIGH,
                    "Project model validation failed.", exc.getValidationResult().render("\n"), new OpenPomAction(nbproject));
            addReport(report);
        }
    }
    
    public void doBaseProblemChecks(MavenProject project) {
        //TODO.. non existing dependencies, not declared app server/j2se platform etc..
        if (project != null) {
            MavenProject parent = project;
            while (parent != null) {
                checkParent(parent);
                parent = parent.getParent();
            }
            List compileArts = project.getTestArtifacts();
            if (compileArts != null) {
                List missingJars = new ArrayList();
                Iterator it = compileArts.iterator();
                while (it.hasNext()) {
                    NbArtifact art = (NbArtifact) it.next();
                    if (art.getFile() != null && art.isFakedSystemDependency()) {
                        //TODO create a correction action for this.
                        ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_MEDIUM,
                                "A 'system' scope dependency was not found. Code completion is affected. ",
                                "There is a 'system' scoped dependency in the project but the path to the binary is not valid." +
                                "Please check that the path is absolute and points to an existing binary.", new OpenPomAction(nbproject));
                        addReport(report);
                    } else if (art.getFile() == null || !art.getFile().exists()) {
                        missingJars.add(art);
                    }
                }
                if (missingJars.size() > 0) {
                    //TODO create a correction action for this.
                    Iterator it2 = missingJars.iterator();
                    String mess = "";
                    while (it2.hasNext()) {
                        Artifact ar = (Artifact)it2.next();
                        mess = mess + ar.getId() + "\n";
                    }
                    ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_MEDIUM,
                            "Some dependency artifacts are not in local repository.",
                            "Your project has dependencies that are not resolved locally. Code completion" +
                            " in the ide will not include classes from these dependencies" +
                            "and their transitive dependencies neither (unless they are among the opened projects)." +
                            "Please download the dependencies, or install them manually, if not available remotely.\n\n" +
                            "The artifacts are:\n" + mess, null);
                    addReport(report);
                }
                
            }
        }
    }
    
    private void checkParent(final MavenProject project) {
        Artifact art = project.getParentArtifact();
        if (art != null && art instanceof NbArtifact) {
            
            File parent = project.getParent().getFile();
            if (parent != null && parent.exists()) {
                return;
            }
            NbArtifact nbart = (NbArtifact)art;
            try {
                // shouldnot be necessary after update to maven embedder sources 20/9/2006 and later.
                EmbedderFactory.getProjectEmbedder().resolve(nbart, Collections.EMPTY_LIST, EmbedderFactory.getProjectEmbedder().getLocalRepository());
                //getFile to create the fake file etc..
                nbart.getFile();
            } catch (ArtifactResolutionException ex) {
                ex.printStackTrace();
            } catch (ArtifactNotFoundException ex) {
                ex.printStackTrace();
            } catch (MavenEmbedderException ex) {
                ex.printStackTrace();
            }
            if (nbart.getNonFakedFile() != null && !nbart.getNonFakedFile().exists()) {
                //TODO create a correction action for this.
                ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_HIGH,
                        "Parent pom file is not accessible. Project might be inproperly setup.",
                        "The parent pom with id " + nbart.getId() + " was not found in sources or local repository. Please check that <relativePath> tag is correct is present, the version of parent pom in sources matches the version defined. " +
                        "If parent is only available thought remote repository, please check that the repository hosting it is defined in the current pom.", new OpenPomAction(nbproject));
                addReport(report);
            }
        }
    }

    
    static class OpenPomAction extends AbstractAction {
        
        private NbMavenProject project;
        private String filepath;
        
        private OpenPomAction() {
            putValue(Action.NAME, "Open pom.xml");
        }
        
        OpenPomAction(NbMavenProject proj) {
            this();
            project = proj;
        }
        
        OpenPomAction(NbMavenProject project, String filePath) {
            this(project);
            filepath = filePath;
        }
        
        public void actionPerformed(ActionEvent e) {
            FileObject fo = null;
            if (filepath != null) {
                fo = FileUtil.toFileObject(FileUtil.normalizeFile(new File(filepath)));
            } else {
                fo = FileUtil.toFileObject(project.getPOMFile());
            }
            if (fo != null) {
                try {
                    DataObject dobj = DataObject.find(fo);
                    EditCookie edit = (EditCookie)dobj.getCookie(EditCookie.class);
                    edit.edit();
                } catch (DataObjectNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
}
