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

import java.awt.Component;
import java.util.ArrayList;
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
