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
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.WizardDescriptor;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 *@author mkleint
 */
public class ExistingWizardIterator implements WizardDescriptor.ProgressInstantiatingIterator {
    
    private static final long serialVersionUID = 1L;
    
    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    
    private ExistingWizardIterator() {}
    
    public static ExistingWizardIterator createIterator() {
        return new ExistingWizardIterator();
    }
    
    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[] {
            new UseOpenWizardPanel()
        };
    }
    
    private String[] createSteps() {
        return new String[] {
            NbBundle.getMessage(MavenWizardIterator.class, "LBL_UseOpenStep"),
        };
    }
    
    public Set/*<FileObject>*/ instantiate() throws IOException {
        assert false : "Cannot call this method if implements WizardDescriptor.ProgressInstantiatingIterator."; //NOI18N
        return null;
    }
    
    public Set instantiate(ProgressHandle handle) throws IOException {
        try {
            handle.start(2);
            handle.progress(1);
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    tryOpenProject();
                }
            });
            return Collections.EMPTY_SET;
        } finally {
            handle.finish();
            
        }
    }
    
    private void tryOpenProject() {
        final Action act = findAction("Actions/Project/org-netbeans-modules-project-ui-OpenProject.instance");
        if (act != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    act.actionPerformed(null);
                }
            });
        }
    }
    
    public static Action findAction( String key ) {
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource(key);
        
        if (fo != null && fo.isValid()) {
            try {
                DataObject dob = DataObject.find(fo);
                InstanceCookie ic = (InstanceCookie) dob.getCookie(InstanceCookie.class);
                
                if (ic != null) {
                    Object instance = ic.instanceCreate();
                    if (instance instanceof Action) {
                        Action a = (Action) instance;
                        return a;
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(ExistingWizardIterator.class.getName()).log(Level.WARNING, e.getMessage(), e);
                return null;
            }
        }
        return null;
    }
    
    
    public void initialize(WizardDescriptor wiz) {
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
    
}
