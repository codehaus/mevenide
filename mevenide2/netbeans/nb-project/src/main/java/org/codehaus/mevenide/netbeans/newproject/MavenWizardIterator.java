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

import org.codehaus.mevenide.netbeans.api.archetype.Archetype;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.api.execute.RunUtils;
import org.codehaus.mevenide.netbeans.execute.BeanRunConfig;
import org.codehaus.mevenide.netbeans.options.MavenCommandSettings;
import org.codehaus.mevenide.netbeans.spi.archetype.ArchetypeNGProjectCreator;
import org.codehaus.mevenide.netbeans.spi.archetype.NewProjectWizardExtender;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 *@author mkleint
 */
public class MavenWizardIterator implements WizardDescriptor.ProgressInstantiatingIterator {
    
    private static final long serialVersionUID = 1L;
    
    private static final String USER_DIR_PROP = "user.dir"; //NOI18N
    static final String PROPERTY_CUSTOM_CREATOR = "customCreator"; //NOI18N
    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    private transient WizardDescriptor.Panel[] basicPanels;
    private transient WizardDescriptor.Panel[] additionalPanels;
    private transient HashMap<WizardDescriptor.Panel, NewProjectWizardExtender> mapping;
    private transient WizardDescriptor wiz;
    private final List<ChangeListener> listeners;
    private Archetype selectedArchetype = null;
    
    public MavenWizardIterator() {
        listeners = new ArrayList<ChangeListener>();
        mapping = new HashMap<WizardDescriptor.Panel, NewProjectWizardExtender>();
    }
    
    public static MavenWizardIterator createIterator() {
        return new MavenWizardIterator();
    }

    private void checkSelectedArchetype() {
        final Archetype archetype = (Archetype)wiz.getProperty("archetype"); //NOI18N
        if (archetype != null && !archetype.equals(selectedArchetype)) {
            mapping.clear();
            List<WizardDescriptor.Panel> addit = new ArrayList<WizardDescriptor.Panel>();
            Collection<? extends NewProjectWizardExtender> res = Lookup.getDefault().lookupAll(NewProjectWizardExtender.class);
            for (NewProjectWizardExtender extender : res) {
                WizardDescriptor.FinishablePanel pnl = extender.createPanel(archetype);
                if (pnl != null) {
                    mapping.put(pnl, extender);
                    addit.add(pnl);
                }
            }
            additionalPanels = addit.toArray(new WizardDescriptor.Panel[addit.size()]);
            panels = new WizardDescriptor.Panel[basicPanels.length + additionalPanels.length];
            int i = 0;
            for (WizardDescriptor.Panel one : basicPanels) {
                panels[i] = one;
                i = i + 1;
            }
            i = basicPanels.length;
            for (WizardDescriptor.Panel one : additionalPanels) {
                panels[i] = one;
                i = i + 1;
            }
            selectedArchetype = archetype;
            updateSteps();
            fireChange();
        }
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
        assert false : "Cannot call this method if implements WizardDescriptor.ProgressInstantiatingIterator."; //NOI18N
        return null;
    }
    
    public Set instantiate(ProgressHandle handle) throws IOException {
        try {
            handle.start(4);
            handle.progress(1);
            final File dirF = FileUtil.normalizeFile((File) wiz.getProperty("projdir")); //NOI18N
            final File parent = dirF.getParentFile();
            if (parent != null && parent.exists()) {
                ProjectChooser.setProjectsFolder(parent);
            }
            
            Set resultSet = new LinkedHashSet();
            final String art = (String)wiz.getProperty("artifactId"); //NOI18N
            final String ver = (String)wiz.getProperty("version"); //NOI18N
            final String gr = (String)wiz.getProperty("groupId"); //NOI18N
            final String pack = (String)wiz.getProperty("package"); //NOI18N
            final Archetype archetype = (Archetype)wiz.getProperty("archetype"); //NOI18N<
            dirF.getParentFile().mkdirs();
            
            handle.progress(NbBundle.getMessage(MavenWizardIterator.class, "PRG_Processing_Archetype"), 2);
            ArchetypeNGProjectCreator customCreator = Lookup.getDefault().lookup(ArchetypeNGProjectCreator.class);
            if (customCreator != null) {
                customCreator.runArchetype(dirF.getParentFile(), wiz);
            } else {
                runArchetype(dirF.getParentFile(), gr, art, ver, pack, archetype);
            }
            handle.progress(3);
            // Always open top dir as a project:
            FileObject fDir = FileUtil.toFileObject(dirF);
            if (fDir != null) {
                // the archetype generation didn't fail.
                resultSet.add(fDir);
                addJavaRootFolders(fDir);
                // Look for nested projects to open as well:
                Enumeration e = fDir.getFolders(true);
                while (e.hasMoreElements()) {
                    FileObject subfolder = (FileObject) e.nextElement();
                    if (ProjectManager.getDefault().isProject(subfolder)) {
                        resultSet.add(subfolder);
                        addJavaRootFolders(subfolder);
                    }
                }
                Project prj = ProjectManager.getDefault().findProject(fDir);
                if (prj != null) {
                    for (WizardDescriptor.Panel pnl : mapping.keySet()) {
                        NewProjectWizardExtender ext = mapping.get(pnl);
                        resultSet.addAll(ext.instantiate(prj, wiz));
                    }
                }
                prj.getLookup().lookup(ProjectURLWatcher.class).triggerDependencyDownload();
            }
            return resultSet;
        } finally {
            handle.finish();
        }
    }
    
    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        ArchetypeNGProjectCreator customCreator = Lookup.getDefault().lookup(ArchetypeNGProjectCreator.class);
        if (customCreator != null) {
            wiz.putProperty(PROPERTY_CUSTOM_CREATOR, Boolean.TRUE);
        }
        index = 0;
        panels = createPanels();
        basicPanels = panels;
        updateSteps();
    }
    
    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty("projdir",null); //NOI18N
        this.wiz.putProperty("name",null); //NOI18N
        this.wiz = null;
        panels = null;
        basicPanels = null;
        additionalPanels = null;
        listeners.clear();
        mapping.clear();
    }
    
    public String name() {
        return MessageFormat.format(org.openide.util.NbBundle.getMessage(MavenWizardIterator.class, "NameFormat"),
                new Object[] {new Integer(index + 1), new Integer(panels.length)});
    }
    
    public boolean hasNext() {
        checkSelectedArchetype();
        return index < panels.length - 1;
    }
    
    public boolean hasPrevious() {
        checkSelectedArchetype();
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
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    private void fireChange() {
        synchronized (listeners) {
            for (ChangeListener list : listeners) {
                list.stateChanged(new ChangeEvent(this));
            }
        }
    }

    private int runArchetype(File dirF, String gr, String art, String ver, String pack, Archetype arch) {
        BeanRunConfig config = new BeanRunConfig();
        config.setActivatedProfiles(Collections.EMPTY_LIST);
        config.setExecutionDirectory(dirF);
        config.setExecutionName(NbBundle.getMessage(MavenWizardIterator.class, "RUN_Project_Creation"));
        config.setGoals(Collections.singletonList(MavenCommandSettings.getDefault().getCommand(MavenCommandSettings.COMMAND_CREATE_ARCHETYPE))); //NOI18N
        Properties props = new Properties();
        props.setProperty("archetypeArtifactId", arch.getArtifactId()); //NOI18N
        props.setProperty("archetypeGroupId", arch.getGroupId()); //NOI18N
        props.setProperty("archetypeVersion", arch.getVersion()); //NOI18N
        if (arch.getRepository() != null) {
            props.setProperty("remoteRepositories", arch.getRepository()); //NOI18N
        }
        props.setProperty("artifactId", art); //NOI18N
        props.setProperty("groupId", gr); //NOI18N
        props.setProperty("version", ver); //NOI18N
        if (pack != null && pack.trim().length() > 0) {
            props.setProperty("packageName", pack); //NOI18N
        }
        config.setProperties(props);
        config.setTaskDisplayName(NbBundle.getMessage(MavenWizardIterator.class, "RUN_Project_Creation"));
        // setup executor now..
        //hack - we need to setup the user.dir sys property..
        String oldUserdir = System.getProperty(USER_DIR_PROP); //NOI18N
        System.setProperty(USER_DIR_PROP, dirF.getAbsolutePath()); //NOI18N
        try {
            ExecutorTask task = RunUtils.executeMaven(config); //NOI18N
            return task.result();
        } finally {
            if (oldUserdir == null) {
                System.getProperties().remove(USER_DIR_PROP); //NOI18N
            } else {
                System.setProperty(USER_DIR_PROP, oldUserdir); //NOI18N
            }
        }
        
    }
    
    private void addJavaRootFolders(FileObject fo) {
        try {
            Project prj = ProjectManager.getDefault().findProject(fo);
            ProjectURLWatcher watch = prj.getLookup().lookup(ProjectURLWatcher.class);
            if (watch != null) {
                // do not create java/test for pom type projects.. most probably not relevant.
                if (! ProjectURLWatcher.TYPE_POM.equals(watch.getPackagingType())) {
                    URI mainJava = FileUtilities.convertStringToUri(watch.getMavenProject().getBuild().getSourceDirectory());
                    URI testJava = FileUtilities.convertStringToUri(watch.getMavenProject().getBuild().getTestSourceDirectory());
                    File file = new File(mainJava);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    file = new File(testJava);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void updateSteps() {
        // Make sure list of steps is accurate.
        String[] steps = new String[panels.length];
        String[] basicOnes = createSteps();
        System.arraycopy(basicOnes, 0, steps, 0, basicOnes.length);
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (i >= basicOnes.length || steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) {
                // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i)); //NOI18N
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps); //NOI18N
            }
        }
    }
    
}
