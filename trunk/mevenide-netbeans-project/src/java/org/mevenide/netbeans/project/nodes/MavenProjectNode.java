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
package org.mevenide.netbeans.project.nodes;

import java.awt.Image;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Utilities;


/** A node to represent this object.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenProjectNode extends FilterNode {
     private static Log log = LogFactory.getLog(MavenProjectNode.class);
     
     
     private MavenProject project;
     private Image icon;
     public MavenProjectNode(Node original, MavenProject project) {
        super(original, new MavenProjectChildren(project));
        this.project = project;
//        setIconBase("org/mevenide/netbeans/projects/resources/MavenIcon");
    }
    
    
    public String getDisplayName()
    {
        return project.getDisplayName();
    }
    
    public Image getIcon(int param)
    {
        if (icon == null) {
            icon = Utilities.loadImage("org/mevenide/netbeans/projects/resources/MavenIcon.gif");
        }
        return icon;
    }
    
    public Image getOpenedIcon(int param)
    {
        if (icon == null) {
            icon = Utilities.loadImage("org/mevenide/netbeans/projects/resources/MavenIcon.gif");
        }
        return icon;
    }
    
//    protected Sheet createSheet() {
//        Sheet sheet = super.createSheet();
//        Sheet.Set set = sheet.get(SHEET_MAVEN_PROPS);
//        if (set == null) {
//            set = createMavenPropsSet();
//            sheet.put(set);
//        }
//        MavenProjectCookie cook = (MavenProjectCookie)this.getCookie(MavenProjectCookie.class);
//        sheetCreated = true;
//        set.put(cook.getProperties());
//        return sheet;
//    }
//    
//    private static class DependenciesNode extends AbstractNode
//    {
//        DataObject obj;
//        public DependenciesNode(MavenProjectDataObject obj, ArtifactCookie cook)
//        {
//            super(new DependencyChildren(cook));
//            setName("Dependencies");
//            setDisplayName("Dependencies");
//            setIconBase("org/mevenide/ui/netbeans/loader/jars");//NOI18N
//            this.obj = obj;
//        }
//        
//        public Cookie getCookie(Class clazz)
//        {
//            if (ArtifactCookie.class.isAssignableFrom(clazz))
//            {
//                return obj.getCookie(clazz);
//            }
//            return  super.getCookie(clazz);
//        }
//        
//        public SystemAction[] getActions()
//        {
//            SystemAction[] actions = new SystemAction[]
//            {
//                SystemAction.get(MountDependenciesAction.class),
//                null,
//                SystemAction.get(PropertiesAction.class)
//            };
//            return actions;
//        }        
//        
//    }
    
//    /** 
//    * Convenience method to create new sheet set named MavenProjectNode.SHEET_MAVEN_PROPS.
//    * @return a new properties sheet set
//    */
//    public static final Sheet.Set createMavenPropsSet () {
//        Sheet.Set ps = new Sheet.Set ();
//        ps.setName(SHEET_MAVEN_PROPS);
//        ps.setDisplayName(NbBundle.getMessage(MavenProjectNode.class, "MavenProjectNode.sheetName"));
//        ps.setShortDescription(NbBundle.getMessage(MavenProjectNode.class, "MavenProjectNode.sheetDesc"));
//        return ps;
//    }
//    
//    // Don't use getDefaultAction(); just make that first in the data loader's getActions list
    
}
