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
package org.mevenide.ui.eclipse.sync.model.source;

import java.io.File;

import org.mevenide.project.ProjectConstants;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectory {
	
    private String directoryPath = "";
	private String directoryType = ProjectConstants.MAVEN_SRC_DIRECTORY;
	
	private boolean isInherited;
	private SourceDirectoryGroup group;
	
	private boolean isReadOnly;
	protected boolean isInPom;
	
	public SourceDirectory(String path, SourceDirectoryGroup group) {
		//directoryPath = "${basedir}" + File.separator + path;
		directoryPath = path;
		this.group = group;
	}
	
	public String getDisplayPath() {
		if ( directoryPath.equals(ProjectConstants.BASEDIR)) {
			return ProjectConstants.BASEDIR;
		}
		return ProjectConstants.BASEDIR + File.separator +  directoryPath;
	}
	
	public String getDirectoryPath() {
		return directoryPath;
	}
	
	public String getDirectoryType() {
		return directoryType;
	}
	
	public void setDirectoryType(String newDirectoryType) {
		directoryType = newDirectoryType;
	}
	
	public boolean equals(Object o) {
		return (o instanceof SourceDirectory)
				&& ((SourceDirectory) o).getDirectoryPath() != null
				&& ((SourceDirectory) o).getDirectoryPath().equals(directoryPath);
				
	}
	
	public boolean isSource() {
		boolean b = getDirectoryType().equals(ProjectConstants.MAVEN_ASPECT_DIRECTORY)
					|| getDirectoryType().equals(ProjectConstants.MAVEN_SRC_DIRECTORY)
					|| getDirectoryType().equals(ProjectConstants.MAVEN_TEST_DIRECTORY)
					|| getDirectoryType().equals(ProjectConstants.MAVEN_INTEGRATION_TEST_DIRECTORY);
		return b;					
	}
    public boolean isInherited() {
        return isInherited;
    }

    public void setInherited(boolean isInherited) {
        this.isInherited = isInherited;
    }

    public SourceDirectoryGroup getGroup() {
        return group;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }
	
	public boolean isInPom() {
		return isInPom;
	}

	public void setInPom(boolean isInPom) {
		this.isInPom = isInPom;
	}
}
