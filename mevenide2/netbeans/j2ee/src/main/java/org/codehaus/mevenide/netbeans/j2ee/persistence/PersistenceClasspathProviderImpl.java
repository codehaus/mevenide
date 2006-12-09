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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.j2ee.metadata.ClassPathSupport;
import org.netbeans.modules.j2ee.persistence.spi.PersistenceClassPathProvider;

/**
 * Maven2 Implementation of 
 * <CODE>org.netbeans.modules.j2ee.persistence.spi.PersistenceClassPathProvider</CODE> 
 * @author Daniel Mohni
 */
public class PersistenceClasspathProviderImpl implements PersistenceClassPathProvider
{
    private NbMavenProject project;
    
    private ClassPath projectSourcesClassPath;
    
    
    /**
     * Creates a new instance of PersistenceClasspathProviderImpl
     * @param proj reference to the NbMavenProject provider
     */
    public PersistenceClasspathProviderImpl(NbMavenProject proj)
    {
        project = proj;
    }
    
    /**
     * retrieves the source classpath used available in maven2
     * @return classpath of this project
     */
    public ClassPath getClassPath()
    {
        synchronized (this)
        {
            if (projectSourcesClassPath == null)
            {
                ClassPathProviderImpl cpProvider = (ClassPathProviderImpl)project
                    .getLookup().lookup(ClassPathProviderImpl.class);
                
                projectSourcesClassPath = ClassPathSupport.createWeakProxyClassPath(
                    getPersistenceClassPaths(cpProvider));
            }
            return projectSourcesClassPath;
        }
    }
    
    private ClassPath[] getPersistenceClassPaths(ClassPathProviderImpl cpProvider) 
   {
         List/*<ClassPath>*/ combined = new ArrayList();
         ClassPath[] temp = null;

         temp = cpProvider.getProjectClassPaths(ClassPath.SOURCE);
         combined.addAll(Arrays.asList(temp));
         
         temp = cpProvider.getProjectClassPaths(ClassPath.COMPILE);
         combined.addAll(Arrays.asList(temp));

         return (ClassPath[])combined.toArray(new ClassPath[combined.size()]);
    }
}
