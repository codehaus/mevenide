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

package org.mevenide.netbeans.project.writer;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.customizer.MavenPropertyChange;
import org.mevenide.properties.Comment;
import org.mevenide.properties.Element;
import org.mevenide.properties.ElementFactory;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.properties.KeyValuePair;
import org.mevenide.properties.PropertyModel;
import org.mevenide.properties.PropertyModelFactory;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.UserQuestionException;

/**
 * utility class to write project (POM + properties files).
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class NbProjectWriter {
    private MavenProject project;
    /** Creates a new instance of NbProjectWriter */
    public NbProjectWriter(MavenProject proj) {
        project = proj;
    }
    
    public void applyChanges(List changes) throws Exception {
        HashMap locFileToModelMap = new HashMap();
        HashMap locFileToLockMap = new HashMap();
        Iterator it = changes.iterator();
        try {
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof MavenPropertyChange) {
                    MavenPropertyChange change = (MavenPropertyChange)obj;
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
            }
        // now write the models..
            it = locFileToModelMap.keySet().iterator();
            while (it.hasNext()) {
                File file = (File)it.next();
                PropertyModel model = (PropertyModel)locFileToModelMap.get(file);
                FileLock lock = (FileLock)locFileToLockMap.get(file);
                FileObject fo = FileUtil.toFileObject(file);
                model.store(fo.getOutputStream(lock));
            }
        
        } finally {
            // release the locks
            Iterator locIt = locFileToLockMap.values().iterator();
            while (locIt.hasNext()) {
                FileLock lock = (FileLock)locIt.next();
                if (lock.isValid()) {
                    lock.releaseLock();
                }
            }
        }
    }
    
    private PropertyModel createPropertyModel(File location, HashMap locks, HashMap models) throws Exception {
        FileObject fo = null;
        if (!location.exists()) {
            File parent = location.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            FileObject parentFO = FileUtil.toFileObject(parent);
            if (parentFO == null) {//parent fo not exists
                throw new IOException("Cannot create directory where to create the propety file.");
            }
            fo = parentFO.createData(location.getName());
        } else {
            fo = FileUtil.toFileObject(location);
        }
        if (fo == null) {
            throw new IOException("");
        }
        try {
            FileLock lock = fo.lock();
            locks.put(location, lock);
            PropertyModel model = PropertyModelFactory.getFactory().newPropertyModel(fo.getInputStream());
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
    
}