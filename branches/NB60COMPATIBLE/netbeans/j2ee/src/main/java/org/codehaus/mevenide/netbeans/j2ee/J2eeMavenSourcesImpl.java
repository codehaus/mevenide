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

package org.codehaus.mevenide.netbeans.j2ee;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.GenericSources;
import org.openide.filesystems.FileObject;

import org.openide.util.RequestProcessor;

/**
 * Implementation of Sources interface for maven projects in the j2ee area
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class J2eeMavenSourcesImpl implements Sources {
    
    public static final String TYPE_DOC_ROOT="doc_root"; //NOI18N
    public static final String TYPE_WEB_INF="web_inf"; //NOI18N
    
    private NbMavenProject project;
    private List<ChangeListener> listeners;
    
    private SourceGroup webDocSrcGroup;
    
    private Object lock = new Object();
    
    
    /** Creates a new instance of MavenSourcesImpl */
    public J2eeMavenSourcesImpl(NbMavenProject proj) {
        project = proj;
        listeners = new ArrayList<ChangeListener>();
        project.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (NbMavenProject.PROP_PROJECT.equals(event.getPropertyName())) {
                    checkChanges(true);
                }
            }
        });
    }
    
    private void checkChanges(boolean synchronous) {
        boolean changed = false;
        synchronized (lock) {
            MavenProject mp = project.getOriginalMavenProject();
            FileObject fo = null;
            if (mp != null) {
                 fo = FileUtilities.convertURItoFileObject(project.getWebAppDirectory());
            }
            changed = checkWebDocGroupCache(fo);
        }
        if (changed) {
            if (synchronous) {
                fireChange();
            } else {
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        fireChange();
                    }
                });
            }
        }
    }
    
    private void fireChange() {
        List<ChangeListener> currList;
        synchronized (listeners) {
            currList = new ArrayList<ChangeListener>(listeners);
        }
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener list : currList) {
            list.stateChanged(event);
        }
    }
    
    public void addChangeListener(ChangeListener changeListener) {
        synchronized (listeners) {
            listeners.add(changeListener);
        }
    }
    
    public void removeChangeListener(ChangeListener changeListener) {
        synchronized (listeners) {
            listeners.remove(changeListener);
        }
    }
    
    public SourceGroup[] getSourceGroups(String str) {
        if (TYPE_DOC_ROOT.equals(str)) {
            return createWebDocRoot();
        }
        return new SourceGroup[0];
    }
    
    
    private SourceGroup[] createWebDocRoot() {
        FileObject folder = FileUtilities.convertURItoFileObject(project.getWebAppDirectory());
        SourceGroup grp = null;
        synchronized (lock) {
            checkWebDocGroupCache(folder);
            grp = webDocSrcGroup;
        }
        if (grp != null) {
            return new SourceGroup[] {grp};
        } else {
            return new SourceGroup[0];
        }
    }
    
    
    
    /**
     * consult the SourceGroup cache, return true if anything changed..
     */
    private boolean checkWebDocGroupCache(FileObject root) {
        if (root == null && webDocSrcGroup != null) {
            webDocSrcGroup = null;
            return true;
        }
        if (root == null) {
            return false;
        }
        boolean changed = false;
        if (webDocSrcGroup == null || !webDocSrcGroup.getRootFolder().equals(root)) {
            webDocSrcGroup = GenericSources.group(project, root, TYPE_DOC_ROOT, "Web Pages", null, null);
            changed = true;
        }
        return changed;
    }
    
}
