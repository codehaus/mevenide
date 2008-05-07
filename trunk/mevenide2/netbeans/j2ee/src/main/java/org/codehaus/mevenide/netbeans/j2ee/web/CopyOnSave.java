/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/* ==========================================================================
 * Copyright 2005 Mevenide Team
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
package org.codehaus.mevenide.netbeans.j2ee.web;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.StringTokenizer;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.ErrorManager;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Jesse Glick, et al., Pavel Buzek
 * @author mkleint - copied and adjusted from netbeans.org web project until it gets rewritten there to
 *  be generic.
 */
public class CopyOnSave extends FileChangeAdapter implements PropertyChangeListener {

    private FileObject docBase = null;
    private Project project;
    private WebModuleProviderImpl provider;
    boolean active = false;
    private ProjectURLWatcher mavenproject;

    /** Creates a new instance of CopyOnSaveSupport */
    public CopyOnSave(Project prj, WebModuleProviderImpl prov) {
        project = prj;
        provider = prov;
        mavenproject = project.getLookup().lookup(ProjectURLWatcher.class);
    }

    private WebModule getWebModule() {
        return provider.findWebModule(project.getProjectDirectory());
    }

    private J2eeModule getJ2eeModule() {
        return provider.getJ2eeModule();
    }

    private boolean isInPlace() throws IOException {
        FileObject fo = getJ2eeModule().getContentDirectory();
        return fo != null && fo.equals(getWebModule().getDocumentBase());
    }

    public void initialize() throws FileStateInvalidException {
        smallinitialize();
        ProjectURLWatcher.addPropertyChangeListener(project, this);
        active = true;
    }

    public void cleanup() throws FileStateInvalidException {
        smallcleanup();
        ProjectURLWatcher.removePropertyChangeListener(project, this);
        active = false;
    }

    public void smallinitialize() throws FileStateInvalidException {
        docBase = getWebModule().getDocumentBase();
        if (docBase != null) {
            docBase.getFileSystem().addFileChangeListener(this);
        }
    }

    public void smallcleanup() throws FileStateInvalidException {
        if (docBase != null) {
            docBase.getFileSystem().removeFileChangeListener(this);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (ProjectURLWatcher.PROP_PROJECT.equals(evt.getPropertyName())) {
            try {
                //TODO reduce cleanup to cases where the actual directory locations change..
                if (active) {
                    smallcleanup();
                    smallinitialize();
                }
            } catch (org.openide.filesystems.FileStateInvalidException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
        }
    }

    /** Fired when a file is changed.
     * @param fe the event describing context where action has taken place
     */
    @Override
    public void fileChanged(FileEvent fe) {
        try {
            if (!isInPlace()) {
                handleCopyFileToDestDir(fe.getFile());
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
        try {
            if (!isInPlace()) {
                handleCopyFileToDestDir(fe.getFile());
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
    }

    @Override
    public void fileRenamed(FileRenameEvent fe) {
        try {
            if (isInPlace()) {
                return;
            }

            FileObject fo = fe.getFile();
            FileObject docBase = getWebModule().getDocumentBase();
            if (docBase != null && FileUtil.isParentOf(docBase, fo)) {
                // inside docbase
                handleCopyFileToDestDir(fo);
                FileObject parent = fo.getParent();
                String path;
                if (FileUtil.isParentOf(docBase, parent)) {
                    path = FileUtil.getRelativePath(docBase, fo.getParent()) +
                            "/" + fe.getName() + "." + fe.getExt(); //NOI18N
                } else {
                    path = fe.getName() + "." + fe.getExt(); //NOI18N
                }
                if (!isSynchronizationAppropriate(path)) {
                    return;
                }
                handleDeleteFileInDestDir(path);
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
    }

    @Override
    public void fileDeleted(FileEvent fe) {
        try {
            if (isInPlace()) {
                return;
            }
            FileObject fo = fe.getFile();
            FileObject ducumentBase = getWebModule().getDocumentBase();
            if (ducumentBase != null && FileUtil.isParentOf(ducumentBase, fo)) {
                // inside docbase
                String path = FileUtil.getRelativePath(ducumentBase, fo);
                if (!isSynchronizationAppropriate(path)) {
                    return;
                }
                handleDeleteFileInDestDir(path);
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
    }

    private boolean isSynchronizationAppropriate(String filePath) {
        if (filePath.startsWith("WEB-INF/classes")) { //NOI18N
            return false;
        }
        if (filePath.startsWith("WEB-INF/src")) { //NOI18N
            return false;
        }
        if (filePath.startsWith("WEB-INF/lib")) { //NOI18N
            return false;
        }
        return true;
    }

    private void handleDeleteFileInDestDir(String resourcePath) throws IOException {
        FileObject webBuildBase = getJ2eeModule().getContentDirectory();
        if (webBuildBase != null) {
            // project was built
            FileObject toDelete = webBuildBase.getFileObject(resourcePath);
            if (toDelete != null) {
                toDelete.delete();
            }
        }
    }

    /** Copies a content file to an appropriate  destination directory, 
     * if applicable and relevant.
     */
    private void handleCopyFileToDestDir(FileObject fo) throws IOException {
        if (!fo.isVirtual()) {
            FileObject documentBase = findAppropriateResourceRoots(fo);
            if (documentBase != null) {
                // inside docbase
                String path = FileUtil.getRelativePath(documentBase, fo);
                if (!isSynchronizationAppropriate(path)) {
                    return;
                }
                FileObject webBuildBase = getJ2eeModule().getContentDirectory();
                if (webBuildBase != null) {
                    // project was built
                    if (FileUtil.isParentOf(documentBase, webBuildBase) || FileUtil.isParentOf(webBuildBase, documentBase)) {
                        //cannot copy into self
                        return;
                    }
                    FileObject destFile = ensureDestinationFileExists(webBuildBase, path, fo.isFolder());
                    if (!fo.isFolder()) {
                        InputStream is = null;
                        OutputStream os = null;
                        FileLock fl = null;
                        try {
                            is = fo.getInputStream();
                            fl = destFile.lock();
                            os = destFile.getOutputStream(fl);
                            FileUtil.copy(is, os);
                        } finally {
                            if (is != null) {
                                is.close();
                            }
                            if (os != null) {
                                os.close();
                            }
                            if (fl != null) {
                                fl.releaseLock();
                            }
                        }
                    //System.out.println("copied + " + FileUtil.copy(fo.getInputStream(), destDir, fo.getName(), fo.getExt()));
                    }
                }
            }
        }
    }
    //#106522 make sure we also copy src/main/resource.. TODO for now ignore resource filtering or repackaging..
    private FileObject findAppropriateResourceRoots(FileObject child) {
        FileObject documentBase = getWebModule().getDocumentBase();
        if (documentBase != null && FileUtil.isParentOf(documentBase, child)) {
            return documentBase;
        }
        URI[] uris = mavenproject.getResources(false);
        for (URI uri : uris) {
            FileObject fo = FileUtil.toFileObject(new File(uri));
            if (fo != null && FileUtil.isParentOf(fo, child)) {
                return fo;
            }
        }
        return null;
    }

    /** Returns the destination (parent) directory needed to create file with relative path path under webBuilBase
     */
    private FileObject ensureDestinationFileExists(FileObject webBuildBase, String path, boolean isFolder) throws IOException {
        FileObject current = webBuildBase;
        StringTokenizer st = new StringTokenizer(path, "/"); //NOI18N
        while (st.hasMoreTokens()) {
            String pathItem = st.nextToken();
            FileObject newCurrent = current.getFileObject(pathItem);
            if (newCurrent == null) {
                // need to create it
                if (isFolder || st.hasMoreTokens()) {
                    // create a folder
                    newCurrent = FileUtil.createFolder(current, pathItem);
                } else {
                    newCurrent = FileUtil.createData(current, pathItem);
                }
            }
            current = newCurrent;
        }
        return current;
    }
}
