/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.MavenProject;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public final class MevenideUtils {
	public static final String EMPTY_STR = "";
	public static final String PROPERTY_SEPARATOR = ":";
	
	private MevenideUtils() { }
	
	public static boolean findFile(File rootDirectory, String fileName) {
		File[] f = rootDirectory.listFiles();
		for (int i = 0; i < f.length; i++) {
			if ( f[i].isDirectory() ) {
				if ( findFile(f[i], fileName) ) {
					return true;
				}
			}
			else {
				if ( f[i].getName().equals(fileName) ) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param newValue
	 * @param oldValue
	 * @return true if new and old values are not equal (or new is null, but old is not null)
	 */
	public static boolean notEquivalent(Object newValue, Object oldValue) {
		// if both null, no difference
		if (newValue == null && oldValue == null) {
			return false;
		}
		else if (newValue == null){
			return true;
		}
		return (!newValue.equals(oldValue));
	}

	/**
	 * retrieve a string containing jelly scripting variable by navigating pom object
	 * so until a jelly variable is a descendant of pom it wont be resolved and the 
	 * returnde string will still contains the reference.
	 * 
	 * this is equivalent to resolve(project, unresolvedString, false) 
	 * 
	 * @param project
	 * @param unresolvedString
	 * @return
	 * @throws Exception
	 * @see #resolve(Project project, String unresolvedString, boolean preserveBasedir)
	 */
	public static String resolve(MavenProject project, String unresolvedString) throws Exception {
		return resolve(project, unresolvedString, false);
	}
	
	/**
	 *  retrieve a string containing jelly scripting variable by navigating pom object
	 * so until a jelly variable is a descendant of pom it wont be resolved and the 
	 * returnde string will still contains the reference. if preserveBasedir is true, 
	 * then ${basedir} wont be evaluated, else it will be replaced by "."
	 * 
	 * @param project
	 * @param unresolvedString
	 * @return
	 * @throws Exception
	 * @see #resolve(Project project, String unresolvedString, boolean preserveBasedir)
	 */
	public static String resolve(MavenProject project, String unresolvedString, boolean preserveBasedir) throws Exception {
		String resolvedString = EMPTY_STR;
		
		String tempVariable = EMPTY_STR;
		
		for (int i = 0; i < unresolvedString.length(); i++) {
			if ( unresolvedString.charAt(i) == '$' ) {
				tempVariable += unresolvedString.charAt(i);
			}
            if ( unresolvedString.charAt(i) != '$'
            		&& unresolvedString.charAt(i) != '{'
					&& unresolvedString.charAt(i) != '}' ) {
				if ( !tempVariable.equals(EMPTY_STR) ) {
					tempVariable += unresolvedString.charAt(i);
				}		
				else {
					resolvedString += unresolvedString.charAt(i);
				}
			}
			if ( unresolvedString.charAt(i) == '}' ) {
				tempVariable = tempVariable.substring(1, tempVariable.length()); 
				if ( !tempVariable.startsWith("pom") && !tempVariable.startsWith("basedir") ) {
					//return the string as is since we wont be able to resolve it
					return unresolvedString;
				}
				else {
					Object evaluation = null;
					if ( tempVariable.startsWith("basedir") ) {
						if ( preserveBasedir  ) {
							evaluation = "${basedir}";
						}
						else {
							evaluation = ".";
						}
					}
					else {
						String[] splittedVar = StringUtils.split(tempVariable, ".");
						evaluation = project.getModel();
						for (int j = 1; j < splittedVar.length; j++) {
							Field f = evaluation.getClass().getDeclaredField(splittedVar[j]);
							f.setAccessible(true);
	                	    evaluation = f.get(evaluation);
	                    	f.setAccessible(false);
	                	}
					}
	                resolvedString += evaluation;
	                tempVariable = EMPTY_STR;
				}
			}
		}
		return resolvedString;
	}
	
	/**
	 * Resolves a string in the Maven kludgy name:value format to an array
	 * of strings, guaranteed to be exactly two items in length: [name, value].
	 * @param property
	 * @return
	 */
	public static String[] resolveProperty(String property) {
		String[] parts = property.split(PROPERTY_SEPARATOR);
		String name = parts[0];
		String value;
		if (parts.length > 1) {
			value = parts[1];
		} else {
			value = EMPTY_STR;
		}
		return new String[] {name, value};
	}
	
	/**
	 * copied from org.apache.maven
	 * 
     * Convert an absolute path to a relative path if it is under a given base directory.
     * @param basedir the base directory for relative paths
     * @param path the directory to resolve
     * @return the relative path
     * @throws IOException if canonical path fails
     */
    public static String makeRelativePath( File basedir, String path ) throws IOException {
        String canonicalBasedir = basedir.getCanonicalPath();
        String canonicalPath = new File( path ).getCanonicalPath();

        if ( canonicalPath.equals(canonicalBasedir) ) {
            return ".";
        }

        if ( canonicalPath.startsWith( canonicalBasedir ) ) {
            if ( canonicalPath.charAt( canonicalBasedir.length() ) == File.separatorChar ) {
                canonicalPath = canonicalPath.substring( canonicalBasedir.length() + 1 );
            }
            else {
                canonicalPath = canonicalPath.substring( canonicalBasedir.length() );
            }
        }
        return canonicalPath;
    }
    
    public static List asList(Map properties) {
    	List list = new ArrayList();
    	Iterator iterator = properties.keySet().iterator();
    	while ( iterator.hasNext() ) {
    		String nextKey = (String) iterator.next();
    		list.add(nextKey + PROPERTY_SEPARATOR + properties.get(nextKey));
    	}
    	return list;
    }
    
    public static Properties asProperties(List properties) {
    	Properties props = new Properties();
    	for (int i = 0; i < properties.size(); i++) {
			String[] resolvedProperty = resolveProperty((String) properties.get(i));
			props.put(resolvedProperty[0], resolvedProperty[1]);
		}
    	return props;
    }
}
