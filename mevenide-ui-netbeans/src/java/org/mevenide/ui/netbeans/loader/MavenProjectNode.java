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
import org.openide.nodes.Children.Array;
import org.openide.nodes.Node.Cookie;
import org.openide.nodes.Sheet;
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
        setIconBase("org/mevenide/ui/netbeans/resources/MyDataIcon");
        final MavenProjectCookie cook = (MavenProjectCookie)obj.getCookie(MavenProjectCookie.class);
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
                    } else {
                        props.put(cook.getProperties());
                        firePropertySetsChange(null, getSheet().toArray());
                    }
                }
            }
        });
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
