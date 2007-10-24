/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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

package org.mevenide.netbeans.project.nodes;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.CharConversionException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;

import org.apache.maven.project.Resource;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Utilities;
import org.openide.xml.XMLUtil;


/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class ResourceFilterNode extends FilterNode {
    private static final Logger logger = Logger.getLogger(ResourceFilterNode.class.getName());
    private Resource resource;
    private File root;
    private boolean isIncluded;
    
    ResourceFilterNode(Node original, File resPath, Resource res, boolean included) {
        super(original, new ResFilterChildren(original, resPath, res));
        resource = res;
        root = resPath;
        DataObject dobj = (DataObject)getLookup().lookup(DataObject.class);
        
        isIncluded = included;
    }
    
    public Action[] getActions( boolean context ) {
        return super.getActions(context);
    }
    
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue;
        retValue = super.getIcon(param);
        if (isIncluded) {
            return retValue;
        } else {
            return Utilities.mergeImages(retValue, 
                        Utilities.loadImage("org/mevenide/netbeans/project/resources/ResourceNotIncluded.gif"),
                        0,0);
        }
    }
    
    public Image getOpenedIcon(int type) {
        java.awt.Image retValue;
        retValue = super.getOpenedIcon(type);
        if (isIncluded) {
            return retValue;
        } else {
            return Utilities.mergeImages(retValue, 
                        Utilities.loadImage("org/mevenide/netbeans/project/resources/ResourceNotIncluded.gif"),
                        0,0);
        }
    }
    
    public String getDisplayName() {
        String retValue;
        retValue = super.getDisplayName();
        return retValue;
    }

    public java.lang.String getHtmlDisplayName() {

        java.lang.String retValue;
        if (!isIncluded) {
            // try use html, need to escape all the unallowed characters..
            // how to do? is XmlUtil the answer?
            retValue = getDisplayName();
            try {
                retValue = "<I>" + XMLUtil.toAttributeValue(retValue) + "</I>";
            } catch (CharConversionException exc) {
                logger.log(Level.FINE, "conversion failed for =" + retValue, exc);
            }
        } else {
            retValue = super.getHtmlDisplayName();
        }
        return retValue;
    }
    
    static class ResFilterChildren extends FilterNode.Children {
        private Resource resource;
        private File root;
        private FileObject rootFO;
        private PropertyChangeListener list;
        private Node orig;
        ResFilterChildren(Node original, File rootpath, Resource res) {
            super(original);
            root = rootpath;
            orig = original;
            rootFO = FileUtil.toFileObject(root);
            resource = res;
            list = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    doRefreshKeys();
                }
            };
        }
        
        private void doRefreshKeys() {
            Node[] nds = orig.getChildren().getNodes();
            for (int i = 0; i < nds.length; i++) {
                DataObject dobj = (DataObject)nds[i].getLookup().lookup(DataObject.class);
                if (dobj != null) {
                    File file = FileUtil.toFile(dobj.getPrimaryFile());
                    if (file != null) {
                        boolean isIncl = DirScannerSubClass.checkIncluded(dobj.getPrimaryFile(), rootFO, resource);
                        if (!isIncl) {
                            refreshKey(nds[i]);
                        }
                    }
                }
            }
        }
        
        protected Node[] createNodes(Node obj) {
            DataObject dobj = (DataObject) obj.getLookup().lookup(DataObject.class);
            if (dobj != null) {
                File file = FileUtil.toFile(dobj.getPrimaryFile());
                if (file != null) {
                    if (/*SharabilityQuery.getSharability(file) == SharabilityQuery.NOT_SHARABLE || */
                        !DirScannerSubClass.checkVisible(file, root)) {
                        return new Node[0];
                    }
                    boolean isIncl = DirScannerSubClass.checkIncluded(dobj.getPrimaryFile(), rootFO, resource);
                    if (ShowAllResourcesAction.getInstance().isShowingAll()) {
                        Node n = new ResourceFilterNode((Node)obj, root, resource, isIncl);
                        return new Node[] {n};
                    } else if (isIncl) {
                        Node n = new ResourceFilterNode((Node)obj, root, resource, isIncl);
                        return new Node[] {n};
                    }
                    return new Node[0];
                }
            }
            Node origos = (Node)obj;
            return new Node[] { origos.cloneNode() };
        }        

        protected void removeNotify() {
            super.removeNotify();
            ShowAllResourcesAction.getInstance().removePropertyChangeListener(list);
        }

        protected void addNotify() {
            super.addNotify();
            ShowAllResourcesAction.getInstance().addPropertyChangeListener(list);
        }
    }
    
}

