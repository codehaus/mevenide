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

import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.EntityMappingsMetadata;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceLocationProvider;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceScopeImplementation;
import org.netbeans.modules.j2ee.persistence.spi.support.EntityMappingsMetadataModelHelper;
import org.openide.filesystems.FileObject;

/**
 * Maven2 Implementation of 
 * <CODE>org.netbeans.modules.j2ee.persistence.spi.PersistenceScopeImplementation</CODE> 
 * @author Daniel Mohni
 */
public class PersistenceScopeImpl implements PersistenceScopeImplementation
{
    private PersistenceLocationProvider  locationProvider  = null;
    private PersistenceClasspathProviderImpl classpathProvider = null;
    private final EntityMappingsMetadataModelHelper modelHelper;
    private ClassPathProviderImpl cpProvider;
    
    
    /**
     * Creates a new instance of PersistenceScopeImpl
     * @param locProvider the PersistenceLocationProvider instance to use for lookups
     * @param cpProvider the PersistenceClasspathProvider instance to use for lookups
     */
    public PersistenceScopeImpl(PersistenceLocationProvider locProvider,
            PersistenceClasspathProviderImpl cpProv, ClassPathProviderImpl imp)
    {
        this.locationProvider  = locProvider;
        this.classpathProvider = cpProv;
        cpProvider = imp;
        modelHelper = createEntityMappingsHelper();
    }
    
    /**
     * property access to the project's persistence.xml
     * @return the persistence.xml file used in this project or null
     */
    public FileObject getPersistenceXml()
    {
        FileObject location = locationProvider.getLocation();
        if (location == null)
        {
            return null;
        }
        return location.getFileObject("persistence.xml"); // NOI18N
        
    }
    
    /**
     * property access to the persistence project classpath
     * @return the classpath provided by the PersistenceClasspathProvider
     */
    public ClassPath getClassPath()
    {
        return classpathProvider.getClassPath();
    }

    public MetadataModel<EntityMappingsMetadata> getEntityMappingsModel(String persistenceUnitName) {
        return modelHelper.getEntityMappingsModel(persistenceUnitName);
    }
    
    private EntityMappingsMetadataModelHelper createEntityMappingsHelper() {
        return EntityMappingsMetadataModelHelper.create(
            cpProvider.getProjectSourcesClassPath(ClassPath.BOOT),
            cpProvider.getProjectSourcesClassPath(ClassPath.COMPILE),
            cpProvider.getProjectSourcesClassPath(ClassPath.SOURCE));
    }
    
    
}
