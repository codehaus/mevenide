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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ArtifactMappingContentProvider implements ITreeContentProvider {
    private static Log log = LogFactory.getLog(ArtifactMappingContentProvider.class); 
    
    private int direction;
    
    public Object[] getChildren(Object parentElement) {
        if ( parentElement instanceof ProjectContainer ) {
            IArtifactMappingNodeContainer directoryContainer = new DirectoryMappingNodeContainer();
            DirectoryMappingNode node = new DirectoryMappingNode();
            Directory dir = new Directory();
            dir.setPath("${basedir}/src/java");
            dir.setType("sourceDirectory");
            node.setDirectory(dir);
            directoryContainer.setNodes(new IArtifactMappingNode[] { node });
            
            IProject project = ((ProjectContainer) parentElement).getProject();
            IArtifactMappingNodeContainer dependencyContainer = new DependencyMappingNodeContainer();
            try {
                if ( project.hasNature(JavaCore.NATURE_ID) ) {
                    dependencyContainer = DependencyMappingNodeContainerFactory.getFactory().getContainer(JavaCore.create(project));
                }
            }
            catch (CoreException e) {
                log.error(e);
            }
//            DependencyMappingNode d = new DependencyMappingNode();
//            Dependency dep = new Dependency();
//            dep.setArtifactId("myArtifactId");
//            dep.setGroupId("myGroupId");
//            dep.setVersion("1.0.1");
//            d.setDependency(dep);
//            dependencyContainer.setNodes(new IArtifactMappingNode[] { d });
            
            return new IArtifactMappingNodeContainer[] { directoryContainer.filter(direction), dependencyContainer.filter(direction) }; 
        }
        if ( parentElement instanceof IArtifactMappingNodeContainer ) {
            return ((IArtifactMappingNodeContainer) parentElement).getNodes();
        }
        return null;
    }
    
    public Object getParent(Object element) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public boolean hasChildren(Object element) {
        // TODO Auto-generated method stub
        return getChildren(element) != null;
    }
    
    public void dispose() {
    }
    
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
    
    public Object[] getElements(Object inputElement) {
        return new Object[] { new ProjectContainer((IProject) inputElement) };
    }
    
    public int getDirection() {
        return direction;
    }
    
    public void setDirection(int direction) {
        this.direction = direction;
    }
}

