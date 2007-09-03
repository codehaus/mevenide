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

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.j2ee.persistence.api.PersistenceScope;
import org.netbeans.modules.j2ee.persistence.api.PersistenceScopes;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceClassPathProvider;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceLocationProvider;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceScopeProvider;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceScopesProvider;
import org.netbeans.modules.j2ee.persistenceapi.FileChangeSupport;
import org.netbeans.modules.j2ee.persistenceapi.FileChangeSupportEvent;
import org.netbeans.modules.j2ee.persistenceapi.FileChangeSupportListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Daniel Mohni
 */
public class MavenPersistenceProvider implements PersistenceLocationProvider, 
        PersistenceScopeProvider, PersistenceScopesProvider, 
        PersistenceClassPathProvider
{
    public static final String PROP_PERSISTENCE = "MavenPersistence"; //NOI18N
    
    private PersistenceLocationProviderImpl  locProvider = null;
    private PersistenceClasspathProviderImpl cpProvider = null;
    private PersistenceScopesProviderImpl    scopesProvider   = null;
    private PersistenceScopeProviderImpl     scopeProvider    = null;
   
    private final PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);
    private final FileChangeSupportListener listener = new FileListener();
    
    /**
     * Creates a new instance of MavenPersistenceProvider
     */
    public MavenPersistenceProvider(NbMavenProject proj)
    {
        locProvider    = new PersistenceLocationProviderImpl(proj);
        cpProvider     = new PersistenceClasspathProviderImpl(proj);
        scopeProvider  = new PersistenceScopeProviderImpl(locProvider, cpProvider);
        scopesProvider = new PersistenceScopesProviderImpl(scopeProvider);
        
        propChangeSupport.addPropertyChangeListener(locProvider);
        propChangeSupport.addPropertyChangeListener(scopesProvider);
                
        // add FileChangeListener on persistence.xml
        File persistenceXml = locProvider.getPersistenceXml();
        FileChangeSupport.DEFAULT.addListener(listener, persistenceXml);
    }

    /**************************************************************************
     * PersistenceLocationProvider methodes
     *************************************************************************/
    public FileObject getLocation()
    {
        return locProvider.getLocation();
    }

    public FileObject createLocation() throws IOException
    {
         return locProvider.createLocation();
    }

    /**************************************************************************
     * PersistenceScopeProvider methodes
     *************************************************************************/
    public PersistenceScope findPersistenceScope(FileObject fileObject)
    {
        return scopeProvider.findPersistenceScope(fileObject);
    }

    /**************************************************************************
     * PersistenceScopesProvider methodes
     *************************************************************************/
    public PersistenceScopes getPersistenceScopes()
    {
        return scopesProvider.getPersistenceScopes();
    }

    /**************************************************************************
     * PersistenceClasspathProvider methodes
     *************************************************************************/
    public ClassPath getClassPath()
    {
        return cpProvider.getClassPath();
    }
    
    
    /**************************************************************************
     * FileChangeSupportListener for Persistence.xml
     *************************************************************************/
    private class FileListener implements FileChangeSupportListener 
    {
        public void fileCreated(FileChangeSupportEvent fileChangeSupportEvent)
        {
            propChangeSupport.firePropertyChange(PROP_PERSISTENCE, null, null);
        }

        public void fileDeleted(FileChangeSupportEvent fileChangeSupportEvent)
        {
            propChangeSupport.firePropertyChange(PROP_PERSISTENCE, null, null);
        }

        public void fileModified(FileChangeSupportEvent fileChangeSupportEvent)
        {
            // we don't care about file changes
        }
    }
 }
