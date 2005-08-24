/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.ui.eclipse.properties.pages;

import org.apache.plexus.util.StringInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.preferences.pages.LocationPreferencePage;

/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class LocationPropertyPage extends LocationPreferencePage implements IWorkbenchPropertyPage {
    private IAdaptable element;

    public IAdaptable getElement() {
        return element;
    }

    public void setElement(IAdaptable element) {
        this.element = element;
    }
    
    private PreferenceStore store;
    protected PreferenceStore getStore() {
        try {
            if ( store == null ) {
                IProject project = (IProject) this.element.getAdapter(IProject.class);
                IFile props = project.getFile(new Path(".settings/org.mevenide.ui.prefs"));
                if ( !props.exists() ) {
                    IFolder folder = project.getFolder(new Path(".settings"));
                    if ( !folder.exists() ) {
                        folder.create(true, true, null);
                    }
                    props.create(new StringInputStream(""), true, null);
                }
                store = new PreferenceStore(props.getLocation().toOSString());
                store.load();
            }
            return store;
        }
        catch ( Exception e ) {
            String msg = "Unable to load property store"; 
            Mevenide.getInstance().getLog().log(new Status(1, Mevenide.PLUGIN_ID, 1, msg, e));
            throw new RuntimeException(msg, e);
        }
    }
    
    protected boolean isPropertyPage() {
        return true;
    }
}
