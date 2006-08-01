/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.newproject;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.execute.BeanRunConfig;
import org.codehaus.mevenide.netbeans.execute.MavenJavaExecutor;
import org.codehaus.mevenide.netbeans.execute.RunConfig;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 *@author mkleint
 */
public class MavenWizardIterator implements WizardDescriptor.InstantiatingIterator {
    
    private static final long serialVersionUID = 1L;
    
    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    private transient WizardDescriptor wiz;
    
    public MavenWizardIterator() {}
    
    public static MavenWizardIterator createIterator() {
        return new MavenWizardIterator();
    }
    
    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[] {
            new ChooseWizardPanel(),
            new BasicWizardPanel()
        };
    }
    
    private String[] createSteps() {
        return new String[] {
            NbBundle.getMessage(MavenWizardIterator.class, "LBL_CreateProjectStep"),
            NbBundle.getMessage(MavenWizardIterator.class, "LBL_CreateProjectStep2")
        };
    }
    
    public Set/*<FileObject>*/ instantiate() throws IOException {
        
        final File dirF = FileUtil.normalizeFile((File) wiz.getProperty("projdir"));
        final File parent = dirF.getParentFile();
        if (parent != null && parent.exists()) {
            ProjectChooser.setProjectsFolder(parent);
        }
        
        Set resultSet = new LinkedHashSet();
        File root = FileUtil.normalizeFile(new File(System.getProperty("java.io.tmpdir")));
        int index = 0;
        File dir = new File(root, "tempmavenproject" + index);
        while (dir.exists()) {
            index = index + 1;
            dir = new File(root, "tempmavenproject" + index);
        }
        boolean isPosted = true;
        File tempPomFile = null;
        if (dir.mkdir()) {
            dir.deleteOnExit();
            tempPomFile = new File(dir, "pom.xml.temp");
            tempPomFile.createNewFile();
            tempPomFile.deleteOnExit();
            resultSet.add(FileUtil.toFileObject(dir));
        } else {
            isPosted = false;
        }
        final File fTempFile = tempPomFile;
        final boolean fIsPosted = isPosted;
        final FileObject fTtemplate = Templates.getTemplate(wiz);
        final String art = (String)wiz.getProperty("artifactId");
        final String ver = (String)wiz.getProperty("version");
        final String gr = (String)wiz.getProperty("groupId");
        final String pack = (String)wiz.getProperty("package");
        final Archetype archetype = (Archetype)wiz.getProperty("archetype");
        Runnable create = new Runnable() {
            public void run() {
                Set resultSet = new LinkedHashSet();
                dirF.getParentFile().mkdirs();
                
                
                runArchetype(dirF.getParentFile(), gr, art, ver, pack, archetype);
                
                // Always open top dir as a project:
                FileObject fDir = FileUtil.toFileObject(dirF);
                if (fDir != null) {
                    // the archetype generation didn't fail.
                    if (fIsPosted) {
                        try {
                            Project project = ProjectManager.getDefault().findProject(fDir);
                            if (project != null) {
                                resultSet.add(project);
                            }
                        } catch (IllegalArgumentException ex) {
                            ex.printStackTrace();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        resultSet.add(fDir);
                    }
                    // Look for nested projects to open as well:
                    Enumeration e = fDir.getFolders(true);
                    while (e.hasMoreElements()) {
                        FileObject subfolder = (FileObject) e.nextElement();
                        if (ProjectManager.getDefault().isProject(subfolder)) {
                            if (fIsPosted) {
                                try {
                                    Project project = ProjectManager.getDefault().findProject(subfolder);
                                    if (project != null) {
                                        resultSet.add(project);
                                    }
                                } catch (IllegalArgumentException ex) {
                                    ex.printStackTrace();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                resultSet.add(subfolder);
                            }
                        }
                    }
                }
                if (fIsPosted) {
                    final Project[] prjs = (Project[])resultSet.toArray(new Project[resultSet.size()]);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (prjs.length > 0) {
                                OpenProjects.getDefault().open(prjs, true);
                                OpenProjects.getDefault().setMainProject(prjs[0]);
                            }
                            FileObject temp = FileUtil.toFileObject(fTempFile.getParentFile());
                            try {
                                Project oldprj = ProjectManager.getDefault().findProject(temp);
                                if (oldprj != null) {
                                    OpenProjects.getDefault().close(new Project[] {oldprj});
                                }
                                fTempFile.delete();
                                fTempFile.getParentFile().delete();
                            } catch (IllegalArgumentException ex) {
                                ex.printStackTrace();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                }
            }
        };
        if (fIsPosted) {
            RequestProcessor.getDefault().post(create);
        } else {
            create.run();
        }
        return resultSet;
    }
    
    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps);
            }
        }
    }
    
    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty("projdir",null);
        this.wiz.putProperty("name",null);
        this.wiz = null;
        panels = null;
    }
    
    public String name() {
        return MessageFormat.format("{0} of {1}",
                new Object[] {new Integer(index + 1), new Integer(panels.length)});
    }
    
    public boolean hasNext() {
        return index < panels.length - 1;
    }
    
    public boolean hasPrevious() {
        return index > 0;
    }
    
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }
    
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }
    
    public WizardDescriptor.Panel current() {
        return panels[index];
    }
    
    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {}
    public final void removeChangeListener(ChangeListener l) {}

    private void runArchetype(File dirF, String gr, String art, String ver, String pack, Archetype arch) {
        BeanRunConfig config = new BeanRunConfig();
        config.setActiveteProfiles(Collections.EMPTY_LIST);
        config.setExecutionDirectory(dirF);
        config.setExecutionName("Project Creation");
        config.setGoals(Collections.singletonList("org.apache.maven.plugins:maven-archetype-plugin:1.0-alpha-4:create"));
        Properties props = new Properties();
        props.setProperty("archetypeArtifactId", arch.getArtifactId());
        props.setProperty("archetypeGroupId", arch.getGroupId());
        props.setProperty("archetypeVersion", arch.getVersion());
        props.setProperty("artifactId", art);
        props.setProperty("groupId", gr);
        props.setProperty("version", ver);
        if (pack != null && pack.trim().length() > 0) {
            props.setProperty("packageName", pack);
        }
        config.setProperties(props);
        // setup executor now..
        //hack - we need to setup the user.dir sys property..
        String oldUserdir = System.getProperty("user.dir");
        System.setProperty("user.dir", dirF.getAbsolutePath());
        try {
            MavenJavaExecutor exec = new MavenJavaExecutor(config);
            ExecutorTask task = ExecutionEngine.getDefault().execute("Maven", exec, exec.getInputOutput());
            //        RequestProcessor.getDefault().post();
            task.result();
        } finally {
            if (oldUserdir == null) {
                System.getProperties().remove("user.dir");
            } else {
                System.setProperty("user.dir", oldUserdir);
            }
        }
        
    }
    
    
}
