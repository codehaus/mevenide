/*
 * SourcesNodeFactory.java
 *
 * Created on November 6, 2006, 3:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.nodes;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
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
        NbMavenProject prj = project.getLookup().lookup(NbMavenProject.class);
        return  new NList(prj);
    }
    
    private static class NList extends AbstractMavenNodeList<SourceGroup> implements PropertyChangeListener {
        private NbMavenProject project;
        private NList(NbMavenProject prj) {
            project = prj;
        }
        
        public List<SourceGroup> keys() {
            List<SourceGroup> list = new ArrayList<SourceGroup>();
            Sources srcs = project.getLookup().lookup(Sources.class);
            if (srcs == null) {
                throw new IllegalStateException("need Sources instance in lookup");
            }
            SourceGroup[] javagroup = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            for (int i = 0; i < javagroup.length; i++) {
                list.add(javagroup[i]);
            }
            SourceGroup[] gengroup = srcs.getSourceGroups(MavenSourcesImpl.TYPE_GEN_SOURCES);
            for (int i = 0; i < gengroup.length; i++) {
                list.add(gengroup[i]);
            }
            return list;
        }
        
        public Node node(SourceGroup group) {
            return PackageView.createPackageView(group);
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                fireChange();
            }
        }
        
        public void addNotify() {
            project.addPropertyChangeListener(this);
            
        }
        
        public void removeNotify() {
            project.removePropertyChangeListener(this);
        }
    }
}
