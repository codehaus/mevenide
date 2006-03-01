/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mkleint
 */
public final class ModelHandle {

    private Model model;
    private MavenProject project;
    private ActionToGoalMapping mapping;
    private Properties foAttributes;
    private List listeners;
    
    /** Creates a new instance of ModelHandle */
    ModelHandle(Model mdl, MavenProject proj, ActionToGoalMapping mapping, Properties foProperties) {
        model = mdl;
        project = proj;
        this.mapping = mapping;
        foAttributes = new Properties();
        foAttributes.putAll(foProperties);
        listeners = new ArrayList();
    }
    
    /**
     * action listeners are notified when the dialog is closed and values applied.
     */
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
    /**
     * action listeners are notified when the dialog is closed and values applied.
     */
    public void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }
    
    public Model getPOMModel() {
        return model;
    }
    
    public MavenProject getProject() {
        return project;
    }
    
    public ActionToGoalMapping getActionMappings() {
        return mapping;
    }

    public Properties getAttributes() {
        return foAttributes;
    }
    
    void fireActionPerformed() {
        Iterator it = listeners.iterator();
        ActionEvent evnt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "X");
        while (it.hasNext()) {
            ActionListener elem = (ActionListener) it.next();
            elem.actionPerformed(evnt);
        }
    }
    
}
