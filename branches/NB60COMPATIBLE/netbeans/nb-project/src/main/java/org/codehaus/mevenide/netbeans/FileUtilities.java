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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.io.jdom.NetbeansBuildActionJDOMWriter;
import org.codehaus.plexus.util.IOUtil;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.JDOMFactory;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

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
    
    public static FileObject[] convertStringsToFileObjects(List<String> strings) {
        FileObject[] fos = new FileObject[strings.size()];
        int index = 0;
        Iterator<String> it = strings.iterator();
        while (it.hasNext()) {
            String str = it.next();
            File fil = new File(str);
            fil = FileUtil.normalizeFile(fil);
            fos[index] = FileUtil.toFileObject(fil);
            index++;
        }
        return fos;
    }
    
    public static File[] convertStringsToNormalizedFiles(List strings) {
        File[] fos = new File[strings.size()];
        int index = 0;
        Iterator it = strings.iterator();
        while (it.hasNext()) {
            String str = (String)it.next();
            File fil = new File(str);
            fil = FileUtil.normalizeFile(fil);
            fos[index] = fil;
            index++;
        }
        return fos;
    }
    
    
    /**
     * inspired by netbeans's org.apache.tools.ant.module.api.support.ActionUtils.findSelectedFiles
     */
    public static FileObject[] findSelectedFiles(Lookup context, FileObject dir, String suffix) {
        if (dir != null && !dir.isFolder()) {
            throw new IllegalArgumentException("Not a folder: " + dir); // NOI18N
        }
        if (suffix != null && suffix.indexOf('/') != -1) {
            throw new IllegalArgumentException("Cannot includes slashes in suffix: " + suffix); // NOI18N
        }
        List files = new ArrayList();
        Iterator it = context.lookup(new Lookup.Template(DataObject.class)).allInstances().iterator();
        while (it.hasNext()) {
            DataObject d = (DataObject)it.next();
            FileObject f = d.getPrimaryFile();
            boolean matches = FileUtil.toFile(f) != null;
            if (dir != null) {
                matches &= (FileUtil.isParentOf(dir, f) || dir == f);
            }
            if (suffix != null) {
                matches &= f.getNameExt().endsWith(suffix);
            }
            if (matches) {
                files.add(f);
            }
        }
        return (FileObject[])files.toArray(new FileObject[files.size()]);
    }
    
    /**
     * just gets the array of FOs from lookup.
     */
    public static FileObject[] extractFileObjectsfromLookup(Lookup lookup) {
        List files = new ArrayList();
        Iterator it = lookup.lookup(new Lookup.Template(DataObject.class)).allInstances().iterator();
        while (it.hasNext()) {
            DataObject d = (DataObject)it.next();
            FileObject f = d.getPrimaryFile();
            files.add(f);
        }
        return (FileObject[])files.toArray(new FileObject[files.size()]);
    }

    /**
     * delete a file or dir, recursively.
     */
    public static void delete(File file) {
        if ( file.isFile() ) {
            file.delete();
        }
        else {
            File[] files = file.listFiles();
            if ( files != null ) {
                for (int i = 0; i < files.length; i++) {
                    delete(files[i]);
                }
            }
            file.delete();
        }
    }
    
//    /**
//     * for source java files returns a their respective test (same name + Test) if it exists.
//     * for test sources, returns the smae fileobject.
//     *for anything else, returns null.
//     *
//     */
//    public static FileObject findTestForFile(MavenProject project, FileObject f) {
//        if (f == null || !"java".equals(f.getExt())) { //NOI18N
//            return null;
//        }
//        File testRootFile = new File(project.getBTestSrcDirectory());
//        FileObject testroot = FileUtil.toFileObject(testRootFile);
//        if (testroot != null && FileUtil.isParentOf(testroot, f)) {
//            return f;
//        }
//        FileObject srcroot = FileUtil.toFileObject(new File(project.getSrcDirectory()));
//        if (srcroot != null && FileUtil.isParentOf(srcroot, f)) {
//            String relative = FileUtil.getRelativePath(srcroot, f);
//            relative = relative.substring(0, relative.length() - f.getNameExt().length());
//            File testFile = new File(testRootFile, relative + f.getName() + "Test.java"); //NOI18N
//            if (testFile.exists()) {
//                return FileUtil.toFileObject(testFile);
//            }
//        }
//        return null;
//    }
    
    
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
       pth = pth.replaceFirst("^\\./", "");
       pth = pth.replaceFirst("^\\.\\\\", "");
       File src = FileUtilities.resolveFilePath(root, pth);
       return FileUtil.normalizeFile(src).toURI();
   }
    
   public static URI getDirURI(FileObject root, String path) {
       return getDirURI(FileUtil.toFile(root), path);
   }
    
   
   public static void writeNbActionsModel(final FileObject pomDir, final ActionToGoalMapping mapping) throws IOException {
        pomDir.getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {
            public void run() throws IOException {
                JDOMFactory factory = new DefaultJDOMFactory();
                
                InputStream inStr = null;
                FileLock lock = null;
                OutputStreamWriter outStr = null;
                try {
                    Document doc;
                    FileObject fo = pomDir.getFileObject(UserActionGoalProvider.FILENAME);
                    if (fo == null) {
                        fo = pomDir.createData(UserActionGoalProvider.FILENAME);
                        doc = factory.document(factory.element("actions"));
                    } else {
                        //TODO..
                        inStr = fo.getInputStream();
                        SAXBuilder builder = new SAXBuilder();
                        doc = builder.build(inStr);
                        inStr.close();
                        inStr = null;
                    }
                    lock = fo.lock();
                    NetbeansBuildActionJDOMWriter writer = new NetbeansBuildActionJDOMWriter();
                    String encoding = mapping.getModelEncoding() != null ? mapping.getModelEncoding() : "UTF-8";
                    outStr = new OutputStreamWriter(fo.getOutputStream(lock), encoding);
                    Format form = Format.getRawFormat().setEncoding(encoding);
                    writer.write(mapping, doc, outStr, form);
                } catch (JDOMException exc){
                    throw (IOException) new IOException("Cannot parse the nbactions.xml by JDOM.").initCause(exc);
                } finally {
                    IOUtil.close(inStr);
                    IOUtil.close(outStr);
                    if (lock != null) {
                        lock.releaseLock();
                    }
                    
                }
            }
        });
    }
   
    
}
