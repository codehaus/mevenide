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
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.undo.UndoableEdit;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.graph.api.GraphFactory;
import org.netbeans.graph.api.IGraphEventHandler;
import org.netbeans.graph.api.control.builtin.DefaultViewController;
import org.netbeans.graph.api.model.GraphEvent;
import org.netbeans.graph.api.model.IGraphDocument;
import org.netbeans.graph.api.model.IGraphLink;
import org.netbeans.graph.api.model.IGraphNode;
import org.netbeans.graph.api.model.IGraphPort;
import org.netbeans.graph.api.model.ability.IDirectionable;
import org.netbeans.graph.api.model.builtin.GraphDocument;
import org.netbeans.graph.api.model.builtin.GraphLink;
import org.netbeans.graph.api.model.builtin.GraphNode;
import org.netbeans.graph.api.model.builtin.GraphPort;
import org.netbeans.graph.vmd.VMDDocumentRenderer;
import org.netbeans.graph.vmd.VMDOrderingLogic;
import org.netbeans.graph.vmd.VMDSerializer;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * component showing graph of modules for a pom project.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class ModulesGraphTopComponent extends TopComponent {
    
    private NbMavenProject project;
    private JComponent view = null;
    private DefaultViewController controller;
    private MyGraphEventHandler handler;
    private float zoom = 1;
    
    /** Creates new form ModulesGraphTopComponent */
    public ModulesGraphTopComponent(NbMavenProject proj) {
        initComponents();
        project = proj;
        setName("Module Graph");
        setDisplayName("Module Graph - " + proj.getDisplayName());
    }
    
    private Collection loadModules(NbMavenProject prj) {
        Collection modules = new ArrayList();
        File base = prj.getOriginalMavenProject().getBasedir();
        for (Iterator it = prj.getOriginalMavenProject().getModules().iterator(); it.hasNext();) {
            String elem = (String) it.next();
            File projDir = FileUtil.normalizeFile(new File(base, elem));
            FileObject fo = FileUtil.toFileObject(projDir);
            if (fo != null) {
                try {
                    Project childproj = ProjectManager.getDefault().findProject(fo);
                    if (childproj instanceof NbMavenProject) {
                        modules.add(childproj);
                    }
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                //TODO broken module reference.. show as such..
            }
        }
        return modules;
    }
    
    private GraphDocument createDocument() {
        GraphDocument doc = new GraphDocument();
        GraphNode parentnode = new ProjectGraphNode(project);
        doc.addComponents(GraphEvent.createSingle(parentnode));
        GraphPort parentport = new GraphPort();
        parentport.setSource(true);
        parentport.setDirection(IDirectionable.RIGHT);
        parentport.setPreferredOrderPosition(new Integer(IDirectionable.RIGHT));
        parentnode.addPort(parentport);
        parentnode.setDefaultPort(parentport);
        createSubnodes(doc, parentport, project);
        return doc;
    }
    
    private void createSubnodes(final GraphDocument doc, final GraphPort parentport, NbMavenProject prj) {
        Iterator it = loadModules(prj).iterator();
        while (it.hasNext()) {
            NbMavenProject proj = (NbMavenProject)it.next();
            GraphNode node = new ProjectGraphNode(proj);
            doc.addComponents(GraphEvent.createSingle(node));
            GraphPort port = new GraphPort();
            port.setTarget(true);
            port.setDirection(IDirectionable.LEFT);
            // WTF is the order position and how it's calculated???
            port.setPreferredOrderPosition(new Integer(IDirectionable.LEFT + 8));
            node.addPort(port);
            GraphLink link = new GraphLink();
            link.setSourcePort(parentport);
            link.setTargetPort(port);
            doc.addComponents(GraphEvent.createSingle(link));
            if ("pom".equalsIgnoreCase(proj.getOriginalMavenProject().getPackaging())) {
                GraphPort myparent = new GraphPort();
                myparent.setSource(true);
                myparent.setDirection(IDirectionable.RIGHT);
                myparent.setPreferredOrderPosition(new Integer(IDirectionable.RIGHT));
                node.addPort(myparent);
                node.setDefaultPort(myparent);
                createSubnodes(doc, myparent, proj);
            }
        }
    }
    
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    
    protected void componentOpened() {
        super.componentOpened();
        controller = new MyGraphController();
        GraphDocument doc = createDocument();
        handler = new MyGraphEventHandler(doc);
        view = GraphFactory.createView(doc,
                new VMDDocumentRenderer(),
                controller,
                handler);
        GraphFactory.layoutNodes(view);
        FileObject fo = project.getProjectDirectory().getFileObject("modules-graph.xml");
        if (fo != null) {
            loadDocument(fo);
        }
        JScrollPane pane = new JScrollPane();
        pane.setViewportView(view);
        add(pane, BorderLayout.CENTER);
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
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jButton1.setText("+");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel1.add(jButton1);

        jButton2.setText("-");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel1.add(jButton2);

        jButton3.setText("Save layout");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel1.add(jButton3);

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Select With Modules");
        jCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBox1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox1StateChanged(evt);
            }
        });

        jPanel1.add(jCheckBox1);

        add(jPanel1, java.awt.BorderLayout.NORTH);

    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox1StateChanged
        handler.setMultiSelect(jCheckBox1.isSelected());
    }//GEN-LAST:event_jCheckBox1StateChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        Document doc = XMLUtil.createDocument("moduleLayout", null, null, null);
        VMDSerializer ser = new VMDSerializer();
        ser.createStructure(controller.getHelper());
        Node nd = ser.saveStructure(doc, "layout");
        doc.getDocumentElement().appendChild(nd);
        FileObject fo = project.getProjectDirectory().getFileObject("modules-graph.xml");
        FileLock lock = null;
        OutputStream str = null;
        try {
            if (fo == null) {
                fo = project.getProjectDirectory().createData("modules-graph.xml");
            }
            lock = fo.lock();
            str = fo.getOutputStream(lock);
            XMLUtil.write(doc, str, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (str != null) {
                try {
                str.close();
                } catch (IOException exc) {
                    
                }
            }
            if (lock != null) {
                lock.releaseLock();
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        zoom = zoom * (float)0.75;
        GraphFactory.setZoom(view, zoom);
    }//GEN-LAST:event_jButton2ActionPerformed
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        zoom = zoom * (float)1.5;
        GraphFactory.setZoom(view, zoom);
// TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void loadDocument(FileObject fo) {
        VMDSerializer ser = new VMDSerializer();
        InputStream str = null;
        try {
            str = fo.getInputStream();
            Document doc = XMLUtil.parse(new InputSource(str),false, false, null, null);
            Node nd = doc.getDocumentElement().getFirstChild();
            while (nd != null && !(nd instanceof Element)) {
                nd = nd.getNextSibling();
            }
            if (nd == null) {
                System.out.println("errror...");
            } else {
                ser.loadStructure(nd);
                ser.useStructure(controller.getHelper());
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } finally {
            if (str != null) {
                try {
                    str.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
    
}
