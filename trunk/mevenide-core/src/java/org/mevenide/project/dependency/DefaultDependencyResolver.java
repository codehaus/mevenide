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

import java.io.File;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DefaultDependencyResolver implements IDependencyResolver {
	private String artifact;
	
	private String fileName;
	
	private String artifactId;
	private String version;
	private String extension;
	private String groupId;
	

	private IDependencySplitter.DependencyParts dependencyParts ;
	
	public void setFileName(String fName) {
		this.artifact = fName;
		this.fileName = new File(fName).getName();
		dependencyParts = new DependencySplitter(fileName).split();
		init();
	}

	private void init() {
		initArtifactId();
		initVersion();
		initExtension();
		initGroupId();
	}
	
	private void initArtifactId() {
		artifactId = dependencyParts.artifactId;
		if ( artifactId == null && fileName.indexOf("SNAPSHOT") > 0 ) {
			artifactId = fileName.substring(0, fileName.indexOf("SNAPSHOT") - 1);
		}
	}
	
	private void initVersion() {
		version = dependencyParts.version;
		if ( version == null && fileName.indexOf("SNAPSHOT") > 0 ) {
			version = "SNAPSHOT";
		}
	}
	
	private void initExtension() {
		extension = fileName.substring(fileName.lastIndexOf('.') + 1);
	}
	
	private void initGroupId() {
		File fileToCompute = new File(artifact);
		File firstLevelParent = fileToCompute.getParentFile();
		if ( firstLevelParent != null && firstLevelParent.getParentFile() != null ) {
			groupId = firstLevelParent.getParentFile().getName();
		}
		if ( !DependencyUtil.isValidGroupId(groupId) ) {
			groupId = null;
		}
	}

	public String guessArtifactId() {
		return artifactId;
	}

	public String guessVersion() {
		return version;
	}

	public String guessExtension() {
		return extension;
	}

	public String guessGroupId()  {
		return groupId;
	}

	
	
	
	
	

}
