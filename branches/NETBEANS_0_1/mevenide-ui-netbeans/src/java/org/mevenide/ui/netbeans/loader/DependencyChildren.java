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
package org.mevenide.ui.netbeans.loader;

import java.util.List;

import org.apache.maven.repository.Artifact;
import org.mevenide.ui.netbeans.ArtifactCookie;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class DependencyChildren extends Children.Keys
{
    
    private ArtifactCookie m_cookie;
    /** Creates a new instance of DependencyChildren */
    public DependencyChildren(ArtifactCookie projCookie)
    {
        setProjectCookie(projCookie);
    }
    
    private void setProjectCookie(ArtifactCookie projectCookie)
    {
        m_cookie = projectCookie;
    }
    
    
    protected Node[] createNodes(Object obj)
    {
        if (obj instanceof Artifact) {
            Artifact art = (Artifact)obj;
            AbstractNode node = new DependencyNode(art);
            return new Node[] {node};
        }
        return new Node[0];
    }
    
    protected void removeNotify()
    {
        setKeys(new Object[0]);
        super.removeNotify();
    }
    
    protected void addNotify()
    {
        super.addNotify();
        List project = m_cookie.getArtifacts();
        if (project == null)
        {
            setKeys(new Object[0]);
        } else {
            setKeys(project);
        }
    }
    
}
