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

import java.util.Iterator;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.modules.ModuleInfo;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;

/** Manages a module's lifecycle.
 * Remember that an installer is optional and often not needed at all.
 *
 * @author cenda
 */
public class MavenModule extends ModuleInstall
{
    private static Log log = LogFactory.getLog(MavenModule.class);
 
    private static final long serialVersionUID = -485754848837354747L;
    
    public void restored()
    {
        // By default, do nothing.
        // Put your startup code here.
    }
    
    public void validate() throws java.lang.IllegalStateException
    {
        String maven_home = System.getProperty("Env-MAVEN_HOME");//NOI18N
        if (maven_home == null)
        {
            throw new IllegalStateException("Maven not installed or the MAVEN_HOME property not set.");
        }
        
        //DEBUG
        Lookup.Template template = new Lookup.Template(ModuleInfo.class, "Module[org.mevenide.ui.netbeans", null); //NOI18N
        Lookup.Item item = Lookup.getDefault().lookupItem(template);
        if (item != null)
        {
            ModuleInfo info = (ModuleInfo)item.getInstance();
            log.debug("classpath = " + info.getAttribute("Class-Path"));
        } else {
            log.debug("module not found :(");
        }
    }    
    
    // Less commonly needed:
    /*
    public boolean closing() {
        // return false if cannot close now
        return true;
    }
    public void close() {
        // shut down stuff
    }
     */
    
    // Generally the methods below should be avoided in favor of restored():
    /*
    // By default, do nothing but call restored().
    public void installed() {
        restored();
    }
     
    // By default, do nothing.
    public void uninstalled() {
    }
     
    // By default, call restored().
    public void updated(int release, String specVersion) {
    }
     */
    
    // It is no longer recommended to override Externalizable methods
    // (readExternal and writeExternal). See the Modules API section on
    // "installation-clean" modules for an explanation.
    
}
