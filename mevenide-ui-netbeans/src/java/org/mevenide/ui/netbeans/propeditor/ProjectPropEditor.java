/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Milos Kleint (ca206216@tiscali.cz).  All rights
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

package org.mevenide.ui.netbeans.propeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ClassNotFoundException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.ui.netbeans.MavenPropertyFiles;
import org.openide.ErrorManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerPanel;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Workspace;

/**
 *
 * @author  cenda
 */
public class ProjectPropEditor extends TopComponent implements ExplorerManager.Provider
{
    private static Log log = LogFactory.getLog(ProjectPropEditor.class);
    
   // REMEMBER: You should have a public default constructor!
    // This is for externalization. If you have a nondefault
    // constructor for normal creation of the component, leave
    // in a default constructor that will put the component into
    // a consistent but unconfigured state, and make sure readExternal
    // initializes it properly. Or, be creative with writeReplace().
    private ExplorerManager manager;
    /** Creates new form ProjectPropEditor */
    public ProjectPropEditor()
    {
        init();
    }
    
    public ProjectPropEditor(DataObject obj)
    {
        super(obj);
        init();
    }
    
    private void init()
    {
        initComponents();
        setCloseOperation(CLOSE_LAST); // or CLOSE_EACH
        // Display name of this window (not needed if you use the DataObject constructor):
        setName(NbBundle.getMessage(ProjectPropEditor.class, "ProjectPropEditor.title"));
        // You may set the icon, but often it is better to set the icon for an associated mode instead:
         setIcon(Utilities.loadImage("org/apache/maven/nb/resources/MavenIcon.gif", true));
        // Use the Component Inspector to set tool-tip text. This will be saved
        // automatically. Other JComponent properties you may need to save yuorself.
        // At any time you can affect the node selection:
        // setActivatedNodes(new Node[] {...});
         putClientProperty("PersistenceType","Never"); //NOI18N
         manager = new ExplorerManager();     
//         cbShowAll.addActionListener(new ActionListener() {
//             public void actionPerformed(ActionEvent event)
//             {
//                 loadAllDefinedProperties();
//             }
//         });
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.mevenide.ui.netbeans");
    }
    
    /*
    // If you are using CloneableTopComponent, probably you should override:
    protected CloneableTopComponent createClonedObject() {
        return new ProjectPropTC();
    }
    protected boolean closeLast() {
        // You might want to prompt the user first and maybe return false:
        return true;
    }
     */
    
    // APPEARANCE
     
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        java.awt.GridBagConstraints gridBagConstraints;

        lblProject = new javax.swing.JLabel();
        lblProjectBuild = new javax.swing.JLabel();
        lblUserBuild = new javax.swing.JLabel();
        txtProject = new javax.swing.JTextField();
        txtProjectBuild = new javax.swing.JTextField();
        txtUserBuild = new javax.swing.JTextField();
        ttvProps = new org.openide.explorer.view.TreeTableView();

        setLayout(new java.awt.GridBagLayout());

        lblProject.setLabelFor(txtProject);
        lblProject.setText(org.openide.util.NbBundle.getBundle(ProjectPropEditor.class).getString("ProjectPropEditor.lblProject.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 11, 0, 0);
        add(lblProject, gridBagConstraints);

        lblProjectBuild.setLabelFor(txtProjectBuild);
        lblProjectBuild.setText(org.openide.util.NbBundle.getBundle(ProjectPropEditor.class).getString("ProjectPropEditor.lblProjectBuild.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 11, 0, 0);
        add(lblProjectBuild, gridBagConstraints);

        lblUserBuild.setLabelFor(txtUserBuild);
        lblUserBuild.setText(org.openide.util.NbBundle.getBundle(ProjectPropEditor.class).getString("ProjectPropEditor.lblUserBuild.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 11, 0, 0);
        add(lblUserBuild, gridBagConstraints);

        txtProject.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 12);
        add(txtProject, gridBagConstraints);

        txtProjectBuild.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 12);
        add(txtProjectBuild, gridBagConstraints);

        txtUserBuild.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 12);
        add(txtUserBuild, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(ttvProps, gridBagConstraints);

    }//GEN-END:initComponents
   
    // MODES AND WORKSPACES
    

    // If you want it to open in a specific mode:
    public void open(Workspace ws) {
        if (ws == null) ws = WindowManager.getDefault().getCurrentWorkspace();
        Mode m = ws.findMode("editor");
        if (m != null) 
        {
            m.dockInto(this);
        }
        
        MavenPropertyFiles propFiles = (MavenPropertyFiles)getLookup().lookup(MavenPropertyFiles.class);
        showFilesLocations(propFiles);
        setupTreeTable();
        loadProperties(propFiles);
        super.open(ws);
    }
    
    private void showFilesLocations(MavenPropertyFiles propFiles)
    {
        final String display1 =propFiles.getProjectPropFile() == null ? "<Not defined>" : propFiles.getProjectPropFile().getAbsolutePath();
        final String display2 =propFiles.getProjectBuildFile() == null ? "<Not defined>" : propFiles.getProjectBuildFile().getAbsolutePath();
        final String display3 =propFiles.getUserBuildFile() == null ? "<Not defined>" : propFiles.getUserBuildFile().getAbsolutePath();

        if (SwingUtilities.isEventDispatchThread())
        {
            txtProject.setText(display1);
            txtProjectBuild.setText(display2);
            txtUserBuild.setText(display3);
        } else
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    txtProject.setText(display1);
                    txtProjectBuild.setText(display2);
                    txtUserBuild.setText(display3);
                }
            });
        }
    }
    
    private void setupTreeTable()
    {
        Node.Property[] props = new Node.Property[]
        {
            new DummyHeaderNodeProp("name", String.class, "Property Name", "Desc", true, false),
            new DummyHeaderNodeProp("value", String.class, "Property Value", "Desc", true, false),
            new DummyHeaderNodeProp("location", String.class, "PropertyLocation", "Desc", true, false)
        };
        props[0].setValue("TreeColumnTTV", Boolean.TRUE); //NOI18N
        props[0].setValue("SortingColumnTTV", Boolean.TRUE); //NOI18N
        props[0].setValue("ComparableColumnTTV", Boolean.TRUE); //NOI18N
        props[1].setValue("ComparableColumnTTV", Boolean.TRUE); //NOI18N
        props[2].setValue("ComparableColumnTTV", Boolean.TRUE); //NOI18N
        ttvProps.setProperties(props);
        ttvProps.setTreePreferredWidth(150);
        ttvProps.setTableColumnPreferredWidth(1, 50);
        ttvProps.setTableColumnPreferredWidth(0, 200);
        ttvProps.setRootVisible(false);
        Node root = new AbstractNode(Children.LEAF);
        root.setName("Property Name");
        getExplorerManager().setRootContext(root);
    }
    
    private void loadProperties(final MavenPropertyFiles files)
    {
        RequestProcessor.getDefault().post(new Runnable()
        {
            public void run() {
                doLoadProperties(files);
            }
        });
    }
    
    private void doLoadProperties(MavenPropertyFiles files)
    {
        InputStream str1 = null;
        InputStream str2 = null;
        InputStream str3 = null;
        try {
            str1 = files.getUserBuildFile() == null ? null : new FileInputStream(files.getUserBuildFile());
        } catch (FileNotFoundException exc) {
            ErrorManager.getDefault().notify(exc);
            str1 = null;
        }
        try {
            str2 = files.getProjectBuildFile() == null ? null : new FileInputStream(files.getProjectBuildFile());
        } catch (FileNotFoundException exc) {
            ErrorManager.getDefault().notify(exc);
            str2 = null;
        }
        try {
            str3 = files.getProjectPropFile() == null ? null : new FileInputStream(files.getProjectPropFile());
        } catch (FileNotFoundException exc) {
            ErrorManager.getDefault().notify(exc);
            str3 = null;
        }
        try {
            PropReader reader = new PropReaderImpl(MavenPropertyFiles.PROP_USER_BUILD, str1, 
                                    new PropReaderImpl(MavenPropertyFiles.PROP_PROJECT_BUILD, str2,
                                        new PropReaderImpl(MavenPropertyFiles.PROP_PROJECT, str3, null)));
            Children childs = new Children.Array();
            childs.add(reader.createNodeStructure());
            AbstractNode root = new AbstractNode(childs);
            root.setName("Property name");
            getExplorerManager().setRootContext(root);
        } catch (IOException exc)
        {
            ErrorManager.getDefault().notify(exc);
            log.error("IO Error while reading property files", exc);
        }
    }
    
//    private void loadAllDefinedProperties()
//    {
//        final MavenPropertyFiles files = (MavenPropertyFiles)getLookup().lookup(MavenPropertyFiles.class);
//        RequestProcessor.getDefault().post(new Runnable()
//        {
//            public void run() {
//                doLoadAllDefinedProperties(files);
//            }
//        });
//    }
//    
//    private void doLoadAllDefinedProperties(MavenPropertyFiles files)
//    {
//        
//    }
    
    public ExplorerManager getExplorerManager()
    {
        return manager;
    }
    
    /*
    // If you are not specifying a mode you may wish to use:
    public Dimension getPreferredSize() {
        return ...WindowManager.getDefault().getCurrentWorkspace().getBounds()...;
    }
     */
    
    /*
    // If you want it to open on a specific workspace:
    public static final String ProjectPropTC_WORKSPACE = NbBundle.getMessage(ProjectPropTC.class, "LBL_workspace_name");
    public void open() {
        WindowManager wm = WindowManager.getDefault();
        Workspace ws = wm.findWorkspace(ProjectPropTC_WORKSPACE);
        if (ws == null)
            ws = wm.createWorkspace(ProjectPropTC_WORKSPACE);
        open(ws);
        ws.activate();
    }
     */
    
    // PERSISTENCE
    
    private static final long serialVersionUID = 14746564647462111L;
    
//    // If you wish to keep any state between IDE restarts, put it here:
//    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//        super.readExternal(in);
//        String path = (String)in.readObject();
//        setProjectBuildFile(new File(path));
//    }
//    public void writeExternal(ObjectOutput out) throws IOException {
//        super.writeExternal(out);
//        if (getProjectBuildFile() != null) {
//            out.writeObject(getProjectBuildFile().getAbsolutePath());
//        } else {
//            out.writeObject((String)null);
//        }
//    }
    
    /*
    // The above assumes that the SomeType is safely serializable, e.g. String or Date.
    // If it is some class of your own that might change incompatibly, use e.g.:
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        NbMarshalledObject read = (NbMarshalledObject)in.readObject();
        if (read != null) {
            try {
                setSomeState((SomeType)read.get());
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
                // If the problem would make this component inconsistent, use:
                // throw new SafeException(e);
            }
        }
    }
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        Object toWrite;
        try {
            toWrite = new NbMarshalledObject(getSomeState());
        } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
            toWrite = null;
            // Again you may prefer to use:
            // throw new SafeException(e);
        }
        out.writeObject(toWrite);
    }
     */
    
    /*
    // Use this to discard the component after restarts (make it nonpersistent):
    private Object readResolve() throws ObjectStreamException {
        return null;
        // If you wish to conditionally discard it, make readExternal set
        // or clear some flag acc. to the condition, then use:
        // return discardFlag ? null : this;
        // Singleton component using private static ProjectPropTC theInstance:
        // if (theInstance == null) theInstance = this;
        // return theInstance;
    }
     */
    
    // ACTIONS
    
    /*
    // If you wish to have extra actions appear in the window's
    // popup menu, they can go here:
    public SystemAction[] getSystemActions() {
        SystemAction[] supe = super.getSystemActions();
        SystemAction[] mine = new SystemAction[supe.length + 1];
        System.arraycopy(supe, 0, mine, 0, supe.length);
        mine[supe.length] = SystemAction.get(SomeActionOfMine.class);
        return mine;
    }
     */
    
    /*
    // Associate implementations with copying, searching, etc.:
    protected void componentActivated() {
        ((CallbackSystemAction)SystemAction.get(FindAction.class)).setActionPerformer(new ActionPerformer() {
                public void performAction(SystemAction action) {
                    // search this component somehow
                }
            });
        // similar for CopyAction, CutAction, DeleteAction, GotoAction, ReplaceAction, etc.
        // for PasteAction, use:
        // ((PasteAction)SystemAction.get(PasteAction.class)).setPasteTypes(new PasteType[] {...});
    }
    protected void componentDeactivated() {
        // FindAction will be turned off by itself
        // ((PasteAction)SystemAction.get(PasteAction.class)).setPasteTypes(null);
    }
     */
    
    /*
    // If you want UndoAction and RedoAction to be enabled on this component:
    public UndoRedo getUndoRedo() {
        return new MyUndoRedo(this);
    }
     */
    
    // Printing, saving, compiling, etc.: use cookies on some appropriate node and
    // use this node as the node selection.
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblProject;
    private javax.swing.JLabel lblProjectBuild;
    private javax.swing.JLabel lblUserBuild;
    private org.openide.explorer.view.TreeTableView ttvProps;
    private javax.swing.JTextField txtProject;
    private javax.swing.JTextField txtProjectBuild;
    private javax.swing.JTextField txtUserBuild;
    // End of variables declaration//GEN-END:variables

    private class DummyHeaderNodeProp extends PropertySupport
    {
        public DummyHeaderNodeProp(String name, Class type, String displName, String shortDesc, boolean canRead, boolean canWrite)
        {
            super(name, type, displName, shortDesc, canRead, canWrite);
        }
        
        
        public Object getValue() throws IllegalAccessException, InvocationTargetException
        {
            return null;
        }
        
        public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
        }
        
    }

    
}
