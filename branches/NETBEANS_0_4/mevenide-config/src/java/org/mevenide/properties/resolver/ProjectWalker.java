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
package org.mevenide.properties.resolver;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.Project;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ProjectWalker {
    private static final String EMPTY_STR = "";
    
    private Project project;
    
    public ProjectWalker(Project project) {
        this.project = project;
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
	public String resolve(String unresolvedString) throws Exception {
		return resolve(unresolvedString, false);
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
	public String resolve(String unresolvedString, boolean preserveBasedir) throws Exception {
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
						evaluation = project;
						for (int j = 1; j < splittedVar.length; j++) {
						    try {
								Field f = evaluation.getClass().getDeclaredField(splittedVar[j]);
								f.setAccessible(true);
		                	    evaluation = f.get(evaluation);
		                    	f.setAccessible(false);
						    }
						    catch ( NoSuchFieldException ex ) {
							    //try to get the field from the superclass
				                Field f = evaluation.getClass().getSuperclass().getDeclaredField(splittedVar[j]);
				                f.setAccessible(true);
				                evaluation = f.get(evaluation);
				                f.setAccessible(false);
						    }
	                	}
					}
	                resolvedString += evaluation;
	                tempVariable = EMPTY_STR;
				}
			}
		}
		return resolvedString;
	}
	
}
