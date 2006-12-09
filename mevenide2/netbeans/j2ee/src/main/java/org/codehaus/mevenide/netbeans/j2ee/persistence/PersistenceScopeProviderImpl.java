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

import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.persistence.api.PersistenceScope;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceClassPathProvider;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceLocationProvider;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceScopeFactory;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceScopeImplementation;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceScopeProvider;
import org.openide.filesystems.FileObject;


/**
 * Maven2 Implementation of 
 * <CODE>org.netbeans.modules.j2ee.persistence.spi.PersistenceScopeProvider</CODE> 
 * @author Daniel Mohni
 */
public class PersistenceScopeProviderImpl implements PersistenceScopeProvider 
{
    
    private PersistenceScopeImplementation persistenceScopeImpl = null;
    private PersistenceScope persistenceScope = null;

    /**
     * Creates a new instance of PersistenceScopeProviderImpl
     * @param locProvider the PersistenceLocationProvider instance to use for lookups
     * @param cpProvider the PersistenceClasspathProvider instance to use for lookups
     */
    public PersistenceScopeProviderImpl(PersistenceLocationProvider locProvider,
            PersistenceClassPathProvider cpProvider)
    {
        persistenceScopeImpl = new PersistenceScopeImpl(locProvider, cpProvider);
        persistenceScope = PersistenceScopeFactory.createPersistenceScope(persistenceScopeImpl);
    }

    /**
     * validated access to the current PersistenceScope instance by checking
     * the presence of a persistence.xml file
     * @param fileObject file to check for persistence scope, not used !
     * @return a valid PersistenceScope instance or null
     */
    public PersistenceScope findPersistenceScope(FileObject fileObject)
    {
        FileObject persistenceXml = persistenceScope.getPersistenceXml();
        if (persistenceXml != null && persistenceXml.isValid()) {
            return persistenceScope;
        }
        return null;
    }
}
