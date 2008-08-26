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
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.nodes;
import org.netbeans.modules.maven.spi.nodes.AbstractMavenNodeList;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.Node;

/**
 *
 * @author mkleint
 */
public class SourcesNodeFactory implements NodeFactory {
    
    /** Creates a new instance of SourcesNodeFactory */
    public SourcesNodeFactory() {
    }
    
    public NodeList createNodes(Project project) {
        NbMavenProjectImpl prj = project.getLookup().lookup(NbMavenProjectImpl.class);
        return  new NList(prj);
    }
    
    private static class NList extends AbstractMavenNodeList<SourceGroup> implements ChangeListener {
        private NbMavenProjectImpl project;
        private NList(NbMavenProjectImpl prj) {
            project = prj;
        }
        
        public List<SourceGroup> keys() {
            List<SourceGroup> list = new ArrayList<SourceGroup>();
            Sources srcs = ProjectUtils.getSources(project);
            SourceGroup[] javagroup = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            for (int i = 0; i < javagroup.length; i++) {
                    list.add(javagroup[i]);
                }
            return list;
        }
        
        public Node node(SourceGroup group) {
            return PackageView.createPackageView(group);
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (NbMavenProjectImpl.PROP_PROJECT.equals(evt.getPropertyName())) {
                fireChange();
            }
        }
        
        @Override
        public void addNotify() {
            Sources srcs = ProjectUtils.getSources(project);
            srcs.addChangeListener(this);
        }
        
        @Override
        public void removeNotify() {
            Sources srcs = ProjectUtils.getSources(project);
            srcs.removeChangeListener(this);
        }

        public void stateChanged(ChangeEvent arg0) {
            fireChange();
        }
    }
}
