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
package org.mevenide.ui.eclipse.sync.model;

import org.apache.maven.project.Dependency;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.views.properties.IPropertySource;
import org.mevenide.ui.eclipse.editors.properties.DependencyPropertySource;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyMappingNode extends AbstractArtifactMappingNode implements IPropertyChangeListener {
    
    private Dependency dirtyDependency;
    
    public String getLabel() {
        if ( (parent.getDirection() & EclipseContainerContainer.OUTGOING) != 0 ) {
            Dependency displayDependency = null;
            if ( dirtyDependency != null ) {
                displayDependency = dirtyDependency ;
            }
            else {
                displayDependency = ((Dependency) resolvedArtifact); 
            }
            return displayDependency.getArtifactId() + "-" + displayDependency.getVersion();
		}
        if ( (parent.getDirection() & EclipseContainerContainer.INCOMING) != 0 ) {
            return ((Dependency) artifact).getGroupId() + ":" + ((Dependency) artifact).getArtifactId(); 
        }
		//NO_CHANGE or CONFLICTING
		if ( artifact != null ) {
			return ((Dependency) artifact).getGroupId() + ":" + ((Dependency) artifact).getArtifactId();
		}
		return "Unresolved";
    }
   
    public Object getAdapter(Class adapter) {
        if ( adapter == IPropertySource.class ) {
            DependencyPropertySource propertySource = null;
            if ( artifact == null ) {
				if ( dirtyDependency != null ) {
					propertySource = new DependencyPropertySource((Dependency) resolvedArtifact);
				}
				else {
	                propertySource = new DependencyPropertySource((Dependency) resolvedArtifact);
				}
            }
			else {
            	propertySource = new DependencyPropertySource((Dependency) artifact);
			}
            propertySource.addPropertyChangeListener(this);
            return propertySource;
        }
        return null;
    }

    public int getChangeDirection() {
        if ( resolvedArtifact == null ) {
            return EclipseContainerContainer.INCOMING;
        }
        if ( artifact == null ) {
            return EclipseContainerContainer.OUTGOING;
        }
        if ( !((Dependency) artifact).getVersion().equals(((Dependency) resolvedArtifact).getVersion()))  {
            //donot compare groupId b/c in most case it may not be resolved
            //|| !dependency.getGroupId().equals(resolvedDependency.getGroupId())
            return EclipseContainerContainer.CONFLICTING;
        }
        return EclipseContainerContainer.NO_CHANGE;
    }
    
    public void setDependency(Dependency d) {
        artifact = d;
    }
    public void setResolvedDependency(Dependency resolvedDependency) {
        this.resolvedArtifact = resolvedDependency;
    }
    public void setIdeEntry(Object ideEntry) {
        this.ideEntry = ideEntry;
    }
    public void propertyChange(PropertyChangeEvent event) {
        setDirtyDependency((Dependency)((DependencyPropertySource)event.getSource()).getSource());
    }
    public Dependency getDirtyDependency() {
        return this.dirtyDependency;
    }
    public void setDirtyDependency(Dependency dirtyDependency) {
        this.dirtyDependency = dirtyDependency;
    }
}
