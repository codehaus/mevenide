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

package org.netbeans.modules.maven.gsf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.maven.project.MavenProject;
import org.netbeans.modules.maven.api.FileUtilities;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.GenericSources;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

import org.openide.util.RequestProcessor;

/**
 * Implementation of Sources interface for maven projects in the gsf area
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class GsfMavenSourcesImpl implements Sources {
    
    public static final String TYPE_GROOVY="groovy"; //NOI18N
    public static final String TYPE_SCALA="scala"; //NOI18N
    
    public static final String NAME_GROOVYSOURCE = "81GroovySourceRoot"; //NOI18N
    public static final String NAME_GROOVYTESTSOURCE = "82GroovyTestSourceRoot"; //NOI18N
    public static final String NAME_SCALASOURCE = "91ScalaSourceRoot"; //NOI18N
    public static final String NAME_SCALATESTSOURCE = "92ScalaTestSourceRoot"; //NOI18N
    
    private final List<ChangeListener> listeners;
    
    private Map<String, SourceGroup> groovyGroup;
    private Map<String, SourceGroup> scalaGroup;
    
    private final Object lock = new Object();
    private CPProvider cpprovider;
    private Project project;
    
    
    /** Creates a new instance of MavenSourcesImpl */
    GsfMavenSourcesImpl(CPProvider cpp, Project prj) {
        project = prj;
        cpprovider = cpp;
        listeners = new ArrayList<ChangeListener>();
        groovyGroup = new TreeMap<String, SourceGroup>();
        scalaGroup = new TreeMap<String, SourceGroup>();
        NbMavenProject.addPropertyChangeListener(project, new PropertyChangeListener() {
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
            FileObject folder = FileUtilities.convertURItoFileObject(cpprovider.getGroovyDirectory(false));
            changed = changed | checkGroupCache(folder, NAME_GROOVYSOURCE, NbBundle.getMessage(GsfMavenSourcesImpl.class, "SG_GroovySources"), groovyGroup);
            folder = FileUtilities.convertURItoFileObject(cpprovider.getGroovyDirectory(true));
            changed = changed | checkGroupCache(folder, NAME_GROOVYTESTSOURCE, NbBundle.getMessage(GsfMavenSourcesImpl.class, "SG_Test_GroovySources"), groovyGroup);
            folder = FileUtilities.convertURItoFileObject(cpprovider.getScalaDirectory(false));
            changed = changed | checkGroupCache(folder, NAME_SCALASOURCE, NbBundle.getMessage(GsfMavenSourcesImpl.class, "SG_ScalaSources"), scalaGroup);
            folder = FileUtilities.convertURItoFileObject(cpprovider.getScalaDirectory(true));
            changed = changed | checkGroupCache(folder, NAME_SCALATESTSOURCE, NbBundle.getMessage(GsfMavenSourcesImpl.class, "SG_Test_ScalaSources"), scalaGroup);
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
        if (TYPE_GROOVY.equals(str)) {
            List<SourceGroup> toReturn = new ArrayList<SourceGroup>();
            synchronized (lock) {
                // don't fire event synchronously..
                checkChanges(false);
                toReturn.addAll(groovyGroup.values());
            }
            SourceGroup[] grp = new SourceGroup[toReturn.size()];
            grp = toReturn.toArray(grp);
            return grp;
        }
        if (TYPE_SCALA.equals(str)) {
            List<SourceGroup> toReturn = new ArrayList<SourceGroup>();
            synchronized (lock) {
                // don't fire event synchronously..
                checkChanges(false);
                toReturn.addAll(scalaGroup.values());
            }
            SourceGroup[] grp = new SourceGroup[toReturn.size()];
            grp = toReturn.toArray(grp);
            return grp;
        }
        return new SourceGroup[0];
    }

    /**
     * consult the SourceGroup cache, return true if anything changed..
     */
    private boolean checkGroupCache(FileObject root, String name, String displayName, Map<String, SourceGroup> groups) {
        SourceGroup group = groups.get(name);
        if (root == null && group != null) {
            groups.remove(name);
            return true;
        }
        if (root == null) {
            return false;
        }
        boolean changed = false;
        if (group == null) {
            group = GenericSources.group(project, root, name, displayName, null, null);
            groups.put(name, group);
            changed = true;
        } else {
            if (!group.getRootFolder().equals(root)) {
                group = GenericSources.group(project, root, name, displayName, null, null);
                groups.put(name, group);
                changed = true;
            }
        }
        return changed;
    }

    
}
