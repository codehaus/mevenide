/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Mevenide @ Sourceforge.net.  All rights
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
public class DependencyMappingNode implements IArtifactMappingNode, IPropertyChangeListener {
    
    /** may be instance of IProject or IClasspathEntry */
    private Object ideEntry;
    private Dependency resolvedDependency;
    private Dependency dependency;
    
    private DependencyMappingNodeContainer parent; 
    
    public Object getIdeEntry() {
        return ideEntry;
    }

    
    public Object getArtifact() {
        return dependency;
    }

    public void setDependency(Dependency d) {
        dependency = d;
    }
    
    public int getChangeDirection() {
        if ( resolvedDependency == null ) {
			return ProjectContainer.INCOMING;
		}
		if ( dependency == null ) {
			return ProjectContainer.OUTGOING;
		}
		if ( !dependency.getVersion().equals(resolvedDependency.getVersion()) ) {
		    //donot compare groupId b/c in most case it may not be resolved
		    //|| !dependency.getGroupId().equals(resolvedDependency.getGroupId())
			return ProjectContainer.CONFLICTING;
		}
        return ProjectContainer.NO_CHANGE;
    }
    
    public String getLabel() {
        if ( (parent.getDirection() & ProjectContainer.OUTGOING) != 0 ) {
            return resolvedDependency.getArtifactId() + "-" + resolvedDependency.getVersion();
		}
        if ( (parent.getDirection() & ProjectContainer.INCOMING) != 0 ) {
            return dependency.getGroupId() + ":" + dependency.getArtifactId(); 
        }
		//NO_CHANGE or CONFLICTING
		if ( dependency != null ) {
			return dependency.getGroupId() + ":" + dependency.getArtifactId();
		}
		return "Unresolved";
    }
   
    public Object getAdapter(Class adapter) {
        if ( adapter == IPropertySource.class ) {
            DependencyPropertySource propertySource = null;
            if ( dependency == null ) {
                dependency = new Dependency();
				propertySource = new DependencyPropertySource(resolvedDependency);
            }
			else {
            	propertySource = new DependencyPropertySource(dependency);
			}
            propertySource.addPropertyChangeListener(this);
            return propertySource;
        }
        return null;
    }
    
    public void setResolvedDependency(Dependency resolvedDependency) {
        this.resolvedDependency = resolvedDependency;
    }
   
    public Object getResolvedArtifact() {
        return resolvedDependency;
    }
    
    public void setIdeEntry(Object ideEntry) {
        this.ideEntry = ideEntry;
    }
    
    public void propertyChange(PropertyChangeEvent event) {
        setDependency((Dependency)((DependencyPropertySource)event.getSource()).getSource());
    }
    
    public IArtifactMappingNodeContainer getParent() {
        return parent;
    }
    
    public void setParent(DependencyMappingNodeContainer parent) {
        this.parent = parent;
    }
}
