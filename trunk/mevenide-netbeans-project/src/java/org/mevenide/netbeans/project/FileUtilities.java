/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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

package org.mevenide.netbeans.project;

import java.io.IOException;
import java.util.StringTokenizer;
import org.openide.filesystems.FileObject;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class FileUtilities
{
    
    /** Creates a new instance of FileUtilities */
    public FileUtilities()
    {
    }
    
    public static FileObject findFolder(FileObject rootFolder, String relPath)
    {
        StringTokenizer tok = new StringTokenizer(relPath, "/", false);
        FileObject folder = rootFolder;
        while (tok.hasMoreTokens() && folder != null)
        {
            folder = folder.getFileObject(tok.nextToken(), null);
        }
        return folder;
    }
    
    public static FileObject findOrCreate(FileObject rootFolder, String relPath) throws IOException
    {
        FileObject parentFolder = rootFolder;
        FileObject folder = rootFolder;
        StringTokenizer tok = new StringTokenizer(relPath, "/", false);
        while (tok.hasMoreTokens())
        {
            String name = tok.nextToken();
            folder = parentFolder.getFileObject(name, null);
            if (folder == null)
            {
                parentFolder.createFolder(name);
            }
            parentFolder = folder;
        }
        return folder;
    }
}
