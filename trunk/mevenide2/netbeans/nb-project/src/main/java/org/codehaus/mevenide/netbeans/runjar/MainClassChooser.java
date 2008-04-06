/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.codehaus.mevenide.netbeans.runjar;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import javax.lang.model.element.TypeElement;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.SourceUtils;
import org.openide.awt.MouseUtils;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/** Browses and allows to choose a project's main class.
 *
 * @author  Jiri Rechtacek
 */
class MainClassChooser extends JPanel {

    private ChangeListener changeListener;
    private Collection<ElementHandle<TypeElement>> possibleMainClasses;
            
    /** Creates new form MainClassChooser */
    MainClassChooser (FileObject... sourcesRoots) {
        initComponents();
        jMainClassList.setCellRenderer(new MainClassRenderer());
        initClassesView (sourcesRoots);
    }
    
    private void initClassesView (final FileObject... sourcesRoots) {
        possibleMainClasses = null;
        jMainClassList.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
        jMainClassList.setListData (getWarmupList ());
        jMainClassList.addListSelectionListener (new ListSelectionListener () {
            public void valueChanged (ListSelectionEvent evt) {
                if (changeListener != null) {
                    changeListener.stateChanged (new ChangeEvent (evt));
                }
            }
        });
        // support for double click to finish dialog with selected class
        jMainClassList.addMouseListener (new MouseListener () {
            public void mouseClicked (MouseEvent e) {
                if (MouseUtils.isDoubleClick (e)) {
                    if (getSelectedMainClass () != null) {
                        if (changeListener != null) {
                            changeListener.stateChanged (new ChangeEvent (e));
                        }
                    }
                }
            }
            public void mousePressed (MouseEvent e) {}
            public void mouseReleased (MouseEvent e) {}
            public void mouseEntered (MouseEvent e) {}
            public void mouseExited (MouseEvent e) {}
        });
        
        RequestProcessor.getDefault ().post (new Runnable () {
            public void run () {
                
                possibleMainClasses = SourceUtils.getMainClasses(sourcesRoots);
                if (possibleMainClasses.isEmpty ()) {                    
                    SwingUtilities.invokeLater( new Runnable () {
                        public void run () {
                            jMainClassList.setListData (new String[] { NbBundle.getMessage (MainClassChooser.class, "LBL_ChooseMainClass_NO_CLASSES_NODE") } ); // NOI18N
                        }
                    });                    
                } else {
                    final ElementHandle<TypeElement>[] arr = possibleMainClasses.toArray(new ElementHandle[possibleMainClasses.size()]);
                    // #46861, sort name of classes
                    Arrays.sort (arr, new MainClassComparator());
                    SwingUtilities.invokeLater(new Runnable () {
                        public void run () {
                            jMainClassList.setListData (arr);
                            jMainClassList.setSelectedIndex (0);
                        }
                    });                    
                }
            }
        });
        
    }
    
    private Object[] getWarmupList () {        
          return new Object[] {NbBundle.getMessage (MainClassChooser.class, "LBL_ChooseMainClass_WARMUP_MESSAGE")}; //NOI18N
    }
    
    private boolean isValidMainClassName (Object value) {
        return (possibleMainClasses != null) && (possibleMainClasses.contains (value));
    }


    /** Returns the selected main class.
     *
     * @return name of class or null if no class with the main method is selected
     */    
    String getSelectedMainClass () {
        if (isValidMainClassName (jMainClassList.getSelectedValue ())) {
            return ((ElementHandle)jMainClassList.getSelectedValue()).getQualifiedName();
        } else {
            return null;
        }
    }
    
    
    
    public void addChangeListener (ChangeListener l) {
        changeListener = l;
    }
    
    public void removeChangeListener (ChangeListener l) {
        changeListener = null;
    }
    
    // Used only from unit tests to suppress check of main method. If value
    // is different from null it will be returned instead.
    public static Boolean unitTestingSupport_hasMainMethodResult = null;
    
    /** Checks if given file object contains the main method.
     *
     * @param classFO file object represents java 
     * @return false if parameter is null or doesn't contain SourceCookie
     * or SourceCookie doesn't contain the main method
     */    
    public static boolean hasMainMethod (FileObject fo) {
      // support for unit testing
        if (MainClassChooser.unitTestingSupport_hasMainMethodResult != null) {
            return MainClassChooser.unitTestingSupport_hasMainMethodResult.booleanValue ();
        }
        if (fo == null) {
            // ??? maybe better should be thrown IAE
            return false;
        }
        return !SourceUtils.getMainClasses(fo).isEmpty();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        lblMainClass = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jMainClassList = new javax.swing.JList();
        rbOnce = new javax.swing.JRadioButton();
        rbSession = new javax.swing.JRadioButton();
        rbPermanent = new javax.swing.JRadioButton();

        setPreferredSize(new java.awt.Dimension(380, 300));

        lblMainClass.setLabelFor(jMainClassList);
        org.openide.awt.Mnemonics.setLocalizedText(lblMainClass, org.openide.util.NbBundle.getMessage(MainClassChooser.class, "CTL_AvaialableMainClasses")); // NOI18N

        jScrollPane1.setMinimumSize(new java.awt.Dimension(100, 200));
        jScrollPane1.setViewportView(jMainClassList);
        jMainClassList.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MainClassChooser.class, "AD_jMainClassList")); // NOI18N

        buttonGroup1.add(rbOnce);
        org.openide.awt.Mnemonics.setLocalizedText(rbOnce, org.openide.util.NbBundle.getMessage(MainClassChooser.class, "MainClassChooser.rbOnce.text")); // NOI18N

        buttonGroup1.add(rbSession);
        rbSession.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(rbSession, org.openide.util.NbBundle.getMessage(MainClassChooser.class, "MainClassChooser.rbSession.text")); // NOI18N

        buttonGroup1.add(rbPermanent);
        org.openide.awt.Mnemonics.setLocalizedText(rbPermanent, org.openide.util.NbBundle.getMessage(MainClassChooser.class, "MainClassChooser.rbPermanent.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                    .add(rbPermanent)
                    .add(rbSession)
                    .add(rbOnce)
                    .add(lblMainClass))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblMainClass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 173, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(rbOnce)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbSession)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbPermanent)
                .addContainerGap())
        );

        lblMainClass.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MainClassChooser.class, "AD_jMainClassList")); // NOI18N

        getAccessibleContext().setAccessibleDescription("null");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JList jMainClassList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMainClass;
    javax.swing.JRadioButton rbOnce;
    javax.swing.JRadioButton rbPermanent;
    javax.swing.JRadioButton rbSession;
    // End of variables declaration//GEN-END:variables


    private static final class MainClassRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String displayName;
            if (value instanceof String) {
                displayName = (String) value;
            } if (value instanceof ElementHandle) {
                displayName = ((ElementHandle)value).getQualifiedName();
            } else {
                displayName = value.toString ();
            }
            return super.getListCellRendererComponent (list, displayName, index, isSelected, cellHasFocus);
        }
    }
    
    private static class MainClassComparator implements Comparator<ElementHandle> {
            
        public int compare(ElementHandle arg0, ElementHandle arg1) {
            return arg0.getQualifiedName().compareTo(arg1.getQualifiedName());
        }
    }

}
