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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.mevenide.ui.netbeans.ArtifactCookie;
import org.mevenide.ui.netbeans.loader.MountDependenciesAction;
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
