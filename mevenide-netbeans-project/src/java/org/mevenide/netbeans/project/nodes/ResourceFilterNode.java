/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.swing.Action;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.MavenUtils;
import org.apache.maven.project.Resource;
import org.apache.tools.ant.DirectoryScanner;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.FilterNode.Children;
import org.openide.util.Utilities;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class ResourceFilterNode extends FilterNode
{
    private static Log logger = LogFactory.getLog(ResourceFilterNode.class);
    private Resource resource;
    private File root;
    private boolean isIncluded;
    private boolean isFile;
    
    ResourceFilterNode(Node original, File resPath, Resource res)
    {
        super(original, new ResFilterChildren(original, resPath, res));
        resource = res;
        root = resPath;
        DataObject dobj = (DataObject)getLookup().lookup(DataObject.class);
        
        isIncluded = true;
        if (dobj != null) {
            File file = FileUtil.toFile(dobj.getPrimaryFile());
            if (file != null && !file.isDirectory()) {
                isIncluded = checkIncluded(file);
            }
        }
    }
    
    public Action[] getActions( boolean context )
    {
        return super.getActions(context);
    }
    
    private boolean checkIncluded(File file) {
        String relPath = "";
        try {
            relPath = MavenUtils.makeRelativePath(root, file.getAbsolutePath());
        } catch (IOException exc) {
            logger.info(exc);
            return false;
        }
        List includes = resource.getIncludes();
        if (includes != null) {
            Iterator it = includes.iterator();
            while (it.hasNext()) {
                String pattern = (String)it.next();
                if (!DirectoryScanner.match(pattern, relPath)) {
                    return false;
                }
            }
        }
        
        List excludes = resource.getExcludes();
        if (excludes != null) {
            Iterator it = excludes.iterator();
            while (it.hasNext()) {
                String pattern = (String)it.next();
                if (DirectoryScanner.match(pattern, relPath)) {
                    return false;
                }
            }
        }
        String[] defaults = DirScannerSubClass.getDefaultExcludesHack();
        if (defaults != null) {
            for (int i =0; i < defaults.length; i++) {
                if (DirectoryScanner.match(defaults[i], relPath)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue;
        retValue = super.getIcon(param);
        if (isIncluded) {
            return retValue;
        } else {
            return Utilities.mergeImages(retValue, 
                        Utilities.loadImage("org/mevenide/netbeans/projects/resources/ResourceNotIncluded.gif"),
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
                        Utilities.loadImage("org/mevenide/netbeans/projects/resources/ResourceNotIncluded.gif"),
                        0,0);
        }
    }
    
    private static class ResFilterChildren extends Children {
        private Resource resource;
        private File root;
        ResFilterChildren(Node original, File rootpath, Resource res) {
            super(original);
            root = rootpath;
            resource = res;
        }
        
        protected Node[] createNodes(Object obj) {
            Node n = new ResourceFilterNode((Node)obj, root, resource);
            return new Node[] {n};
        }        
    }
    
}

