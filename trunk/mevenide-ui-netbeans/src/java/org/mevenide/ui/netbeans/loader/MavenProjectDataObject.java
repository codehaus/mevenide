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

import java.io.File;
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
