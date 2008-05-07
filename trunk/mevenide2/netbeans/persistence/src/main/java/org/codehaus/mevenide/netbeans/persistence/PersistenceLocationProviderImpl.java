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
package org.codehaus.mevenide.netbeans.persistence;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import org.codehaus.mevenide.netbeans.api.FileUtilities;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceLocationProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Maven2 Implementation of 
 * <CODE>org.netbeans.modules.j2ee.persistence.spi.PersistenceLocationProvider</CODE> 
 * also implements PropertyChangeListener to watch for changes on the persistence.xml file
 * @author Daniel Mohni
 */
public class PersistenceLocationProviderImpl implements PersistenceLocationProvider, PropertyChangeListener {

    static final String DEF_LOCATION = "src/main/resources/META-INF"; //NOI18N
    static final String DEF_PERSISTENCE = "src/main/resources/META-INF/persistence.xml"; //NOI18N
    static final String ALT_PERSISTENCE = "src/main/java/META-INF/persistence.xml"; //NOI18N
    private Project project = null;
    private FileObject location = null;
    private File projectDir = null;
    private File persistenceXml = null;

    /**
     * Creates a new instance of PersistenceLocationProviderImpl
     * @param proj reference to the NbMavenProject provider
     */
    public PersistenceLocationProviderImpl(Project proj) {
        project = proj;
        projectDir = FileUtil.toFile(proj.getProjectDirectory());
        persistenceXml = findPersistenceXml();
        location = FileUtil.toFileObject(persistenceXml.getParentFile());
    }

    /**
     * property access to the persistence location
     * @return FileObject representing the location (eg. parent folder) 
     * of the persistence.xml file
     */
    public FileObject getLocation() {
        return location;
    }

    /**
     * creates a new persistence location using the maven resource folder
     * -> /src/main/resources/META-INF
     * @return the newly created FileObject the location (eg. parent folder) 
     * of the persistence.xml file
     * @throws java.io.IOException if location can not be created
     */
    public FileObject createLocation() throws IOException {
        FileObject retVal = null;

        {
            File defaultLocation = FileUtilities.resolveFilePath(
                    projectDir, DEF_LOCATION);

            if (!defaultLocation.exists()) {
                retVal = FileUtil.createFolder(
                        project.getProjectDirectory(), DEF_LOCATION);
            }

            retVal = FileUtil.toFileObject(defaultLocation);

            location = retVal;
        }

        return retVal;
    }

    /**
     * Protected method used by MavenPersistenceSupport to create a file listener
     * @return property access to the current persistence.xml file
     */
    protected File getPersistenceXml() {
        return persistenceXml;
    }

    /**
     * called by constructor to check if there is a persistence.xml available,
     * it checks in /src/main/java/META-INF and /src/main/resources/META-INF
     * @return File object with the current persistence.xml or null
     */
    private File findPersistenceXml() {
        File retVal = null;

        // check if persistence.xml is in src/main/resources/META-INF
        File defaultLocation = FileUtilities.resolveFilePath(
                projectDir, DEF_PERSISTENCE);

        if (defaultLocation.exists()) {
            retVal = defaultLocation;
        } else {
            // check if persistence.xml is in src/main/java/META-INF
            File altLocation = FileUtilities.resolveFilePath(
                    projectDir, ALT_PERSISTENCE);
            if (altLocation.exists()) {
                retVal = altLocation;
            } else {
                retVal = defaultLocation;
            }
        }

        return retVal;
    }

    /**
     * watches for creation and deletion of the persistence.xml file
     * @param evt the change event to process
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (MavenPersistenceProvider.PROP_PERSISTENCE.equals(evt.getPropertyName())) {
            persistenceXml = findPersistenceXml();
            location = FileUtil.toFileObject(persistenceXml.getParentFile());
        }
    }
}
