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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import org.netbeans.modules.j2ee.persistence.api.PersistenceScope;
import org.netbeans.modules.j2ee.persistence.api.PersistenceScopes;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceScopeProvider;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceScopesProvider;
import org.netbeans.modules.j2ee.persistence.spi.support.PersistenceScopesHelper;
import org.openide.filesystems.FileUtil;

/**
 * Maven2 Implementation of 
 * <CODE>org.netbeans.modules.j2ee.persistence.spi.PersistenceScopesProvider</CODE>
 * also implements PropertyChangeListener to watch for changes on the persistence.xml file
 * @author Daniel Mohni
 */
public class PersistenceScopesProviderImpl implements PersistenceScopesProvider, PropertyChangeListener
{
    private PersistenceScopesHelper scopesHelper = null;
    private PersistenceScopeProvider scopeProvider = null;
    
    /**
     * Creates a new instance of PersistenceScopesProviderImpl
     * @param provider the PersistenceScopeProvider instance to use for lookups
     */
    public PersistenceScopesProviderImpl(PersistenceScopeProvider provider)
    {
        scopesHelper = new PersistenceScopesHelper();
        scopeProvider = provider;
        checkScope();
    }
    
    /**
     * property access to the persistence scopes
     * @return the PersistenceScopes instance of the current project
     */
    public PersistenceScopes getPersistenceScopes()
    {
        return scopesHelper.getPersistenceScopes();
    }
    
    
    /**
     * checks and initialise, updates the PersistenceScopeHelper of the 
     * current project 
     */
    private void checkScope()
    {
        File persistenceXml = null;
        PersistenceScope scope = scopeProvider.findPersistenceScope(null);
        
        if (scope != null)
        {
            persistenceXml = FileUtil.toFile(scope.getPersistenceXml());
            scopesHelper.changePersistenceScope(scope, persistenceXml);
        }
        else
        {
           scopesHelper.changePersistenceScope(null, null);
        }
    }

    /**
     * watches for creation and deletion of the persistence.xml file
     * @param evt the change event to process
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (MavenPersistenceProvider.PROP_PERSISTENCE.equals(evt.getPropertyName()))
        {
            checkScope();
        }
    }
    
}
