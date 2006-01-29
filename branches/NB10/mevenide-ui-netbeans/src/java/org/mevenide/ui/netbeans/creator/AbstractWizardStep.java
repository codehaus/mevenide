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
import java.util.EventListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.apache.maven.project.Project;
import org.openide.WizardDescriptor;
import org.openide.loaders.TemplateWizard;
import org.openide.util.HelpCtx;


/**
 * Abstract superclass for all wizard steps.
 * Implements the change listeners..
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public abstract class AbstractWizardStep implements WizardDescriptor.Panel, ProjectValidateObserver
{
    private EventListenerList listenerList = new EventListenerList();
    private boolean valid = true;
    
    private ProjectPanel panel;
    private TemplateWizard wizard = null;
    
    public AbstractWizardStep()
    {
    }
    
    /**
     * Registers ChangeListener to receive events.
     */
    public synchronized void addChangeListener(ChangeListener listener)
    {
        listenerList.add(ChangeListener.class, listener);
    }
    
    /** Removes ChangeListener from the list of listeners.
     *@param listener The listener to remove.
     */
    public synchronized void removeChangeListener(ChangeListener listener)
    {
        listenerList.remove(ChangeListener.class, listener);
    }
    
    /** 
     * Notifies all registered listeners about the event.
     */
    protected final void fireChangeListenerStateChanged()
    {
        EventListener[] listeners = listenerList.getListeners(ChangeListener.class);
        ChangeEvent e = new ChangeEvent(this);
        for (int i = listeners.length-1; i>=0; i-=1)
        {
            ((ChangeListener)listeners[i]).stateChanged(e);
        }
    }
    
    /**
     * from projectValidateObserver
     */
    public void resetValidState(boolean newvalidstate, String errorMessage)
    {
        valid = newvalidstate;
        fireChangeListenerStateChanged();
        if (wizard != null)
        {
            // a semi-hidden property of WizardDescriptor, will display an error message.
            wizard.putProperty("WizardPanel_errorMessage", errorMessage); //NOI18N
        }
    }
    
    /**
     * for subclasses to create an instance of the visual representative.
     * is assumed to be descendant of Component.
     */
    public abstract ProjectPanel createComponent();
    
    public Component getComponent()
    {
        if (panel == null)
        {
            panel = createComponent();
        }
        return (Component)panel;
    }
    
    public HelpCtx getHelp()
    {
        return new HelpCtx("org.mevenide.io.netbeans");
    }
    
    public boolean isValid()
    {
        // a semi-hidden property of WizardDescriptor, will display an error message.
        wizard.putProperty("WizardPanel_errorMessage", panel.getValidityMessage()); //NOI18N
        return panel.isInValidState();
    }
    
    public void readSettings(Object settings)
    {
        wizard = (TemplateWizard)settings;
        Project proj = (Project)wizard.getProperty(MavenProjectIterator.PROP_PROJECT);
        panel.setProject(proj);
    }
    
    public void storeSettings(Object settings)
    {
        wizard = (TemplateWizard)settings;
        Project proj = (Project)wizard.getProperty(MavenProjectIterator.PROP_PROJECT);
        panel.copyProject(proj);
    }
  
}
