/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
package org.mevenide.ui.eclipse.sync.wip;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Resource;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.ui.views.properties.IPropertySource;
import org.mevenide.ui.eclipse.editors.properties.ResourcePropertySource;
import org.mevenide.util.MevenideUtils;


/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DirectoryMappingNode extends AbstractArtifactMappingNode {
    private static Log log = LogFactory.getLog(DirectoryMappingNode.class);
    
    private boolean conflicting;
    private boolean overrideSameValue;
    
    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
		if ( adapter == IPropertySource.class ) {
			if ( artifact instanceof Resource ) {
				return new ResourcePropertySource((Resource) artifact);
			}
		}
        return null;
    }
    
   
    public String getLabel() {
    	if ( resolvedArtifact != null ) {
            return ((Directory) resolvedArtifact).getDisplayPath();
        }
        if ( artifact instanceof Resource ) {
            return ((Resource) artifact).getDirectory();
        }
        if ( artifact instanceof Directory ) {
            return ((Directory) artifact).getDisplayPath();   
        }
        return "Unresolved";
    }
    
    public void setResolvedDirectory(Directory directory) {
        this.resolvedArtifact = directory;
    }
    
	/**
	 * either a Resource or Directory 
	 */
	public void setArtifact(Object object) {
		this.artifact = object;
	}

    public void setIdeEntry(IClasspathEntry entry) {
        this.ideEntry = entry;
    }
   
    public void setParent(DirectoryMappingNodeContainer container) {
        this.parent = container;
    }

	public int getChangeDirection() {
		if ( conflicting || overrideSameValue ) {
			return ProjectContainer.CONFLICTING;
		}
		
        if ( artifact == null ) {
        	return ProjectContainer.OUTGOING;
		}
        
        if ( resolvedArtifact == null ) {
			return ProjectContainer.INCOMING;
		}
		
		if ( artifact instanceof Directory 
				&& MevenideUtils.notEquivalent(((Directory) artifact).getType(), ((Directory) resolvedArtifact).getType()) ) {
			return ProjectContainer.CONFLICTING;
		} 
        return ProjectContainer.NO_CHANGE;
		
    }

	public boolean isConflicting() {
		return conflicting;
	}

	public void setConflicting(boolean conflicting) {
		this.conflicting = conflicting;
	}

	public boolean isOverrideSameValue() {
		return overrideSameValue;
	}

	public void setOverrideSameValue(boolean overrideSameValue) {
		this.overrideSameValue = overrideSameValue;
	}

}
