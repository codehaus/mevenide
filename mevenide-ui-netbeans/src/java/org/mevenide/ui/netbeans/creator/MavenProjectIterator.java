/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

package org.mevenide.ui.netbeans.creator;

import java.io.File;

import org.apache.maven.project.Build;

import org.mevenide.project.io.DefaultProjectMarshaller;
import org.mevenide.util.DefaultProjectUnmarshaller;

import java.io.FileNotFoundException;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.mevenide.project.io.CarefulProjectMarshaller;
import org.mevenide.ui.netbeans.MavenProjectCookie;
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
                    DataFolder.create(parentFolder, build.getSourceDirectory());
                }
                if (build.getUnitTestSourceDirectory() != null) {
                    DataFolder.create(parentFolder, build.getUnitTestSourceDirectory());
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
