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
import org.apache.maven.project.InvalidProjectModelException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.validation.ModelValidationResult;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.NbArtifact;
import org.codehaus.mevenide.netbeans.nodes.DependenciesNode;
import org.openide.cookies.EditCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author mkleint
 */
public final class ProblemReporter implements Comparator<ProblemReport> {
    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
    private final Set<ProblemReport> reports;
    private NbMavenProject nbproject;
    
    /** Creates a new instance of ProblemReporter */
    public ProblemReporter(NbMavenProject proj) {
        reports = new TreeSet<ProblemReport>(this);
        nbproject = proj;
    }
    
    public void addChangeListener(ChangeListener list) {
        listeners.add(list);
    }
    
    public void removeChangeListener(ChangeListener list) {
        listeners.remove(list);
    }
    
    public void addReport(ProblemReport report) {
        synchronized (reports) {
            reports.add(report);
        }
        fireChange();
    }
    
    public void addReports(ProblemReport[] report) {
        synchronized (reports) {
            for (int i = 0; i < report.length; i++) {
                reports.add(report[i]);
            }
        }
        fireChange();
    }
    
    public void removeReport(ProblemReport report) {
        synchronized (reports) {
            reports.add(report);
        }
        fireChange();
    }
    
    private void fireChange() {
        for (ChangeListener list : listeners) {
            list.stateChanged(new ChangeEvent(this));
        }
    }
    
    public Collection getReports() {
        synchronized (reports) {
            return new ArrayList<ProblemReport>(reports);
        }
    }
    
    public void clearReports() {
        synchronized (reports) {
            reports.clear();
        }
        fireChange();
    }
    
    public int compare(ProblemReport o1, ProblemReport o2) {
        int ret = new Integer(o1.getSeverityLevel()).compareTo(
                new Integer(o2.getSeverityLevel()));
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
                    org.openide.util.NbBundle.getMessage(ProblemReporter.class, "ERR_Project_validation."), exc.getValidationResult().render("\n"), new OpenPomAction(nbproject)); //NOI18N
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
                List<Artifact> missingJars = new ArrayList<Artifact>();
                Iterator it = compileArts.iterator();
                while (it.hasNext()) {
                    NbArtifact art = (NbArtifact) it.next();
                    if (art.getFile() != null && art.isFakedSystemDependency()) {
                        //TODO create a correction action for this.
                        ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_MEDIUM,
                                org.openide.util.NbBundle.getMessage(ProblemReporter.class, "ERR_SystemScope"),
                                org.openide.util.NbBundle.getMessage(ProblemReporter.class, "MSG_SystemScope"), 
                                new OpenPomAction(nbproject));
                        addReport(report);
                    } else if (art.getFile() == null || !art.getFile().exists()) {
                        missingJars.add(art);
                    }
                }
                if (missingJars.size() > 0) {
                    //TODO create a correction action for this.
                    Iterator<Artifact> it2 = missingJars.iterator();
                    String mess = ""; //NOI18N
                    while (it2.hasNext()) {
                        Artifact ar = it2.next();
                        mess = mess + ar.getId() + "\n"; //NOI18N
                    }
                    AbstractAction act = new DependenciesNode.ResolveDepsAction(nbproject);
                    act.putValue(Action.NAME, org.openide.util.NbBundle.getMessage(ProblemReporter.class, "ACT_DownloadDeps"));
                    
                    ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_MEDIUM,
                            org.openide.util.NbBundle.getMessage(ProblemReporter.class, "ERR_NonLocal"),
                            org.openide.util.NbBundle.getMessage(ProblemReporter.class, "MSG_NonLocal", mess),
                            act);
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
            }
            if (nbart.getNonFakedFile() != null && !nbart.getNonFakedFile().exists()) {
                //TODO create a correction action for this.
                ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_HIGH,
                        org.openide.util.NbBundle.getMessage(ProblemReporter.class, "ERR_NoParent"),
                        org.openide.util.NbBundle.getMessage(ProblemReporter.class, "MSG_NoParent", nbart.getId()),
                        new OpenPomAction(nbproject));
                addReport(report);
            }
        }
    }

    
    static class OpenPomAction extends AbstractAction {
        
        private NbMavenProject project;
        private String filepath;
        
        OpenPomAction(NbMavenProject proj) {
            putValue(Action.NAME, org.openide.util.NbBundle.getMessage(ProblemReporter.class, "ACT_OpenPom"));
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
                    EditCookie edit = dobj.getCookie(EditCookie.class);
                    edit.edit();
                } catch (DataObjectNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    
}
