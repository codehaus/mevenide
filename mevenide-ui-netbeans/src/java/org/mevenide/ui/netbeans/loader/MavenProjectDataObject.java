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

import java.io.File;
import org.mevenide.ui.netbeans.MavenProjectCookie;
import org.mevenide.ui.netbeans.MavenSettings;
import org.mevenide.ui.netbeans.exec.MavenExecSupport;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

/** Represents a My object in the Repository.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenProjectDataObject extends MultiDataObject {
    
    public MavenProjectDataObject(FileObject pf, MavenProjectDataLoader loader) throws DataObjectExistsException {
        super(pf, loader);
        init();
    }
    
    private void init() {
        CookieSet cookies = getCookieSet();
        // Add whatever capabilities you need, e.g.:
        cookies.add(new MavenExecSupport(getPrimaryEntry()));
        // See Editor Support template in Editor API:
        cookies.add(new MavenEditorSupport(this));
        File file = FileUtil.toFile(getPrimaryFile());
        // for jarfilesystems and similar non-local stuff, don't allow editing stuff.
        if (file != null) {
            cookies.add(new MavenProjectCookieImpl(this));
            PropFilesCookieImpl propFiles = new PropFilesCookieImpl();
            propFiles.setProjectFile(file, MavenSettings.getUserHome());
            cookies.add(propFiles);
        }
    }
    
    public void removeSaveCookie(SaveCookie cookie)
    {
        getCookieSet().remove(cookie);
    }
    public void addSaveCookie(SaveCookie cookie)
    {
        getCookieSet().add(cookie);
    }
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.mevenide.ui.netbeans"); // TODO helpset //NOI18N
        // If you add context help, change to:
        // return new HelpCtx(MyDataObject.class);
    }
    
    protected Node createNodeDelegate() {
        return new MavenProjectNode(this);
    }
    
}
