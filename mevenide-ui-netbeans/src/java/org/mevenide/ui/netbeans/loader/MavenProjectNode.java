/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.netbeans.loader;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.ui.netbeans.ArtifactCookie;
import org.mevenide.ui.netbeans.MavenProjectCookie;
import org.openide.actions.PropertiesAction;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.Children.Array;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/** A node to represent this object.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenProjectNode extends DataNode {
     private static Log log = LogFactory.getLog(MavenProjectNode.class);
     
     /**
      * id name for maven properties Sheet.
      */
     public static final String SHEET_MAVEN_PROPS = "MavenProperties"; //NOI18N
   
    private boolean sheetCreated = false;
    
    public MavenProjectNode(MavenProjectDataObject obj) {
        this(obj, new Array());
        ArtifactCookie cook = (ArtifactCookie)getCookie(ArtifactCookie.class);
        if (cook != null)
        {
            Node depNode = new DependenciesNode(obj, cook);
            getChildren().add(new Node[] {depNode});
        }
    }
    
    public MavenProjectNode(MavenProjectDataObject obj, Children ch) {
        super(obj, ch);
        setIconBase("org/mevenide/ui/netbeans/resources/MyDataIcon"); //NOI18N
        final MavenProjectCookie cook = (MavenProjectCookie)obj.getCookie(MavenProjectCookie.class);
        if (cook != null) {
            //for templates the projectcookie is not there..
            cook.addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent event)
                {
                    log.debug("property changed " + event.getPropertyName());
                    fireDisplayNameChange(null, getDisplayName());
                    if (sheetCreated)
                    {
                        log.debug("Updating sheet");
                        Sheet.Set props = getSheet().get(SHEET_MAVEN_PROPS);
                        if (props == null)
                        {
                            createSheet();
                        } else
                        {
                            props.put(cook.getProperties());
                            firePropertySetsChange(null, getSheet().toArray());
                        }
                    }
                }
            });
        }
    }
    
    public String getDisplayName()
    {
        String toReturn = super.getDisplayName();
        MavenProjectCookie cook = (MavenProjectCookie)this.getCookie(MavenProjectCookie.class);
        if (cook != null)
        {
            toReturn = toReturn + " [" + cook.getProjectName() + "]";
        }
        return toReturn;
    }
    
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set set = sheet.get(SHEET_MAVEN_PROPS);
        if (set == null) {
            set = createMavenPropsSet();
            sheet.put(set);
        }
        MavenProjectCookie cook = (MavenProjectCookie)this.getCookie(MavenProjectCookie.class);
        sheetCreated = true;
        set.put(cook.getProperties());
        return sheet;
    }
    
    private static class DependenciesNode extends AbstractNode
    {
        DataObject obj;
        public DependenciesNode(MavenProjectDataObject obj, ArtifactCookie cook)
        {
            super(new DependencyChildren(cook));
            setName("Dependencies");
            setDisplayName("Dependencies");
            setIconBase("org/mevenide/ui/netbeans/loader/jars");//NOI18N
            this.obj = obj;
        }
        
        public Cookie getCookie(Class clazz)
        {
            if (ArtifactCookie.class.isAssignableFrom(clazz))
            {
                return obj.getCookie(clazz);
            }
            return  super.getCookie(clazz);
        }
        
        public SystemAction[] getActions()
        {
            SystemAction[] actions = new SystemAction[]
            {
                SystemAction.get(MountDependenciesAction.class),
                null,
                SystemAction.get(PropertiesAction.class)
            };
            return actions;
        }        
        
    }
    
    /** 
    * Convenience method to create new sheet set named MavenProjectNode.SHEET_MAVEN_PROPS.
    * @return a new properties sheet set
    */
    public static final Sheet.Set createMavenPropsSet () {
        Sheet.Set ps = new Sheet.Set ();
        ps.setName(SHEET_MAVEN_PROPS);
        ps.setDisplayName(NbBundle.getMessage(MavenProjectNode.class, "MavenProjectNode.sheetName"));
        ps.setShortDescription(NbBundle.getMessage(MavenProjectNode.class, "MavenProjectNode.sheetDesc"));
        return ps;
    }
    
    // Don't use getDefaultAction(); just make that first in the data loader's getActions list
    
}
