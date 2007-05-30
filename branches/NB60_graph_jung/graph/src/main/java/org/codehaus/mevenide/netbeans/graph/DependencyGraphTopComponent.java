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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * component showing graph of dependencies for project.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class DependencyGraphTopComponent extends TopComponent {
//    public static final String ATTRIBUTE_DEPENDENCIES_LAYOUT = "MavenProjectDependenciesLayout"; //NOI18N
    
    private NbMavenProject project;
    private DependencyGraphScene scene;
    private Timer timer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
            checkFindValue();
        }
    });
    
    /** Creates new form ModulesGraphTopComponent */
    public DependencyGraphTopComponent(NbMavenProject proj) {
        initComponents();
        project = proj;
        setName("DependencyGraph" + proj.getName());
        setDisplayName("Dependencies - " + proj.getDisplayName());
        timer.setDelay(1000);
        timer.setRepeats(false);
        txtFind.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent arg0) {
                timer.restart();
            }

            public void removeUpdate(DocumentEvent arg0) {
                timer.restart();
            }

            public void changedUpdate(DocumentEvent arg0) {
                timer.restart();
            }
        });
    }
    
    private void checkFindValue() {
        String val = txtFind.getText().trim();
        if ("".equals(val)) {
            scene.clearFind();
        } else {
            scene.findNodeByText(val);
        }
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    
    protected void componentOpened() {
        super.componentOpened();
        final JScrollPane pane = new JScrollPane();
        add(pane, BorderLayout.CENTER);
        JLabel lbl = new JLabel("Loading...");
        lbl.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        pane.setViewportView(lbl);
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                scene = GraphDocumentFactory.createDependencyDocument(project);
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            JComponent sceneView = scene.getView ();
                            if (sceneView == null) {
                                sceneView = scene.createView ();
                            }
                            pane.setViewportView(sceneView);
                        }
                    });
                } catch (Exception e) {
                    
                }
                scene.cleanLayout();
                centerView(pane);
            }
        });
    }
    
    private void centerView(final JScrollPane pane) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Rectangle rectangle = new Rectangle (0, 0, 1, 1);
                for (ArtifactGraphNode node : scene.getNodes()) {
                    Widget widget = scene.findWidget(node);
                    rectangle = rectangle.union (widget.convertLocalToScene (widget.getBounds ()));
                }
                Dimension dim = rectangle.getSize ();
                Dimension viewDim = pane.getViewportBorderBounds ().getSize ();
                scene.setZoomFactor (Math.min ((float) viewDim.width / dim.width, (float) viewDim.height / dim.height));
                
                
        txtForce.setText("" + scene.layout.forceMultiplier);
        txtRange.setText("" + scene.layout.repulsionRange);
        txtStretch.setText("" + scene.layout.stretch);
        txtStretch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("XXX");
                double dbl = Double.parseDouble(txtStretch.getText());
                scene.layout.stretch = dbl;
                scene.cleanLayout();
            }
        });
        txtRange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("XXX");
                double dbl = Double.parseDouble(txtRange.getText());
                scene.layout.repulsionRange = dbl;
                scene.cleanLayout();
            }
        });
        txtForce.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("XXX");
                double dbl = Double.parseDouble(txtForce.getText());
                scene.layout.forceMultiplier = dbl;
                scene.cleanLayout();
            }
        });
                
            }
        });
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
        lblFind = new javax.swing.JLabel();
        txtFind = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtForce = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtStretch = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtRange = new javax.swing.JTextField();

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

        lblFind.setText("Find:");
        jPanel1.add(lblFind);

        txtFind.setMinimumSize(new java.awt.Dimension(100, 19));
        txtFind.setPreferredSize(new java.awt.Dimension(150, 19));
        jPanel1.add(txtFind);

        jLabel1.setText("force");
        jPanel1.add(jLabel1);

        txtForce.setPreferredSize(new java.awt.Dimension(40, 19));
        jPanel1.add(txtForce);

        jLabel2.setText("stretch");
        jPanel1.add(jLabel2);

        txtStretch.setPreferredSize(new java.awt.Dimension(40, 19));
        jPanel1.add(txtStretch);

        jLabel3.setText("repuls range");
        jPanel1.add(jLabel3);

        txtRange.setPreferredSize(new java.awt.Dimension(40, 19));
        jPanel1.add(txtRange);

        add(jPanel1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnSmallerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSmallerActionPerformed
        scene.setZoomFactor(scene.getZoomFactor() * 0.8);
        revalidate();
        repaint();
    }//GEN-LAST:event_btnSmallerActionPerformed
    
    private void btnBiggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBiggerActionPerformed
        scene.setZoomFactor(scene.getZoomFactor() * 1.2);
        revalidate();
        repaint();
    }//GEN-LAST:event_btnBiggerActionPerformed

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBigger;
    private javax.swing.JButton btnSmaller;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblFind;
    private javax.swing.JTextField txtFind;
    private javax.swing.JTextField txtForce;
    private javax.swing.JTextField txtRange;
    private javax.swing.JTextField txtStretch;
    // End of variables declaration//GEN-END:variables
    
}
