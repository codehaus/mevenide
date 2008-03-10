/*
 *  Copyright 2008 Anuradha.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.buildplan.nodes;

import java.awt.Image;
import java.util.Collection;
import javax.swing.Action;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.nodes.NodeUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class ModulesNode extends AbstractNode {

    /** Creates a new instance of ModulesNode */
    public ModulesNode( Collection<MavenProject> projects, String... tasks) {
        super(new ModulesChildren( projects, tasks));
        setName("Modules"); //NOI18N

        setDisplayName(NbBundle.getMessage(ModulesNode.class, "LBL_Modules"));
    }

    @Override
    public Action[] getActions(boolean bool) {
        return new Action[]{};
    }

    private Image getIcon(boolean opened) {
        Image badge = Utilities.loadImage("org/codehaus/mevenide/netbeans/modules-badge.png", true); //NOI18N

        return Utilities.mergeImages(NodeUtils.getTreeFolderIcon(opened), badge, 8, 8);
    }

    @Override
    public Image getIcon(int type) {
        return getIcon(false);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(true);
    }

    public static class ModulesChildren extends Children.Keys<MavenProject> {

        private Collection<MavenProject> projects;
        private String[] tasks;

        public ModulesChildren( Collection<MavenProject> projects, String... tasks) {
            this.projects = projects;
            this.tasks = tasks;
        }

        @Override
        protected Node[] createNodes(MavenProject arg0) {
            return new Node[]{new MavenProjectNode( arg0, tasks)};
        }

        @Override
        protected void addNotify() {
            setKeys(projects);
        }
        
    }
}
