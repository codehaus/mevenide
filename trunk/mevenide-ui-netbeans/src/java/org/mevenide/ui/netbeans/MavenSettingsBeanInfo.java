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
package org.mevenide.ui.netbeans;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import org.mevenide.ui.netbeans.exec.GoalsListPropEditor;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;


public class MavenSettingsBeanInfo extends SimpleBeanInfo {

    public PropertyDescriptor[] getPropertyDescriptors () {
        try {
            PropertyDescriptor executor = new PropertyDescriptor ("executor", MavenSettings.class); //NOI18N
            executor.setDisplayName (NbBundle.getMessage (MavenSettingsBeanInfo.class, "PROP_executor")); //NOI18N
            executor.setShortDescription (NbBundle.getMessage (MavenSettingsBeanInfo.class, "HINT_executor")); //NOI18N
            PropertyDescriptor mavenHome = new PropertyDescriptor("mavenHome", MavenSettings.class); //NOI18N
            mavenHome.setDisplayName(NbBundle.getMessage(MavenSettingsBeanInfo.class, "PROP_mavenHome")); //NOI18N
            mavenHome.setShortDescription(NbBundle.getMessage(MavenSettingsBeanInfo.class, "HINT_mavenHome")); //NOI18N
            PropertyDescriptor topGoals = new PropertyDescriptor ("topGoals", MavenSettings.class); //NOI18N
            topGoals.setDisplayName (NbBundle.getMessage (MavenSettingsBeanInfo.class, "PROP_topGoals")); //NOI18N
            topGoals.setShortDescription (NbBundle.getMessage (MavenSettingsBeanInfo.class, "HINT_topGoals")); //NOI18N
            topGoals.setPropertyEditorClass(GoalsListPropEditor.class);
            return new PropertyDescriptor[] { mavenHome, executor, topGoals };
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            return null;
        }
    }

    public Image getIcon (int type) {
        if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
            return Utilities.loadImage ("org/mevenide/ui/netbeans/resources/MavenIcon.gif"); //NOI18N
        } else {
            return null;
        }
    }
    
//    public static final class AntHomeEd extends PropertyEditorSupport {
//        
//        private static boolean ok(File f) {
//            return f.isDirectory() && new File(new File(f, "bin"), "maven").isFile();
//        }
//        
//        public String getAsText() {
//            return ((File)getValue()).getAbsolutePath();
//        }
//        
//        public void setAsText(String s) throws IllegalArgumentException {
//            File f = new File(s);
//            if (!ok(f)) {
//                IllegalArgumentException iae = new IllegalArgumentException("no lib/ant.jar in " + f); // NOI18N
//                AntModule.err.annotate(iae, NbBundle.getMessage(MavenSettingsBeanInfo.class, "ERR_not_ant_home", f));
//                throw iae;
//            }
//            setValue(f);
//        }
// 
//    }

}
