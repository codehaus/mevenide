/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.netbeans.creator;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.mevenide.project.io.CarefulProjectMarshaller;
import org.mevenide.ui.netbeans.MavenProjectCookie;
import org.mevenide.util.DefaultProjectUnmarshaller;
import org.openide.WizardDescriptor.Panel;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;


/** A template wizard iterator (sequence of panels).
 * Used to fill in the second and subsequent panels in the New wizard.
 * Associate this to a template inside a layer using the
 * Sequence of Panels extra property.
 * Create one or more panels from template as needed too.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenProjectIterator implements TemplateWizard.Iterator
{
    private static Log logger = LogFactory.getLog(MavenProjectIterator.class);
    public static final String PROP_PROJECT = "mavenproject"; //NOI18N
    private static final long serialVersionUID = 1585757464637L;
    private static MavenProjectIterator singleton;
    
    private transient int index;
    private transient Panel[] panels;
    private transient TemplateWizard wiz;
    
    // You should define what panels you want to use here:
    
    public static MavenProjectIterator getDefault()
    {
        if (singleton == null)
        {
            singleton = new MavenProjectIterator();
        }
        return singleton;
    }
    
    protected Panel[] createPanels()
    {
        return new Panel[]
        {
            // Assuming you want to keep the default 2nd panel:
            new BasicsWizardStep(),
            new BuildWizardStep(),
            new DescWizardStep(), 
            new OrgWizardStep(),
            new ListsWizardStep()
            // Or you can supply your own replacement or additions.
            /* --> EDIT ME <--
            new MyPanel1 (),
            new MyPanel2 ()
             */
        };
    }
    
    // And the list of step names:
    
    protected String[] createSteps()
    {
        return new String[]
        {
            null, // for targetChooser(); take name from panel
            null,
            null,
            null,
            null
            /* --> EDIT ME <--
            NbBundle.getMessage(MavenProjectIterator.class, "LBL_step_1"),
            NbBundle.getMessage(MavenProjectIterator.class, "LBL_step_2")
             */
        };
    }
    
    
    public Set instantiate(TemplateWizard wiz) throws IOException/*, IllegalStateException*/
    {
        DataFolder targetFolder = wiz.getTargetFolder();
        DataObject template = wiz.getTemplate();
        logger.debug("template=" + template.getClass());
        // ALL templates translate to project.xml in the end.. at least for now..
        DataObject projObj = template.createFromTemplate(targetFolder, "project");
        MavenProjectCookie cook = (MavenProjectCookie)projObj.getCookie(MavenProjectCookie.class);
        Project proj = (Project)wiz.getProperty(PROP_PROJECT);
        
//        //TEMPORARY not even sure it belongs here, but it irritates me..
//        proj.setId(proj.getArtifactId());
        
        if (cook != null)
        {
            FileLock lock = null;
            try {
                lock = projObj.getPrimaryFile().lock();
                OutputStream stream = projObj.getPrimaryFile().getOutputStream(lock);
                Writer writer = new OutputStreamWriter(stream);
                CarefulProjectMarshaller marshaller = new CarefulProjectMarshaller();
                InputStream original = template.getPrimaryFile().getInputStream();
                marshaller.marshall(writer, proj, original);
            } catch (IOException exc)
            {
                logger.error("cannot write project", exc);
            } catch (Exception e2)
            {
                logger.error("cannot write project2", e2);
            } finally
            {
                if (lock != null) 
                {
                    lock.releaseLock();
                }
            }
        }
        logger.debug("dataObject=" + projObj.getClass());

        // now create the appropriate subfolders..
        try {
            createSubfolders(proj, projObj, wiz);
        } catch (Exception exc)
        {
            logger.error("Error creating subfolders", exc);
        }
        // Do something with the result, e.g. open it:
        OpenCookie open = (OpenCookie)projObj.getCookie(OpenCookie.class);
        if (open != null)
        {
            open.open();
        }
        // or more generically, simulate a double-click:
        /*
        Node n = result.getNodeDelegate();
        SystemAction action = n.getDefaultAction();
        ActionEvent event = new ActionEvent(n, ActionEvent.ACTION_PERFORMED, "");
        if (action != null) {
            ((ActionManager)Lookup.getDefault().lookup(ActionManager.class)).invokeAction(action, event);
        }
         */
        Set toReturn = new HashSet();
        toReturn.add(projObj);
        // TODO shall we add all the files?
        return toReturn;
    }
    
    private void createSubfolders(Project proj, DataObject projObj, TemplateWizard wiz) throws Exception
    {
            File file = FileUtil.toFile(projObj.getPrimaryFile());
            DataFolder parentFolder = wiz.getTargetFolder();
            Build build = proj.getBuild();
            if (build != null) 
            {
                if (build.getSourceDirectory() != null) 
                {
                    DataFolder srcFolder = DataFolder.create(parentFolder, build.getSourceDirectory());
                    createPackageStructure(srcFolder, proj.getPackage());
                }
                if (build.getUnitTestSourceDirectory() != null) {
                    DataFolder srcFolder = DataFolder.create(parentFolder, build.getUnitTestSourceDirectory());
                    createPackageStructure(srcFolder, proj.getPackage());
                }
                if (build.getAspectSourceDirectory() != null)
                {
                    DataFolder.create(parentFolder, build.getAspectSourceDirectory());
                }
                if (build.getIntegrationUnitTestSourceDirectory() != null)
                {
                    DataFolder.create(parentFolder, build.getIntegrationUnitTestSourceDirectory());
                }
            }        
    }

    private void createPackageStructure(DataFolder parentFolder, String pack) throws Exception
    {
        if (pack != null && pack.length() > 0)
        {
            DataFolder.create(parentFolder, pack.replace('.','/'));
        }
    }
    // --- The rest probably does not need to be touched. ---
    

    
    // You can keep a reference to the TemplateWizard which can
    // provide various kinds of useful information such as
    // the currently selected target name.
    // Also the panels will receive wiz as their "settings" object.
    public void initialize(TemplateWizard wiz)
    {
        this.wiz = wiz;
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++)
        {
            Component c = panels[i].getComponent();
            if (steps[i] == null)
            {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent)
            { // assume Swing components
                JComponent jc = (JComponent)c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i)); // NOI18N
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps); // NOI18N
            }
        }
        //TODO.. read some predefined project..
        Project project = null;
        try {
            InputStream str = wiz.getTemplate().getPrimaryFile().getInputStream();
            logger.debug("file=" + wiz.getTemplate().getPrimaryFile().getPackageNameExt('/','.'));
            DefaultProjectUnmarshaller mars = new DefaultProjectUnmarshaller();
//            BufferedReader read = new BufferedReader(new InputStreamReader(str));
//            String line = read.readLine();
//            while (line != null)
//            {
//                logger.debug("line=" + line);
//                line = read.readLine();
//            }
            project = mars.parse(new InputStreamReader(str));
        } catch (FileNotFoundException exc) {
            logger.error("file not found", exc);
            project = new Project();
            project.setPomVersion("3");
        } catch (Exception exc2)
        {
            logger.error("cannot read project", exc2);
            project = new Project();
            project.setPomVersion("3");
        }
        wiz.putProperty(PROP_PROJECT, project);
    }
    public void uninitialize(TemplateWizard wiz)
    {
        this.wiz = null;
        panels = null;
    }
    
    // --- WizardDescriptor.Iterator METHODS: ---
    // Note that this is very similar to WizardDescriptor.Iterator, but with a
    // few more options for customization. If you e.g. want to make panels appear
    // or disappear dynamically, go ahead.
    
    public String name()
    {
        return NbBundle.getMessage(MavenProjectIterator.class, "TITLE_x_of_y",
        new Integer(index + 1), new Integer(panels.length));
    }
    
    public boolean hasNext()
    {
        return index < panels.length - 1;
    }
    public boolean hasPrevious()
    {
        return index > 0;
    }
    public void nextPanel()
    {
        if (!hasNext()) throw new NoSuchElementException();
        index++;
    }
    public void previousPanel()
    {
        if (!hasPrevious()) throw new NoSuchElementException();
        index--;
    }
    public Panel current()
    {
        return panels[index];
    }
    
    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l)
    {}
    public final void removeChangeListener(ChangeListener l)
    {}
    // If something changes dynamically (besides moving between panels),
    // e.g. the number of panels changes in response to user input, then
    // uncomment the following and call when needed:
    // fireChangeEvent();
    /*
    private transient Set listeners = new HashSet(1); // Set<ChangeListener>
    public final void addChangeListener(ChangeListener l) {
        synchronized(listeners) {
            listeners.add(l);
        }
    }
    public final void removeChangeListener(ChangeListener l) {
        synchronized(listeners) {
            listeners.remove(l);
        }
    }
    protected final void fireChangeEvent() {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(ev);
        }
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        listeners = new HashSet(1);
    }
     */
    
}
