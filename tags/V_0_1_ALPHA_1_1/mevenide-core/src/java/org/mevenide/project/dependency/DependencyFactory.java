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
package org.mevenide.project.dependency;


import org.apache.maven.project.Dependency;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DependencyFactory {
	
	private DependencyFactory() throws Exception {
	}
	
	/** singleton related */
	private static DependencyFactory factory = null;
	private static Object lock = new Object();

	public static DependencyFactory getFactory() throws Exception {
		if (factory != null) {
			return factory;
		}
		synchronized (lock) {
			if (factory == null) {
				factory = new DependencyFactory();
			}
			return factory;
		}
	}
		
	/**
	 * return the Dependency instance associated with a given path.
	 * however this seems hard if not impossible to achieve in a 100% way.
	 * 
	 * Also if a file is found in local repo that match the fileName passed 
	 * as parameters, we'll use ${absoluteFileName.parent.parent.name} as 
	 * groupId. in either case we have to guess artifactId and version from the fileName.
	 * 
	 * @param absoluteFileName
	 * @return
	 */
	public Dependency getDependency(String absoluteFileName) throws Exception {
		IDependencyResolver dependencyResolver = AbstractDependencyResolver.newInstance(absoluteFileName);
		
		String groupId = dependencyResolver.guessGroupId();
		
		String artifactId = dependencyResolver.guessArtifactId();
		String version = dependencyResolver.guessVersion();
		String extension = dependencyResolver.guessExtension();
		
		Dependency dependency = new Dependency();
		
		dependency.setGroupId(groupId == null ? "" : groupId); //?
		dependency.setArtifactId(artifactId);
		dependency.setVersion(version);
		//dependency.setArtifact(absoluteFileName);
		dependency.setJar(absoluteFileName);
		dependency.setType(extension);
		
		return dependency;
	}


}
