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

package org.mevenide.netbeans.project.writer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.mevenide.context.IProjectContext;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.customizer.MavenPOMChange;
import org.mevenide.netbeans.project.customizer.MavenPOMTreeChange;
import org.mevenide.netbeans.project.customizer.MavenPropertyChange;
import org.mevenide.project.io.CarefulProjectMarshaller;
import org.mevenide.project.io.ElementContentProvider;
import org.mevenide.project.io.IContentProvider;
import org.mevenide.properties.Comment;
import org.mevenide.properties.Element;
import org.mevenide.properties.ElementFactory;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.properties.KeyValuePair;
import org.mevenide.properties.PropertyModel;
import org.mevenide.properties.PropertyModelFactory;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.UserQuestionException;

/**
 * utility class to write project (POM + properties files).
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class NbProjectWriter implements FileSystem.AtomicAction {
    private MavenProject project;
    private List changes;
    /** Creates a new instance of NbProjectWriter */
    public NbProjectWriter(MavenProject proj) {
        project = proj;
    }

    public void applyChanges(List changes) throws Exception {
        FileSystem fs = project.getProjectDirectory().getFileSystem();
        this.changes = changes;
        fs.runAtomicAction(this);
    }
    
    public void run () throws IOException {
        HashMap locFileToModelMap = new HashMap();
        HashMap locFileToLockMap = new HashMap();
        Iterator it = changes.iterator();
        boolean checkDependencies = false;
        String newArtifact = null;
        String newGroup = null;
        String newVersion = null;
        String oldArtifact = null;
        String oldGroup = null;
        String oldVersion = null;
        try {
            List pomChanges = new ArrayList();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof MavenPropertyChange) {
                    MavenPropertyChange change = (MavenPropertyChange)obj;
                    processPropertyChange(change, locFileToLockMap, locFileToModelMap);
                }
                if (obj instanceof MavenPOMChange || obj instanceof MavenPOMTreeChange) {
                    pomChanges.add(obj);
                    if (obj instanceof MavenPOMChange) {
                        MavenPOMChange chan = (MavenPOMChange)obj;
                        if ("pom.artifactId".equals(chan.getPath()) &&
                            !chan.getOldValue().equals(chan.getNewValue())) 
                        {
                            newArtifact = chan.getNewValue();
                            checkDependencies = true;
                        } 
                        if ("pom.groupId".equals(chan.getPath()) &&
                            !chan.getOldValue().equals(chan.getNewValue())) 
                        {
                            newGroup = chan.getNewValue();
                            checkDependencies = true;
                        } 
                        if ("pom.currentVersion".equals(chan.getPath()) &&
                            !chan.getOldValue().equals(chan.getNewValue())) 
                        {
                            newVersion = chan.getNewValue();
                            checkDependencies = true;
                        } 
                    }
                }
            }
            if (checkDependencies) {
                oldArtifact = project.getOriginalMavenProject().getArtifactId();
                oldGroup = project.getOriginalMavenProject().getGroupId();
                oldVersion = project.getOriginalMavenProject().getCurrentVersion();
                newVersion = newVersion != null ? newVersion : oldVersion;
                newGroup = newGroup != null ? newGroup : oldGroup;
                newArtifact = newArtifact != null ? newArtifact : oldArtifact;
            }
            // now write the POM files..
            writePOMs(pomChanges, locFileToLockMap);
            
        // now write the models..
            it = locFileToModelMap.keySet().iterator();
            while (it.hasNext()) {
                File file = (File)it.next();
                PropertyModel model = (PropertyModel)locFileToModelMap.get(file);
                FileLock lock = (FileLock)locFileToLockMap.get(file);
                FileObject fo = FileUtil.toFileObject(file);
                model.store(fo.getOutputStream(lock));
            }
        } catch (IOException exc) {
            throw exc;
        } catch (Exception exc2) {
            IOException newone = new IOException("Error while writing project files.");
            ErrorManager.getDefault().annotate(newone, exc2);
            throw newone;
        } finally {
            // release the locks
            Iterator locIt = locFileToLockMap.values().iterator();
            while (locIt.hasNext()) {
                FileLock lock = (FileLock)locIt.next();
                lock.releaseLock();
            }
            if (checkDependencies) {
                try {
                    DependencyUpdater.checkOpenedProjects(oldArtifact, oldGroup, oldVersion, newArtifact, newGroup, newVersion);
                } catch (IOException io) {
                    throw io;
                } catch (Exception exc2) {
                    IOException newone = new IOException("Error while writing project files.");
                    ErrorManager.getDefault().annotate(newone, exc2);
                    throw newone;
                }
            }
        }
    }
    
    private PropertyModel createPropertyModel(File location, HashMap locks, HashMap models) throws IOException {
        FileObject fo = null;
        if (!location.exists()) {
            File parent = location.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            FileObject parentFO = FileUtil.toFileObject(parent);
            if (parentFO == null) {//parent fo not exists
                throw new IOException("Cannot create directory where to create the property file.");
            }
            fo = parentFO.createData(location.getName());
        } else {
            fo = FileUtil.toFileObject(location);
        }
        if (fo == null) {
            throw new IOException("");
        }
        try {
            PropertyModel model = PropertyModelFactory.getFactory().newPropertyModel(fo.getInputStream());
            FileLock lock = fo.lock();
            locks.put(location, lock);
            models.put(location, model);
            return model;
        } catch (UserQuestionException exc) {
            throw new IOException("Cannot obtain lock. User interaction required.");
        }
    }
    
    private int findBestPlacement(PropertyModel model, String key) {
        if (key.startsWith("maven.")) { //NOI18N
            int secondDot = key.indexOf(".", 6); //NOI18N
            if (secondDot > 0) {
                String keyGroup = key.substring(0, secondDot);
                Iterator it = model.getList().iterator();
                int index = -1;
                while (it.hasNext()) {
                    index = index + 1;
                    Element el = (Element)it.next();
                    if (el instanceof KeyValuePair) {
                        KeyValuePair pair = (KeyValuePair)el;
                        if (pair.getKey().startsWith(keyGroup)) {
                            return index;
                        }
                    }
                }
            }
        }
        return -1;
    }
    
    private void processPropertyChange(MavenPropertyChange change, HashMap locFileToLockMap, HashMap locFileToModelMap) throws IOException {
        if      (change.getOldLocation() != IPropertyLocator.LOCATION_DEFAULTS &&
                 change.getOldLocation() != IPropertyLocator.LOCATION_NOT_DEFINED) {
            File oldLoc = FileUtilities.locationToFile(change.getOldLocation(), project);
            PropertyModel model = (PropertyModel)locFileToModelMap.get(oldLoc);
            if (model == null) {
                model = createPropertyModel(oldLoc, locFileToLockMap, locFileToModelMap);
            }
            if (change.getOldLocation() != change.getNewLocation()) {
                // remove from old..
                KeyValuePair oldpair = model.findByKey(change.getKey());
                if (oldpair != null) {
                    model.removeElement(oldpair);
                }
            }
        }
        if      (change.getNewLocation() != IPropertyLocator.LOCATION_DEFAULTS &&
                 change.getNewLocation() != IPropertyLocator.LOCATION_NOT_DEFINED) {
            File newLoc = FileUtilities.locationToFile(change.getNewLocation(), project);
            PropertyModel model = (PropertyModel)locFileToModelMap.get(newLoc);
            if (model == null) {
                model = createPropertyModel(newLoc, locFileToLockMap, locFileToModelMap);
            }
            KeyValuePair pair = model.findByKey(change.getKey());
            if (pair != null) {
                // oh boy, is's also herecomment
                if (change.getOldLocation() == change.getNewLocation()) {
                    pair.setValue(change.getNewValue());
                } else {
                    // // oh boy
                    String commentStr = "#" + pair.getKey() + "=" + pair.getValue();
                    int index = model.getList().indexOf(pair);
                    Comment comment = ElementFactory.getFactory().createComment();
                    comment.setComment(commentStr);
                    model.insertAt(index, comment);
                    pair.setValue(change.getNewValue());
                }
            } else {
                // ok, add it here..
                pair = ElementFactory.getFactory().createKeyValuePair(change.getKey(), '=');
                pair.setValue(change.getNewValue());
                int index = findBestPlacement(model, change.getKey());
                if (index > 0) {
                    model.insertAt(index, pair);
                } else {
                    model.addElement(pair);
                }
            }
        }
    }
    
    private void writePOMs(List changes, HashMap fileLockMap) throws Exception {
        if (changes.size() > 0) {
            IProjectContext context = project.getContext().getPOMContext();
            org.jdom.Element[] roots = context.getRootElementLayers();
            File[] files = context.getProjectFiles();
            //write now
            Writer writer = null;
            InputStream stream = null;
            try {
                for (int i = 0; i < files.length; i++) {
                    IContentProvider provider = new ChangesContentProvider(new ElementContentProvider(roots[i]),
                                                                           changes, "pom", i);
                    CarefulProjectMarshaller marshall = new CarefulProjectMarshaller(figureOutFormat(roots[i]));
                    FileObject fo = FileUtil.toFileObject(files[i]);
                    // read the current stream first..
                    stream = fo.getInputStream();
                    SAXBuilder builder = new SAXBuilder();
                    Document originalDoc = builder.build(stream);
                    stream.close();
                    FileLock lock = fo.lock();
                    fileLockMap.put(files[i], lock);
                    writer = new OutputStreamWriter(fo.getOutputStream(lock));
                    marshall.marshall(writer, provider, originalDoc);
                }
            } catch (UserQuestionException exc) {
                throw new IOException("Cannot obtain lock. User interaction required.");
            }
            finally {
                if (writer != null) {
                    writer.close();
                }
                if (stream != null) {
                    stream.close();
                }
            }
        }
    }
    
    static Format figureOutFormat(org.jdom.Element root) {
        Format toRet = Format.getPrettyFormat();
        List content = root.getContent();
        String lineSep = System.getProperty("line.separator");
        String indent = "    ";
        if (content.size() > 2) {
            Content cont1 = (Content)content.get(0);
            Content cont2 = (Content)content.get(1);
            if (cont1 instanceof Text && cont2 instanceof org.jdom.Element) {
                String line = cont1.getValue();
                if (line.indexOf("\r\n") > -1) {
                    lineSep = "\r\n";
                }
                int index = line.indexOf(lineSep);
                if (index > -1 && line.length()  + lineSep.length() > index) {
                    String newLine = line.substring(index + lineSep.length());
                    if (newLine.matches("[ ]{" + newLine.length() + "}")) {
                        indent = newLine;
                    }
                }
            }
        }
        toRet.setIndent(indent);
        toRet.setLineSeparator(lineSep);
        return toRet;
    }
}
