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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import javax.swing.Action;
import org.openide.filesystems.*;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children.Keys;
import org.openide.util.WeakListener;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class PluginScriptNode extends AbstractNode
{
    
    private static Action actions[];
    
    PluginScriptNode(FileObject rootFolder)
    {
        super(new PluginScriptChildren(rootFolder));
        setName("PluginScript"); //NOI18N
        setDisplayName("Plugin Script");
        // can do so, since we depend on it..
        setIconBase("org/netbeans/modules/java/j2seproject/ui/resources/packageRoot"); //NOI18N
    }
    
    public Action[] getActions( boolean context )
    {
        
        if ( actions == null )
        {
            // TODO??
            actions = new Action[0];
        }
        return actions;
        
    }
    
    
    private static class PluginScriptChildren extends Keys implements PropertyChangeListener, FileChangeListener
    {
        private static final Object KEY_PLUGIN_SCRIPT = "PluginScript"; //NOI18N
        private static final Object KEY_PLUGIN_PROPS = "PluginProps"; //NOI18N
        
        private FileObject fileObject;
        private DataObject pluginDO;
        private DataObject propsDO;
        
        PluginScriptChildren(FileObject rootFolder)
        {
            fileObject = rootFolder;
            fileObject.addFileChangeListener(WeakListener.fileChange(this,  fileObject));
        }
        
        public void addNotify()
        {
            super.addNotify();
            setKeys(new Object[] {KEY_PLUGIN_SCRIPT, KEY_PLUGIN_PROPS});
            pluginDO = findDO("jelly");
            propsDO = findDO("properties");
            if (pluginDO != null) {
                pluginDO.addPropertyChangeListener(this);
            } 
            if (propsDO != null) {
                propsDO.addPropertyChangeListener(this);
            } 
        }
        
        public void removeNotify()
        {
            setKeys(Collections.EMPTY_LIST);
            super.removeNotify();
            if (propsDO != null) {
                propsDO.removePropertyChangeListener(this);
            }
            if (pluginDO != null) {
                pluginDO.removePropertyChangeListener(this);
            }
        }
        
        protected Node[] createNodes(Object obj)
        {
            if (obj == KEY_PLUGIN_SCRIPT)
            {
                if (pluginDO != null && pluginDO.isValid()) {
	                return new Node[] {pluginDO.getNodeDelegate().cloneNode()};
                }
            }
            if (obj == KEY_PLUGIN_PROPS) {
                if (propsDO != null && propsDO.isValid()) {
	                return new Node[] {propsDO.getNodeDelegate().cloneNode()};
                }
            }
            return new Node[0];
        }
        
        
        public void propertyChange(PropertyChangeEvent evt)
        {
            if (DataObject.PROP_VALID.equals(evt.getPropertyName())) {
                if (evt.getSource() == pluginDO) {
                    if (pluginDO != null) {
                        pluginDO.removePropertyChangeListener(this);
                    }
                    pluginDO = findDO("jelly");
                    if (pluginDO != null) {
                        pluginDO.addPropertyChangeListener(this);
                    }
                    refreshKey(KEY_PLUGIN_SCRIPT);
                }
                if (evt.getSource() == propsDO) {
                    if (propsDO != null) {
                        propsDO.removePropertyChangeListener(this);
                    }
                    propsDO = findDO("properties");
                    if (propsDO != null) {
                        propsDO.addPropertyChangeListener(this);
                    }
                    refreshKey(KEY_PLUGIN_PROPS);
                }
            }
        }
        
        private DataObject findDO(String extension) {
            FileObject fo = fileObject.getFileObject("plugin", extension);
            DataObject dobj = null;
            try {
                dobj = DataObject.find(fo);
            } catch (DataObjectNotFoundException exc)
            { // do nothing, is probably not valid.. don't return any children..
            }
            return dobj;
        }

        private void doFileUpdates() {
            DataObject findDO = findDO("jelly");
            if ((pluginDO != null) != (findDO != null)) {
                if (pluginDO != null) {
                    pluginDO.removePropertyChangeListener(this);
                }
                pluginDO = findDO;
                if (pluginDO != null) {
                    pluginDO.addPropertyChangeListener(this);
                }
                refreshKey(KEY_PLUGIN_SCRIPT);
            }
            findDO = findDO("properties");
            if ((propsDO != null) != (findDO != null)) {
                if (propsDO != null) {
                    propsDO.removePropertyChangeListener(this);
                }
                propsDO = findDO;
                if (propsDO != null) {
                    propsDO.addPropertyChangeListener(this);
                }
                refreshKey(KEY_PLUGIN_PROPS);
            }
        }
        
        public void fileAttributeChanged(FileAttributeEvent fileAttributeEvent)
        {
            //NOOP
        }
        
        public void fileChanged(FileEvent fileEvent)
        {
            //NOOP
        }
        
        public void fileDataCreated(FileEvent fileEvent)
        {
            doFileUpdates();
        }

        
        public void fileDeleted(FileEvent fileEvent)
        {
            doFileUpdates();
        }
        
        public void fileFolderCreated(FileEvent fileEvent)
        {
            //NOOP
        }
        
        public void fileRenamed(FileRenameEvent fileRenameEvent)
        {
            doFileUpdates();
        }
        
    }
}

