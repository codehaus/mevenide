/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

package org.mevenide.ui.netbeans.creator;

import java.util.Calendar;
import javax.swing.JPanel;
import org.apache.maven.project.Project;
import org.openide.util.NbBundle;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class DescPanel extends JPanel implements ProjectPanel
{
    private boolean propagate;
    private ProjectValidateObserver valObserver;
    
    /** Creates new form BasicsPanel */
    public DescPanel(boolean propagateImmediately)
    {
        initComponents();
        valObserver = null;
        propagate = propagateImmediately;
        //TODO add listeners for immediatePropagation stuff.
        setName(NbBundle.getMessage(DescPanel.class, "DescPanel.name"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        java.awt.GridBagConstraints gridBagConstraints;

        lblUrl = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        lblShortDescription = new javax.swing.JLabel();
        txtShortDescription = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        spDescription = new javax.swing.JScrollPane();
        taDescription = new javax.swing.JTextArea();
        lblInceptionYear = new javax.swing.JLabel();
        txtInceptionYear = new javax.swing.JTextField();
        lblLogo = new javax.swing.JLabel();
        txtLogo = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        lblUrl.setLabelFor(txtUrl);
        lblUrl.setText(org.openide.util.NbBundle.getMessage(DescPanel.class, "DescPanel.lblUrl.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblUrl, gridBagConstraints);

        txtUrl.setMinimumSize(new java.awt.Dimension(50, 26));
        txtUrl.setPreferredSize(new java.awt.Dimension(50, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        add(txtUrl, gridBagConstraints);

        lblShortDescription.setLabelFor(txtShortDescription);
        lblShortDescription.setText(org.openide.util.NbBundle.getMessage(DescPanel.class, "DescPanel.lblShortDescription.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblShortDescription, gridBagConstraints);

        txtShortDescription.setMinimumSize(new java.awt.Dimension(100, 26));
        txtShortDescription.setPreferredSize(new java.awt.Dimension(100, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(txtShortDescription, gridBagConstraints);

        lblDescription.setLabelFor(taDescription);
        lblDescription.setText(org.openide.util.NbBundle.getMessage(DescPanel.class, "DescPanel.lblDescription.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblDescription, gridBagConstraints);

        taDescription.setMinimumSize(new java.awt.Dimension(200, 100));
        spDescription.setViewportView(taDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.1;
        add(spDescription, gridBagConstraints);

        lblInceptionYear.setLabelFor(txtInceptionYear);
        lblInceptionYear.setText(org.openide.util.NbBundle.getMessage(DescPanel.class, "DescPanel.lblInceptionYear.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblInceptionYear, gridBagConstraints);

        txtInceptionYear.setMinimumSize(new java.awt.Dimension(50, 28));
        txtInceptionYear.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        add(txtInceptionYear, gridBagConstraints);

        lblLogo.setLabelFor(txtLogo);
        lblLogo.setText(org.openide.util.NbBundle.getMessage(DescPanel.class, "DescPanel.lblLogo.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblLogo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 3, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(txtLogo, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblInceptionYear;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblShortDescription;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JScrollPane spDescription;
    private javax.swing.JTextArea taDescription;
    private javax.swing.JTextField txtInceptionYear;
    private javax.swing.JTextField txtLogo;
    private javax.swing.JTextField txtShortDescription;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables
    
    public void setProject(Project project)
    {
        txtInceptionYear.setText(project.getInceptionYear() == null ? "" + Calendar.getInstance().get(Calendar.YEAR) : project.getInceptionYear());
        txtShortDescription.setText(project.getShortDescription() == null ? "" : project.getShortDescription());
        txtUrl.setText(project.getUrl() == null ? "http://" : project.getUrl());
        txtLogo.setText(project.getLogo() == null ? "" : project.getLogo());
        taDescription.setText(project.getDescription() == null ? "" : project.getDescription());
    }
    
    public Project copyProject(Project project)
    {
        project.setDescription(taDescription.getText());
        project.setShortDescription(txtShortDescription.getText());
        project.setInceptionYear(txtInceptionYear.getText());
        project.setUrl(txtUrl.getText());
        project.setLogo(txtLogo.getText());
        return project;
    }
    
    public boolean isInValidState()
    {
        //TODO some checks.
        return true;
    }
    
    public void setValidateObserver(ProjectValidateObserver observer)
    {
        valObserver = observer;
    }
    
    public String getValidityMessage()
    {
        return "";
    }
    
}
