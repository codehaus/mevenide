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
package org.codehaus.mevenide.pde.classpath;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.codehaus.mevenide.pde.PdePluginException;
import org.codehaus.mevenide.pde.resources.Messages;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


/**  
 * 
 * transively resolves the dependencies of an eclipse plugin 
 * 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PluginClasspathResolver {

    /** folder of the project under construction */
    private File basedir;
    
    /** absolute path of eclipse home directory */
    private String eclipseHome;
    
    /** used to stop dependencies extraction recursion */
    private HashSet pluginDescriptors;
    
    /** non blocking warnings */
    private List infos = new ArrayList();
    
    /**
     * @param basedir folder of the project under construction
     * @param eclipseHome absolute path of eclipse home directory
     * @throws PdePluginException if unable to parse the .classpath : IOException or JDOMException wrapped
     */
    public PluginClasspathResolver(File basedir, String eclipseHome) {
        pluginDescriptors = new HashSet(); 
        
        this.eclipseHome = eclipseHome;
        this.basedir = basedir;
        
    }
    
    /**
     * extract the eclipse dependencies from plugin.xml 
     * 
     * @return  the list of eclipse dependencies
     * @throws PdePluginException
     */
    public Collection extractEclipseDependencies() throws PdePluginException {
        return extractDependenciesFromDescriptor(basedir.getAbsolutePath());        
    }

    /**
     * extract the dependencies using the requires/import elements in plugin.xml file.
     * 
     * @return  the list of eclipse dependencies
     * @throws PdePluginException
     */
    public Collection extractDependenciesFromDescriptor(String pluginHome) throws PdePluginException {
        Set dependencies = new TreeSet();

        File pluginDescriptor = new File(pluginHome, "plugin.xml");
        
        Document document = null;
        
        try {
            document = new SAXBuilder().build(pluginDescriptor);
        }
        catch (Exception e) {
            String message = Messages.get("ClasspathResolution.CannotExtractDependencies", e.getMessage()); 
            throw new PdePluginException(message, e);
        }
        
        Element requiresElement = document.getRootElement().getChild("requires");
        
        if ( requiresElement != null ) {
            List importElements = requiresElement.getChildren("import");
            for ( Iterator it = importElements.iterator(); it.hasNext(); ) {
                Element importElement = (Element) it.next();
                String dependency = importElement.getAttributeValue("plugin");
                
                String[] libraries = findDependencyLibraries(dependency);
                dependencies.addAll(Arrays.asList(libraries));
		    }
        }
        
        return dependencies;
    }

    /**
     * returns the list of full path of dependencies exported by an eclipse dependency identified by its id
     * 
     * f.i. if plugin.xml declares :
     * <pre>
     *  <runtime>
     *    <library name="some.plugin"/>
     *  </runtime>
     * </pre>
     * 
     * and some.plugin exports a.jar and lib/b.jar then this methods will return [{eclipse.home}/plugins/lib/b.jar, {eclipse.home}/plugins/a.jar] 
     * 
     * @param dependency plugin name for which we want to resolve dependencies
     * @return the list of full path of dependencies exported by an eclipse dependency 
     */
    private String[] findDependencyLibraries(final String dependency) throws PdePluginException {
        if ( !pluginDescriptors.contains(dependency) ) {
	        pluginDescriptors.add(dependency);
	        
	        List libraries = new ArrayList();
	        
	        File pluginDescriptor = findDependencyDescriptor(dependency);
	        
			if ( pluginDescriptor != null ) { 
		        Document document = null;
		        try {
		            document = new SAXBuilder().build(pluginDescriptor);
		        }
		        catch (Exception e) {
		            String message = Messages.get("ClasspathResolution.CannotExtractDependencies", e.getMessage()); 
		            throw new PdePluginException(message, e);
		        }
		        Element plugin = document.getRootElement();
		        
		        Element runtimeElement = plugin.getChild("runtime");
		        if ( runtimeElement != null ) {
		            List libraryElements = runtimeElement.getChildren("library");
		            for (int i = 0; i < libraryElements.size(); i++) {
		                Element libraryElement = (Element) libraryElements.get(i);
		                if ( libraryElement.getChild("export") != null ) {
			                String library = libraryElement.getAttributeValue("name");
			                libraries.add(new File(pluginDescriptor.getParent(), library).getAbsolutePath());
			            }
		            }
		        }
		        
		        Element requireElement = plugin.getChild("requires");
		        if ( requireElement != null ) {
			        List importElements = requireElement.getChildren("import");
			        if ( importElements != null ) {
			            for (int i = 0; i < importElements.size(); i++) {
		                    Element importElement = (Element) importElements.get(i);
		                    libraries.addAll(Arrays.asList(findDependencyLibraries(importElement.getAttributeValue("plugin"))));
		                }
			        }
		        }
		        
		        return (String[]) libraries.toArray(new String[libraries.size()]);
			}
        }
        
        return new String[0];
    }
    
    /**
     * finds the install folder of an eclipse plugin 
     * 
     * @param dependency the formal id of the dependency, f.i org.eclipse.text
     * @return the install folder of the plugin 
     */
    private File findDependencyDescriptor(final String dependency) throws PdePluginException {
        File[] parentNames = new File(eclipseHome, "plugins").listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() && 
                       pathname.getName().indexOf(dependency) >= 0 && 
                       pathname.getName().lastIndexOf("_") == dependency.length();
            }
        });
        
        if ( parentNames == null ) {
            String message = Messages.get("ClasspathResolution.ComputeDependencyParent", dependency);
            throw new PdePluginException(message);
        }
		
		if ( parentNames.length > 0 ) {
			//some plugins may be missing
			File dependencyHome = parentNames[0];
			return new File(dependencyHome, "plugin.xml");
		}
		
		return null;
    }

    /**
     * @param classpath toplevel &lt;classpath&gt; element 
     * @return true if .classpath uses the <code>org.eclipse.pde.core.requiredPlugins</code> container to reference other plugin libraries false otherwise
     */
    boolean checkEclipseDependenciesContainer() {
        Document document = null; 
        
        File classpathFile = new File(basedir, ".classpath");
        
        try {
            document = new SAXBuilder().build(classpathFile);
        }
        catch (Exception e) {
            String message = Messages.get("ClasspathResolution.NoClasspath", e.getMessage()); 
            addInfo(message);
            return false;
        }    
 
        Element classpath = document.getRootElement();
        
        List children = classpath.getChildren();
        List elems = new ArrayList();
        for (Iterator iter = children.iterator(); iter.hasNext();) {
            Element descendant = (Element) iter.next();
            if ( "classpathentry".equals(descendant.getName()) && 
                 "con".equals(descendant.getAttributeValue("kind")) &&
                 "org.eclipse.pde.core.requiredPlugins".equals(descendant.getAttributeValue("path")) ) {
                elems.add(descendant);
            }
        }
        boolean useDependenciesContainer = elems.size() > 0;
        
        if ( !useDependenciesContainer ) {
            addInfo(Messages.get("ClasspathResolution.NoContainer"));
        }
        
        return useDependenciesContainer;
    }
    
    private void addInfo(String info) {
        if ( !infos.contains(info) ) {
            infos.add(info);
        }
    }
    
    
    public List getInfos() {
        return infos;
    }
}
