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

import java.io.IOException;

import org.openide.cookies.*;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.text.DataEditorSupport;
import org.openide.windows.CloneableOpenSupport;

/** Support for editing a data object as text.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
// Replace OpenCookie with EditCookie or maybe ViewCookie as desired:
public class MavenEditorSupport extends DataEditorSupport implements EditorCookie, OpenCookie, CloseCookie, PrintCookie
{
    
    /** Create a new editor support.
     * @param obj the data object whose primary file will be edited as text
     */
    public MavenEditorSupport(MavenProjectDataObject obj)
    {
        super(obj, new MavenEnv(obj));
        // Set a MIME type as needed, e.g.:
        setMIMEType("text/xml"); //NOI18N
    }
    
    /** Called when the document is modified.
     * Here, adding a save cookie to the object and marking it modified.
     * @return true if the modification is acceptable
     */
    protected boolean notifyModified()
    {
        if (!super.notifyModified())
        {
            return false;
        }
        MavenProjectDataObject obj = (MavenProjectDataObject)getDataObject();
        if (obj.getCookie(SaveCookie.class) == null)
        {
            obj.setModified(true);
            // You must implement this method on the object:
            obj.addSaveCookie(new Save());
        }
        return true;
    }
    
    /** Called when the document becomes unmodified.
     * Here, removing the save cookie from the object and marking it unmodified.
     */
    protected void notifyUnmodified()
    {
        MavenProjectDataObject obj = (MavenProjectDataObject)getDataObject();
        SaveCookie save = (SaveCookie)obj.getCookie(SaveCookie.class);
        if (save != null)
        {
            // You must implement this method on the object:
            obj.removeSaveCookie(save);
            obj.setModified(false);
        }
        super.notifyUnmodified();
    }
    
    /** A save cookie to use for the editor support.
     * When saved, saves the document to disk and marks the object unmodified.
     */
    private class Save implements SaveCookie
    {
        public void save() throws IOException
        {
            saveDocument();
            getDataObject().setModified(false);
        }
    }
    
    /** A description of the binding between the editor support and the object.
     * Note this may be serialized as part of the window system and so
     * should be static, and use the transient modifier where needed.
     */
    private static class MavenEnv extends DataEditorSupport.Env
    {
        
        private static final long serialVersionUID = 1656565345463L;
        
        /** Create a new environment based on the data object.
         * @param obj the data object to edit
         */
        public MavenEnv(MavenProjectDataObject obj)
        {
            super(obj);
        }
        
        /** Get the file to edit.
         * @return the primary file normally
         */
        protected FileObject getFile()
        {
            return getDataObject().getPrimaryFile();
        }
        
        /** Lock the file to edit.
         * Should be taken from the file entry if possible, helpful during
         * e.g. deletion of the file.
         * @return a lock on the primary file normally
         * @throws IOException if the lock could not be taken
         */
        protected FileLock takeLock() throws IOException
        {
            return ((MavenProjectDataObject)getDataObject()).getPrimaryEntry().takeLock();
        }
        
        /** Find the editor support this environment represents.
         * Note that we have to look it up, as keeping a direct
         * reference would not permit this environment to be serialized.
         * @return the editor support
         */
        public CloneableOpenSupport findCloneableOpenSupport()
        {
            return (MavenEditorSupport)getDataObject().getCookie(MavenEditorSupport.class);
        }
        
    }
    
}
