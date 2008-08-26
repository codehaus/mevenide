/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s): theanuradha@netbeans.org
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.maven.buildplan.nodes;

import java.awt.Image;
import javax.swing.Action;
import org.apache.maven.lifecycle.MojoBindingUtils;
import org.apache.maven.lifecycle.model.MojoBinding;
import org.apache.maven.project.MavenProject;
import org.netbeans.modules.maven.buildplan.BuildPlanView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class MojoNode extends AbstractNode {

    private MojoBinding mb;

    public MojoNode(BuildPlanView view, MavenProject nmp, MojoBinding mb) {
        super(createChildren(view, nmp, mb), Lookups.fixed(view, nmp, mb));
        this.mb = mb;
        setDisplayName(MojoBindingUtils.toString(mb));
        setShortDescription(MojoBindingUtils.toString(mb));

    }

    @Override
    public Image getIcon(int arg0) {
        return Utilities.loadImage("org/netbeans/modules/maven/buildplan/nodes/mojo.png");
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[] { 
//            new FixateVersion()
        };
    }

    @Override
    public String getHtmlDisplayName() {
        StringBuffer buffer = new StringBuffer("<html>");
        buffer.append(mb.getGroupId()).append(" : ").
                append(mb.getArtifactId()).append(" : ").
                append(mb.getVersion() == null ? "" : mb.getVersion() + ":").
                append("  <b>").append(mb.getGoal());
        return buffer.append("</html>").toString();
    }

    public static Children createChildren(BuildPlanView view, MavenProject nmp, MojoBinding mb) {
        Children.Array array = new Children.Array();
        Node[] nodes = new Node[mb.getConfiguration()==null? 1:2];
        nodes[0] = new ExecutionNode(mb);
        if (mb.getConfiguration()!=null) {
            nodes[1] = new ConfigurationNode(mb);
        }
        array.add(nodes);
        return array;
    }
    
//    private class FixateVersion extends AbstractAction {
//
//        private FixateVersion() {
//            setEnabled(mb.isLateBound());
//            putValue(Action.NAME, "Fixate version in POM");
//        }
//
//        public void actionPerformed(ActionEvent e) {
//            
//        }
//        
//    }
}
