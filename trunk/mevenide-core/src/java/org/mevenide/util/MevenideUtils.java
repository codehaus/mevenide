/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.util;

import java.io.File;
import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.Project;

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
	 * @param sourceDirectory
	 * @return false if source is null or does only contain whitespaces
	 */
	public static boolean isNull(String string) {
		return string == null 
		 		|| string.trim().equals(EMPTY_STR);
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
	public static String resolve(Project project, String unresolvedString) throws Exception {
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
	public static String resolve(Project project, String unresolvedString, boolean preserveBasedir) throws Exception {
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
}
