/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.problems;

import org.netbeans.modules.maven.api.problem.ProblemReport;
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
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.api.problem.ProblemReporter;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.embedder.NbArtifact;
import org.netbeans.modules.maven.nodes.DependenciesNode;
import org.openide.cookies.EditCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
public final class ProblemReporterImpl implements ProblemReporter, Comparator<ProblemReport> {
    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
    private final Set<ProblemReport> reports;
    private NbMavenProjectImpl nbproject;
    
    /** Creates a new instance of ProblemReporter */
    public ProblemReporterImpl(NbMavenProjectImpl proj) {
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
                    org.openide.util.NbBundle.getMessage(ProblemReporterImpl.class, "ERR_Project_validation."), exc.getValidationResult().render("\n"), new OpenPomAction(nbproject)); //NOI18N
            addReport(report);
        }
    }
    
    public void doBaseProblemChecks(MavenProject project) {
        String packaging = nbproject.getProjectWatcher().getPackagingType();
        if (NbMavenProject.TYPE_WAR.equals(packaging) ||
            NbMavenProject.TYPE_EAR.equals(packaging) ||
            NbMavenProject.TYPE_EJB.equals(packaging)) {
            Collection<? extends ModuleInfo> infos = Lookup.getDefault().lookupAll(ModuleInfo.class);
            boolean foundJ2ee = false;
            for (ModuleInfo info : infos) {
                if ("org.netbeans.modules.maven.j2ee".equals(info.getCodeNameBase()) && //NOI18N
                        info.isEnabled()) {
                    foundJ2ee = true;
                    break;
                }
            }
            if (!foundJ2ee) {
                ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_MEDIUM,
                    NbBundle.getMessage(ProblemReporterImpl.class, "ERR_MissingJ2eeModule"),
                    NbBundle.getMessage(ProblemReporterImpl.class, "MSG_MissingJ2eeModule"), 
                    null);
                addReport(report);
            }
        } else if (NbMavenProject.TYPE_NBM.equals(packaging)) {
            Collection<? extends ModuleInfo> infos = Lookup.getDefault().lookupAll(ModuleInfo.class);
            boolean foundApisupport = false;
            for (ModuleInfo info : infos) {
                if ("org.netbeans.modules.maven.apisupport".equals(info.getCodeNameBase()) && //NOI18N
                        info.isEnabled()) {
                    foundApisupport = true;
                    break;
                }
            }
            if (!foundApisupport) {
                ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_MEDIUM,
                    NbBundle.getMessage(ProblemReporterImpl.class, "ERR_MissingApisupportModule"),
                    NbBundle.getMessage(ProblemReporterImpl.class, "MSG_MissingApisupportModule"), 
                    null);
                addReport(report);
            }
        }   
        
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
                                org.openide.util.NbBundle.getMessage(ProblemReporterImpl.class, "ERR_SystemScope"),
                                org.openide.util.NbBundle.getMessage(ProblemReporterImpl.class, "MSG_SystemScope"), 
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
                    act.putValue(Action.NAME, org.openide.util.NbBundle.getMessage(ProblemReporterImpl.class, "ACT_DownloadDeps"));
                    
                    ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_MEDIUM,
                            org.openide.util.NbBundle.getMessage(ProblemReporterImpl.class, "ERR_NonLocal"),
                            org.openide.util.NbBundle.getMessage(ProblemReporterImpl.class, "MSG_NonLocal", mess),
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
                        org.openide.util.NbBundle.getMessage(ProblemReporterImpl.class, "ERR_NoParent"),
                        org.openide.util.NbBundle.getMessage(ProblemReporterImpl.class, "MSG_NoParent", nbart.getId()),
                        new OpenPomAction(nbproject));
                addReport(report);
            }
        }
    }

    
    static class OpenPomAction extends AbstractAction {
        
        private NbMavenProjectImpl project;
        private String filepath;
        
        OpenPomAction(NbMavenProjectImpl proj) {
            putValue(Action.NAME, org.openide.util.NbBundle.getMessage(ProblemReporterImpl.class, "ACT_OpenPom"));
            project = proj;
        }
        
        OpenPomAction(NbMavenProjectImpl project, String filePath) {
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
