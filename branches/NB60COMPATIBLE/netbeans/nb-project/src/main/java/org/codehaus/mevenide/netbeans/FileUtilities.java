/* ==========================================================================
 * Copyright 2005 Mevenide Team
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


package org.codehaus.mevenide.netbeans;

import java.io.File;
import java.net.URI;
import java.util.regex.Pattern;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Various File/FileObject related utilities.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class FileUtilities {
    
    /** Creates a new instance of FileUtilities */
    private FileUtilities() {
    }
    
    public static FileObject convertURItoFileObject(URI uri) {
        if (uri == null) {
            return null;
        }
        File fil = new File(uri);
        return FileUtil.toFileObject(fil);
    }
    
    public static FileObject convertStringToFileObject(String str) {
        if (str != null) {
            File fil = new File(str);
            fil = FileUtil.normalizeFile(fil);
            return FileUtil.toFileObject(fil);
        }
        return null;
    }
    

    public static URI convertStringToUri(String str) {
        if (str != null) {
            File fil = new File(str);
            fil = FileUtil.normalizeFile(fil);
            return fil.toURI();
        }
        return null;
    }

    private static final Pattern RELATIVE_SLASH_SEPARATED_PATH = 
            Pattern.compile("[^:/\\\\.][^:/\\\\]*(/[^:/\\\\.][^:/\\\\]*)*"); // NOI18N
     
    /**
     * copied from netbeans.org's ant/project sources. will find out if path is relative or absolute
     */
    public static File resolveFilePath(File basedir, String filename) {
        if (basedir == null) {
            throw new NullPointerException("null basedir passed to resolveFile"); // NOI18N
        }
        if (filename == null) {
            throw new NullPointerException("null filename passed to resolveFile"); // NOI18N
        }
        if (!basedir.isAbsolute()) {
            throw new IllegalArgumentException("nonabsolute basedir passed to resolveFile: " + basedir); // NOI18N
        }
        if (RELATIVE_SLASH_SEPARATED_PATH.matcher(filename).matches()) {
            // Shortcut - simple relative path. Potentially faster.
            return new File(basedir, filename.replace('/', File.separatorChar));
        } else {
            // All other cases.
            String machinePath = filename.replace('/', File.separatorChar).replace('\\', File.separatorChar);
            File f = new File(machinePath);
            if (!f.isAbsolute()) {
                f = new File(basedir, machinePath);
            }
//            assert f.isAbsolute();
            return new File(f.toURI().normalize());
        }
    }
    
   public static URI getDirURI(File root, String path) {
       String pth = path.trim();
       pth = pth.replaceFirst("^\\./", ""); //NOI18N
       pth = pth.replaceFirst("^\\.\\\\", ""); //NOI18N
       File src = FileUtilities.resolveFilePath(root, pth);
       return FileUtil.normalizeFile(src).toURI();
   }
    
   public static URI getDirURI(FileObject root, String path) {
       return getDirURI(FileUtil.toFile(root), path);
   }
    
   
   
    
}
