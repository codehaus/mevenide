/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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

package org.mevenide.netbeans.project;

import org.mevenide.netbeans.project.nodes.MavenProjectNode;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.LogicalViews;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class LogicalViewProviderImpl implements LogicalViewProvider
{
    private MavenProject project;
    /** Creates a new instance of LogicalViewProviderImpl */
    public LogicalViewProviderImpl(MavenProject proj)
    {
        project = proj;
    }
    
    public Node createLogicalView()
    {
        LogicalViewProvider genericPhysicalView = LogicalViews.physicalView( project );
        Node node = genericPhysicalView.createLogicalView();
        return new MavenProjectNode(node, project);
    }
    
}
