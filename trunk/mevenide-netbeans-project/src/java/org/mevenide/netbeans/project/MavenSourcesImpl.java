/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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

package org.mevenide.netbeans.project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.Icon;
import javax.swing.event.ChangeEvent;

import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.MavenUtils;
import org.apache.maven.project.Build;
import org.apache.maven.project.Resource;
import org.apache.tools.ant.DirectoryScanner;
import org.mevenide.netbeans.project.nodes.DirScannerSubClass;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.spi.project.support.GenericSources;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;

import org.openide.util.RequestProcessor;

/**
 * Implementation of Sources interface for maven projects.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenSourcesImpl implements Sources {
    private static final Log logger = LogFactory.getLog(MavenSourcesImpl.class);
    public static final String TYPE_RESOURCES = "Resources"; //NOI18N
    public static final String TYPE_XDOCS = "XDocs"; //NOI18N
    public static final String NAME_PROJECTROOT = "ProjectRoot"; //NOI18N
    public static final String NAME_XDOCS = "XDocs"; //NOI18N
    public static final String NAME_SOURCE = "1SourceRoot"; //NOI18N
    public static final String NAME_TESTSOURCE = "2TestSourceRoot"; //NOI18N
    public static final String NAME_INTEGRATIONSOURCE = "4IntegrationSourceRoot"; //NOI18N
    public static final String NAME_ASPECTSOURCE = "3AspectSourceRoot"; //NOI18N
    
    private MavenProject project;
    private List listeners;
    
    private SourceGroup rootGroup;
    private Map javaGroup;
    private HashMap resGroup;
    private SourceGroup xdocsGroup;
    
    private Object LOCK = new Object();
    
    
    /** Creates a new instance of MavenSourcesImpl */
    public MavenSourcesImpl(MavenProject proj) {
        project = proj;
        listeners = new ArrayList();
        javaGroup = new TreeMap();
        resGroup = new HashMap();
        project.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                checkChanges(true);
            }
        });
    }
    
    private void checkChanges(boolean synchronous) {
        boolean changed = false;
        synchronized (LOCK) {
            try {
                FileObject folder = URLMapper.findFileObject(project.getSrcDirectory().toURL());
                changed = changed | checkJavaGroupCache(folder, NAME_SOURCE, "Sources");
                folder = URLMapper.findFileObject(project.getTestSrcDirectory().toURL());
                changed = changed | checkJavaGroupCache(folder, NAME_TESTSOURCE, "Test Sources");
                folder = URLMapper.findFileObject(project.getAspectsDirectory().toURL());
                changed = changed | checkJavaGroupCache(folder, NAME_ASPECTSOURCE, "Aspect Sources");
                folder = URLMapper.findFileObject(project.getIntegrationTestsDirectory().toURL());
                changed = changed | checkJavaGroupCache(folder, NAME_INTEGRATIONSOURCE, "Integration Test Sources");
            } catch (MalformedURLException exc) {
                logger.error("Malformed URL", exc);
                changed = false;
                // don't fire or one gets into a cycle here..
            }
        }
        if (changed) {
            if (synchronous) {
                fireChange();
            } else {
                RequestProcessor.getDefault().postRequest(new Runnable() {
                    public void run() {
                        fireChange();
                    }
                });
            }
        }
    }
    
    private void fireChange() {
        List currList;
        synchronized (listeners) {
            currList = new ArrayList(listeners);
        }
        Iterator it = currList.iterator();
        ChangeEvent event = new ChangeEvent(this);
        while (it.hasNext()) {
            ChangeListener list = (ChangeListener)it.next();
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
        if (Sources.TYPE_GENERIC.equals(str)) {
            return new SourceGroup[] { GenericSources.group(project, project.getProjectDirectory(), NAME_PROJECTROOT, "Project Root", null, null) };
        }
        if (JavaProjectConstants.SOURCES_TYPE_JAVA.equals(str)) {
            List toReturn = new ArrayList();
            synchronized (LOCK) {
                boolean changed = false;
                // don't fire event synchronously..
                checkChanges(false);
                toReturn.addAll(javaGroup.values());
            }
            SourceGroup[] grp = new SourceGroup[toReturn.size()];
            grp = (SourceGroup[])toReturn.toArray(grp);
            return grp;
        }
        if (TYPE_XDOCS.equals(str)) {
            return createXDocs();
        }
        if (TYPE_RESOURCES.equals(str)) {
            List toReturn = new ArrayList();
            Build build = project.getOriginalMavenProject().getBuild();
            if (build != null) {
                List resources = build.getResources();
                if (resources != null) {
                    Iterator it = resources.iterator();
                    int count = 0;
                    while (it.hasNext()) {
                        count = count + 1;
                        Resource res = (Resource)it.next();
                        String path = project.getPropertyResolver().resolveString(res.getDirectory());
                        FileObject folder = FileUtilities.findFolder(project.getProjectDirectory(),
                        path);
                        if (folder == null) {
                            // maybe we got a absolutepath in the basedir definition.
                            File fl = FileUtil.normalizeFile(new File(path));
                            folder = FileUtil.toFileObject(fl);
                        }
                        if (folder != null) {
                            toReturn.add(new ResourceGroup(project, folder, res, "Resource" + count + res.getDirectory(), res.getDirectory(), null, null));
                        }
                    }
                }
            }
            SourceGroup[] grp = new SourceGroup[toReturn.size()];
            grp = (SourceGroup[])toReturn.toArray(grp);
            return grp;
        }
        logger.warn("unknown source type=" + str);
        return new SourceGroup[0];
    }
    
    /**
     * consult the SourceGroup cache, return true if anything changed..
     */
    private boolean checkJavaGroupCache(FileObject root, String name, String displayName) {
        SourceGroup group = (SourceGroup)javaGroup.get(name);
        if (root == null && group != null) {
            javaGroup.remove(name);
            return true;
        }
        if (root == null) {
            return false;
        }
        boolean changed = false;
        if (group == null) {
            group = GenericSources.group(project, root, name, displayName, null, null);
            javaGroup.put(name, group);
            changed = true;
        } else {
            if (!group.getRootFolder().equals(root)) {
                group = GenericSources.group(project, root, name, displayName, null, null);
                javaGroup.put(name, group);
                changed = true;
            }
        }
        return changed;
    }
    
    private SourceGroup[] createXDocs() {
        String path = project.getPropertyResolver().getResolvedValue("maven.docs.src");
        if (path != null) {
            File docs = FileUtil.normalizeFile(new File(path));
            if (!docs.exists()) {
                File rootDir = FileUtil.toFile(project.getProjectDirectory());
                // attempt relative path now.. shall we?
                docs = FileUtil.normalizeFile(new File(rootDir, path));
                if (!docs.exists()) {
                    return new SourceGroup[0];
                }
            }
            FileObject dir = FileUtil.toFileObject(docs);
            return new SourceGroup[] { GenericSources.group(project, dir, NAME_XDOCS, "Documentation", null, null) };
        }
        return new SourceGroup[0];
    }
    
    public static final class ResourceGroup implements SourceGroup {
        
        private final FileObject rootFolder;
        private File rootFile;
        private final String name;
        private final String displayName;
        private final Icon icon;
        private final Icon openedIcon;
        private MavenProject project;
        private Resource resource;
        
        ResourceGroup(MavenProject p, FileObject rootFolder, Resource res, String name, String displayName,
        Icon icon, Icon openedIcon) {
            project = p;
            resource = res;
            this.rootFolder = rootFolder;
            rootFile = FileUtil.toFile(rootFolder);
            this.name = name;
            this.displayName = displayName;
            this.icon = icon;
            this.openedIcon = openedIcon;
        }
        
        public FileObject getRootFolder() {
            return rootFolder;
        }
        
        public File getRootFolderFile() {
            return rootFile;
        }
        
        public Resource getResource() {
            return resource;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public Icon getIcon(boolean opened) {
            return opened ? icon : openedIcon;
        }
        
        public boolean contains(FileObject file) throws IllegalArgumentException {
            logger.debug("Resourcegroup.contains()=" + file);
            if (file != rootFolder && !FileUtil.isParentOf(rootFolder, file)) {
                throw new IllegalArgumentException();
            }
            if (FileOwnerQuery.getOwner(file) != project) {
                return false;
            }
            File f = FileUtil.toFile(file);
            if (f != null) {
                // MIXED, UNKNOWN, and SHARABLE -> include it
                return (SharabilityQuery.getSharability(f) != SharabilityQuery.NOT_SHARABLE && 
                        DirScannerSubClass.checkIncluded(f, rootFile, resource));
            } else {
                // Not on disk, include it.
                return true;
            }
        }
        
        public void addPropertyChangeListener(PropertyChangeListener l) {
            // XXX should react to ProjectInformation changes
        }
        
        public void removePropertyChangeListener(PropertyChangeListener l) {
            // XXX
        }
        
    }
}
