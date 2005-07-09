/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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

package org.mevenide.ui.eclipse.template.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.maven.project.Project;
import org.mevenide.context.JDomProjectUnmarshaller;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * A Templates object is a container for Templates
 * 
 * @author <a href="mailto:jens@iostream.net">Jens Andersen </a>, Last updated
 *         by $Author$
 * @version $Id$
 */
public class Templates extends Observable {

    private List fTemplates;

    public Templates() {
        fTemplates = new ArrayList();
    }

    /**
     * Add a template to the Templates container
     * 
     * @param template - template to be added
     */
    public void addTemplate(Template template) {
        fTemplates.add(template);
        setChanged();
        notifyObservers();
    }

    /**
     * Remove a template from the Templates container
     * 
     * @param template - template to be removed
     */
    public void removeTemplate(Template template) {
        fTemplates.remove(template);
        setChanged();
        notifyObservers();
    }

    /**
     * Get all templates as an array
     * 
     * @return a template array
     */
    public Object[] getTemplates() {
        return fTemplates.toArray();
    }

    public static Templates newTemplates() {
        Templates templates = new Templates();
        File tmplFolder = Mevenide.getInstance().getStateLocation().append("templates").toFile();//$NON-NLS-1$
        if (tmplFolder.exists()) {
            JDomProjectUnmarshaller unmarshaller = new JDomProjectUnmarshaller();

            File[] file = tmplFolder.listFiles();
            for (int i = 0; i < file.length; i++) {
                try {
                    if (file[i].getName().endsWith("tmpl")) { //$NON-NLS-1$
                        Project pom = unmarshaller.parse(file[i]);
                        templates.addTemplate(new Template(pom));
                    }
                }
                catch (Exception e) {
                }
            }
        }
        return templates;
    }
}