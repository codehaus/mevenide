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
package org.mevenide.ui.netbeans.exec;

import java.awt.Image;
import java.beans.*;
import org.openide.ErrorManager;

import org.openide.execution.Executor;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;


public class MavenExecutorBeanInfo extends SimpleBeanInfo {

    public BeanInfo[] getAdditionalBeanInfo () {
        try {
            return new BeanInfo[] {
                       Introspector.getBeanInfo (Executor.class)
                   };
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            return null;
        }
    }

    public BeanDescriptor getBeanDescriptor () {
        BeanDescriptor desc = new BeanDescriptor (MavenExecutor.class);
        desc.setDisplayName (NbBundle.getMessage (MavenExecutorBeanInfo.class, "LBL_executor"));
        desc.setShortDescription (NbBundle.getMessage (MavenExecutorBeanInfo.class, "HINT_executor"));
        return desc;
    }

    public PropertyDescriptor[] getPropertyDescriptors () {
        try {
            PropertyDescriptor target = new PropertyDescriptor ("goal", MavenExecutor.class);
            target.setDisplayName (NbBundle.getMessage (MavenExecutorBeanInfo.class, "PROP_goal"));
            target.setShortDescription (NbBundle.getMessage (MavenExecutorBeanInfo.class, "HINT_goal"));
            target.setPropertyEditorClass(GoalPropEditor.class);
            PropertyDescriptor executor = new PropertyDescriptor ("externalExecutor", MavenExecutor.class, "getExternalExecutor", "setExternalExecutor"); // NOI18N
            executor.setDisplayName (NbBundle.getMessage (MavenExecutorBeanInfo.class, "PROP_externalExecutor"));
            executor.setShortDescription (NbBundle.getMessage (MavenExecutorBeanInfo.class, "HINT_externalExecutor"));
            PropertyDescriptor offline = new PropertyDescriptor ("offline", MavenExecutor.class, "isOffline", "setOffline"); // NOI18N
            offline.setDisplayName (NbBundle.getMessage (MavenExecutorBeanInfo.class, "PROP_offline"));
            offline.setShortDescription (NbBundle.getMessage (MavenExecutorBeanInfo.class, "HINT_offline"));
            PropertyDescriptor nobanner = new PropertyDescriptor ("nobanner", MavenExecutor.class, "isNoBanner", "setNoBanner"); // NOI18N
            nobanner.setDisplayName (NbBundle.getMessage (MavenExecutorBeanInfo.class, "PROP_nobanner"));
            nobanner.setShortDescription (NbBundle.getMessage (MavenExecutorBeanInfo.class, "HINT_nobanner"));
            
            return new PropertyDescriptor[] { target, executor, offline, nobanner };
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            return null;
        }
    }

    public Image getIcon (int type) {
        if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
            return Utilities.loadImage ("org/mevenide/ui/netbeans/resources/MavenIcon.gif");
        } else {
            return null;
        }
    }

}
