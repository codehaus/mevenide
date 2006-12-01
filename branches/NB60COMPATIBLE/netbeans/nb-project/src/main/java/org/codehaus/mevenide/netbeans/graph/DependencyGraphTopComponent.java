/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.visual.widget.Scene.SceneListener;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * component showing graph of dependencies for a pom project.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class DependencyGraphTopComponent extends TopComponent {
    public static final String ATTRIBUTE_DEPENDENCIES_LAYOUT = "MavenProjectDependenciesLayout"; //NOI18N
    
    private NbMavenProject project;
    private JComponent view = null;
    private DependencyGraphScene scene;
    
    /** Creates new form ModulesGraphTopComponent */
    public DependencyGraphTopComponent(NbMavenProject proj) {
        initComponents();
        project = proj;
        setName("DependencyGraph" + proj.getName());
        setDisplayName("Dependencies - " + proj.getDisplayName());
    }
    
    
    
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    
    protected void componentOpened() {
        super.componentOpened();
//        controller = new DefaultViewController();
        scene = GraphDocumentFactory.createDependencyDocument(project);
        JComponent sceneView = scene.getView ();
        if (sceneView == null) {
            sceneView = scene.createView ();
        }
        final JScrollPane pane = new JScrollPane();
        pane.setViewportView(sceneView);
        add(pane, BorderLayout.CENTER);
        scene.cleanLayout();
        
        scene.addSceneListener(new SceneListener() {
            public void sceneRepaint() {
            }
            public void sceneValidating() {
            }
            public void sceneValidated() {
//                for (ArtifactGraphNode nd : scene.getNodes()) {
//                    Widget wid = scene.findWidget(nd);
//                    Rectangle sceneBounds = wid.convertLocalToScene(wid.getBounds());
//                    if (rec == null) {
//                        rec = sceneBounds;
//                    } else {
//                        rec.union(sceneBounds);
//                    }
//                }
//                if (rec == null)
//                    rec = new Rectangle ();
//                rec.grow(rec.width / 10, rec.height /10);
//                System.out.println("max rect=" + rec);
//                
                ArtifactGraphNode root = scene.getRootArtifact();
                Widget rootWidget = scene.findWidget (root);
                Rectangle rootSceneBounds = rootWidget.convertLocalToScene(rootWidget.getBounds());
                System.out.println("view bounds to scroll0-" + rootWidget.getBounds());
                System.out.println("view bounds to scroll1-" + rootSceneBounds);
                Rectangle rootViewBounds = scene.convertSceneToView (rootSceneBounds);
                scene.getView().scrollRectToVisible(rootViewBounds); 
                System.out.println("view bounds to scroll2-" + rootViewBounds);

////                Rectangle rec = scene.convertSceneToView(scene.getBounds());
////                Rectangle viewBounds = scene.convertSceneToView (rec);
////                Dimension viewportSize = pane.getViewport().getSize();
////                System.out.println("view size=" + viewportSize);
////                System.out.println("scene bounds=" + viewBounds);
////                float zoomFactor = Math.max ((float) viewportSize.width / viewBounds.width,
////                                             (float) viewportSize.height / viewBounds.height);                
////                
////                System.out.println("zoom factor is " + zoomFactor);
////                scene.setZoomFactor(zoomFactor);
//////                scene.getSceneAnimator().animateZoomFactor(zoomFactor);
                scene.removeSceneListener (this);                
            }
        });
        revalidate();
        repaint();
        
        
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnBigger = new javax.swing.JButton();
        btnSmaller = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnBigger.setIcon(new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/zoomin.gif")));
        btnBigger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBiggerActionPerformed(evt);
            }
        });
        jPanel1.add(btnBigger);

        btnSmaller.setIcon(new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/zoomout.gif")));
        btnSmaller.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSmallerActionPerformed(evt);
            }
        });
        jPanel1.add(btnSmaller);

        add(jPanel1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnSmallerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSmallerActionPerformed
        scene.setZoomFactor(scene.getZoomFactor() * 0.75);
        revalidate();
        repaint();
//        GraphFactory.setZoom(view, zoom);
    }//GEN-LAST:event_btnSmallerActionPerformed
    
    private void btnBiggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBiggerActionPerformed
        scene.setZoomFactor(scene.getZoomFactor() * 1.5);
        revalidate();
        repaint();
//        GraphFactory.setZoom(view, zoom);
// TODO add your handling code here:
    }//GEN-LAST:event_btnBiggerActionPerformed

//    private void loadDocument() {
//        VMDSerializer ser = new VMDSerializer();
//        FileObject fo = project.getProjectDirectory();
//        String attrVal = (String)fo.getAttribute(ATTRIBUTE_DEPENDENCIES_LAYOUT);
//        if (attrVal != null) {
//            try {
//            Reader str = new StringReader(attrVal);
//            Document doc = XMLUtil.parse(new InputSource(str),false, false, null, null);
//            Node nd = doc.getDocumentElement().getFirstChild();
//            while (nd != null && !(nd instanceof Element)) {
//                nd = nd.getNextSibling();
//            }
//            if (nd == null) {
//                System.out.println("errror...");
//            } else {
//                ser.loadStructure(nd);
//                ser.useStructure(controller.getHelper());
//            }
//            } catch (FileNotFoundException ex) {
//                ex.printStackTrace();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            } catch (SAXException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBigger;
    private javax.swing.JButton btnSmaller;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
    
}
