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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.mevenide.ui.netbeans.ArtifactCookie;
import org.apache.maven.project.Dependency;
import org.apache.maven.repository.Artifact;
import org.mevenide.util.MevenideUtils;
import org.openide.ErrorManager;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class DependencyNode extends AbstractNode
{
    private Artifact artefact;
    private ArtifactCookie cookie;
    /** Creates a new instance of DependencyNode */
    public DependencyNode(Artifact art)
    {
        super(Children.LEAF);
        artefact = art;
        Dependency dep = art.getDependency();
        setName(dep.getArtifactId());
        setDisplayName(dep.getArtifactId() + " [" + dep.getVersion() + "]");
        setShortDescription("URL:" + dep.getUrl());
        if (artefact.exists())
        {
            setIconBase("org/mevenide/ui/netbeans/loader/jar"); //NOI18N
        } else
        {
            setIconBase("org/mevenide/ui/netbeans/loader/greyjar"); //NOI18N
        }
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
    
    public Node.Cookie getCookie(Class clazz)
    {
        if (ArtifactCookie.class.isAssignableFrom(clazz) && artefact.exists())
        {
            if (cookie == null )
            {
                cookie = new SingleArtifactCookie();
            }
            return cookie;
        }
        return  super.getCookie(clazz);
    }
    
    protected Sheet createSheet()
    {
        Sheet sheet = super.createSheet();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        if (set == null)
        {
            set = sheet.createPropertiesSet();
            sheet.put(set);
        }
        set.put(createProps());
        List depProps = artefact.getDependency().getProperties();
        if (depProps != null && depProps.size() > 0)
        {
            Sheet.Set set2 = new Sheet.Set();
            set2.setName("DependProps");
            set2.setDisplayName("Dependency props");
            set2.put(createDepProps(depProps));
            sheet.put(set2);
        }
        return sheet;
    }
    
    private Node.Property[] createDepProps(List list)
    {
        Node.Property[] props = new Node.Property[list.size()];
        Iterator it = list.iterator();
        int count = 0;
        while (it.hasNext())
        {
            String pair = (String)it.next();
            final String[] str = MevenideUtils.resolveProperty(pair);
            props[count] = new PropertySupport.ReadOnly(str[0], String.class, str[0], "Dependency property")
            {
                public Object getValue() throws InvocationTargetException, IllegalAccessException
                {
                    return str[1];
                }
            };
            count = count + 1;
        }
        return props;
    }
    
    private Node.Property[] createProps()
    {
        
        Node.Property[] props = new Node.Property[7];
        try
        {
            Dependency dep = artefact.getDependency();
            props[0] = new PropertySupport.Reflection(dep, String.class, "getUrl", null);
            props[0].setName("UrlPath"); //NOI18N
            props[0].setDisplayName("URL");
            props[1] = new PropertySupport.Reflection(dep, String.class, "getType", null);
            props[1].setName("type"); //NOI18N
            props[1].setDisplayName("Dependency Type");
            props[2] = new PropertySupport.Reflection(artefact, String.class, "getName", null);
            props[2].setName("Name"); //NOI18N
            props[2].setDisplayName("Name");
            props[3] = new PropertySupport.Reflection(dep, String.class, "getGroupId", null);
            props[3].setName("GroupId"); //NOI18N
            props[3].setDisplayName("Group ID");
            props[4] = new PropertySupport.Reflection(dep, String.class, "getArtifactId", null);
            props[4].setName("ArtefactId"); //NOI18N
            props[4].setDisplayName("Artefact ID");
            props[5] = new PropertySupport.Reflection(dep, String.class, "getVersion", null);
            props[5].setName("Version"); //NOI18N
            props[5].setDisplayName("Version");
            props[6] = new PropertySupport.Reflection(artefact, Boolean.TYPE, "exists", null);
            props[6].setName("Local"); //NOI18N
            props[6].setDisplayName("Locally available");
        } catch (NoSuchMethodException exc)
        {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, exc);
            props = new Node.Property[0];
        }
        return props;
    }
    
    private class SingleArtifactCookie implements ArtifactCookie
    {
        
        public List getArtifacts()
        {
            List toRet = new ArrayList(3);
            toRet.add(artefact);
            return toRet;
        }
        
    }
    
}
