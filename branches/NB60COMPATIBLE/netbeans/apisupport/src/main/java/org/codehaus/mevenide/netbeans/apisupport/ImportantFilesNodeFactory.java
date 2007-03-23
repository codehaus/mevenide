/*
 * ImportantFilesNodeFactory.java
 *
 * Created on March 14, 2007, 12:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.apisupport;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.apisupport.project.spi.NbModuleProvider;
import org.netbeans.modules.apisupport.project.spi.NodeFactoryUtils;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.ErrorManager;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 *
 * @author mkleint
 */
public class ImportantFilesNodeFactory implements NodeFactory {
    /** Package private for unit tests. */
    static final String IMPORTANT_FILES_NAME = "important.files"; // NOI18N
    
    static final Object LAYER = new Object();
    /** Package private for unit tests only. */
    static final RequestProcessor RP = new RequestProcessor();
    
    /** Creates a new instance of ImportantFilesNodeFactory */
    public ImportantFilesNodeFactory() {
    }
    
    public NodeList createNodes(Project p) {
        if (p.getLookup().lookup(NbModuleProvider.class) != null) {
            return new ImpFilesNL(p);
        }
        return NodeFactorySupport.fixedNodeList();
    }

    private static class ImpFilesNL implements NodeList<String> {
        private Project project;
        
        public ImpFilesNL(Project p) {
            project = p;
        }
    
        public List<String> keys() {
            return Collections.singletonList(IMPORTANT_FILES_NAME);
        }

        public void addChangeListener(ChangeListener l) {
            //ignore, doesn't change
        }

        public void removeChangeListener(ChangeListener l) {
            //ignore, doesn't change
        }

        public Node node(String key) {
            assert key == IMPORTANT_FILES_NAME;
            return new ImportantFilesNode(project);
        }

        public void addNotify() {
        }

        public void removeNotify() {
        }
    }
    /**
     * Show node "Important Files" with various config and docs files beneath it.
     */
    static final class ImportantFilesNode extends AnnotatedNode {
        
        public ImportantFilesNode(Project project) {
            super(new ImportantFilesChildren(project));
        }
        
        ImportantFilesNode(Children ch) {
            super(ch);
        }
        
        public String getName() {
            return IMPORTANT_FILES_NAME;
        }
        
        private Image getIcon(boolean opened) {
            Image badge = Utilities.loadImage("org/codehaus/mevenide/netbeans/apisupport/config-badge.gif", true);
            return Utilities.mergeImages(getTreeFolderIcon(opened), badge, 8, 8);
        }
        
        private static final String DISPLAY_NAME = NbBundle.getMessage(ImportantFilesNodeFactory.class, "LBL_important_files");
        
        public String getDisplayName() {
            return annotateName(DISPLAY_NAME);
        }
        
        public String getHtmlDisplayName() {
            return computeAnnotatedHtmlDisplayName(DISPLAY_NAME, getFiles());
        }
        
        public Image getIcon(int type) {
            return annotateIcon(getIcon(false), type);
        }
        
        public Image getOpenedIcon(int type) {
            return annotateIcon(getIcon(true), type);
        }
        
    }
    
    /**
     * Actual list of important files.
     */
    private static final class ImportantFilesChildren extends Children.Keys {
        
        private List visibleFiles = new ArrayList();
        private FileChangeListener fcl;
        
        /** Abstract location to display name. */
        private static final java.util.Map<String,String> FILES = new LinkedHashMap();
        static {
            FILES.put("src/main/nbm/manifest.mf", NbBundle.getMessage(ImportantFilesNodeFactory.class, "LBL_module_manifest"));
            FILES.put("src/main/nbm/module.xml", NbBundle.getMessage(ImportantFilesNodeFactory.class, "LBL_module.xml"));
        }
        
        private final Project project;
        
        public ImportantFilesChildren(Project project) {
            this.project = project;
        }
        
        protected void addNotify() {
            super.addNotify();
            attachListeners();
            refreshKeys();
        }
        
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
            removeListeners();
            super.removeNotify();
        }
        
        protected Node[] createNodes(Object key) {
            if (key instanceof String) {
                String loc = (String) key;
                FileObject file = project.getProjectDirectory().getFileObject(loc);
                if (file != null) {
                    try {
                        Node orig = DataObject.find(file).getNodeDelegate();
                        return new Node[] {new SpecialFileNode(orig, (String) FILES.get(loc))};
                    } catch (DataObjectNotFoundException e) {
                        throw new AssertionError(e);
                    }
                }
                return new Node[0];
            } else if (key == LAYER) {
                Node nd = NodeFactoryUtils.createLayersNode(project);
                if (nd != null) {
                    return new Node[] {nd };
                }
                return new Node[0];
//            } else if (key instanceof ServiceNodeHandler) {
//                return new Node[]{((ServiceNodeHandler)key).createServiceRootNode()};
            } else {
                throw new AssertionError(key);
            } 
        }
        
        private void refreshKeys() {
            Set<FileObject> files = new HashSet();
            List newVisibleFiles = new ArrayList();
            newVisibleFiles.add(LAYER);
            //TODO figure out location of layer file somehow and ad to files..
            // influences the scm annotations only..
//            LayerUtils.LayerHandle handle = LayerUtils.layerForProject(project);
//            FileObject layerFile = handle.getLayerFile();
//            if (layerFile != null) {
//                newVisibleFiles.add(handle);
//                files.add(layerFile);
//            }
            Iterator it = FILES.keySet().iterator();
            while (it.hasNext()) {
                String loc = (String) it.next();
                FileObject file = project.getProjectDirectory().getFileObject(loc);
                if (file != null) {
                    newVisibleFiles.add(loc);
                    files.add(file);
                }
            }
            if (!isInitialized() || !newVisibleFiles.equals(visibleFiles)) {
                visibleFiles = newVisibleFiles;
//                visibleFiles.add(project.getLookup().lookup(ServiceNodeHandler.class));
                RP.post(new Runnable() { // #72471
                    public void run() {
                        setKeys(visibleFiles);
                    }
                });
                ((ImportantFilesNode) getNode()).setFiles(files);
            }
        }
        
        private void attachListeners() {
            try {
                if (fcl == null) {
                    fcl = new FileChangeAdapter() {
                        public void fileDataCreated(FileEvent fe) {
                            refreshKeys();
                        }
                        public void fileDeleted(FileEvent fe) {
                            refreshKeys();
                        }
                    };
                    project.getProjectDirectory().getFileSystem().addFileChangeListener(fcl);
                }
            } catch (FileStateInvalidException e) {
                assert false : e;
            }
        }
        
        private void removeListeners() {
            if (fcl != null) {
                try {
                    project.getProjectDirectory().getFileSystem().removeFileChangeListener(fcl);
                } catch (FileStateInvalidException e) {
                    assert false : e;
                }
                fcl = null;
            }
        }
        
    }
    /**
     * Node to represent some special file in a project.
     * Mostly just a wrapper around the normal data node.
     */
    static final class SpecialFileNode extends FilterNode {
        
        private final String displayName;
        
        public SpecialFileNode(Node orig, String displayName) {
            super(orig);
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            if (displayName != null) {
                return displayName;
            } else {
                return super.getDisplayName();
            }
        }
        
        public boolean canRename() {
            return false;
        }
        
        public boolean canDestroy() {
            return false;
        }
        
        public boolean canCut() {
            return false;
        }
        
        public String getHtmlDisplayName() {
            String result = null;
            DataObject dob = (DataObject) getLookup().lookup(DataObject.class);
            if (dob != null) {
                Set files = dob.files();
                result = computeAnnotatedHtmlDisplayName(getDisplayName(), files);
            }
            return result;
        }
        
    }
    
    /**
     * Annotates <code>htmlDisplayName</code>, if it is needed, and returns the
     * result; <code>null</code> otherwise.
     */
    private static String computeAnnotatedHtmlDisplayName(
            final String htmlDisplayName, final Set files) {
        
        String result = null;
        if (files != null && files.iterator().hasNext()) {
            try {
                FileObject fo = (FileObject) files.iterator().next();
                FileSystem.Status stat = fo.getFileSystem().getStatus();
                if (stat instanceof FileSystem.HtmlStatus) {
                    FileSystem.HtmlStatus hstat = (FileSystem.HtmlStatus) stat;
                    
                    String annotated = hstat.annotateNameHtml(htmlDisplayName, files);
                    
                    // Make sure the super string was really modified (XXX why?)
                    if (!htmlDisplayName.equals(annotated)) {
                        result = annotated;
                    }
                }
            } catch (FileStateInvalidException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
        }
        return result;
    }
    
    /**
     * Returns default folder icon as {@link java.awt.Image}. Never returns
     * <code>null</code>.
     *
     * @param opened wheter closed or opened icon should be returned.
     */
    public static Image getTreeFolderIcon(boolean opened) {
        Image base = null;
        Icon baseIcon = UIManager.getIcon(opened ? OPENED_ICON_KEY_UIMANAGER : ICON_KEY_UIMANAGER); // #70263
        if (baseIcon != null) {
            base = Utilities.icon2Image(baseIcon);
        } else {
            base = (Image) UIManager.get(opened ? OPENED_ICON_KEY_UIMANAGER_NB : ICON_KEY_UIMANAGER_NB); // #70263
            if (base == null) { // fallback to our owns
                base = Utilities.loadImage(opened ? OPENED_ICON_PATH : ICON_PATH, true);
            }
        }
        assert base != null;
        return base;
    }
    
    private static final String ICON_KEY_UIMANAGER = "Tree.closedIcon"; // NOI18N
    private static final String OPENED_ICON_KEY_UIMANAGER = "Tree.openIcon"; // NOI18N
    private static final String ICON_KEY_UIMANAGER_NB = "Nb.Explorer.Folder.icon"; // NOI18N
    private static final String OPENED_ICON_KEY_UIMANAGER_NB = "Nb.Explorer.Folder.openedIcon"; // NOI18N
    private static final String ICON_PATH = "org/codehaus/mevenide/netbeans/defaultFolder.gif"; // NOI18N
    private static final String OPENED_ICON_PATH = "org/codehaus/mevenide/netbeans/defaultFolderOpen.gif"; // NOI18N
    
}
