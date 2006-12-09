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
package org.codehaus.mevenide.netbeans.j2ee.persistence;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceClassPathProvider;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceLocationProvider;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceScopeImplementation;
import org.openide.filesystems.FileObject;

/**
 * Maven2 Implementation of 
 * <CODE>org.netbeans.modules.j2ee.persistence.spi.PersistenceScopeImplementation</CODE> 
 * @author Daniel Mohni
 */
public class PersistenceScopeImpl implements PersistenceScopeImplementation
{
    private PersistenceLocationProvider  locationProvider  = null;
    private PersistenceClassPathProvider classpathProvider = null;
    
    /**
     * Creates a new instance of PersistenceScopeImpl
     * @param locProvider the PersistenceLocationProvider instance to use for lookups
     * @param cpProvider the PersistenceClasspathProvider instance to use for lookups
     */
    public PersistenceScopeImpl(PersistenceLocationProvider locProvider,
            PersistenceClassPathProvider cpProvider)
    {
        this.locationProvider  = locProvider;
        this.classpathProvider = cpProvider;
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
    
}
