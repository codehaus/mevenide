/*
 * XdocsCollector.java
 *
 * Created on 11. September 2004, 10:50
 */

package org.mevenide.javahelp;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.channels.FileChannel;

import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Utility class to collect xdoc files from different source directories.
 *
 * @author Peter Nabbefeld
 */
public class XdocsCollector {
    
    private static class XdocsFilter implements FileFilter {
        public boolean accept(File file) {
            if (file.isDirectory())
                return true;
            return file.getName().toLowerCase().endsWith(".xml");
        }
    }

    /** Creates a new instance of XdocsCollector */
    private XdocsCollector() {
    }
    
    /**
     * This method is for collecting different xdocs to create html files for the helpset from them.
     * @param dest The destination directory for the collection of xdoc files.
     * @param src The source directory for additional xdoc files not contained in the set of existing (and reusable) files.
     * @throws NullPointerException if either dest or src are null.
     * @throws InvalidArgumentException if either dest or src are not directories.
     * @throws FileNotFoundException if either argument is not null and does not exist.
     */
    public static void collect(String dest, String src) throws SAXException, ParserConfigurationException, FileNotFoundException, IOException {
        collect(dest, src, null, null);
    }
    
    /**
     * This method is for collecting different xdocs to create html files for the helpset from them.
     * @param dest The destination directory for the collection of xdoc files.
     * @param src The source directory for additional xdoc files not contained in the set of existing (and reusable) files.
     * @param ref The xml file containing references to reusable files that are copied into the dest folder (before copying files from the src folder).
     * @throws NullPointerException if either the dest argument is null or src and ref are both null.
     * @throws InvalidArgumentException if either dest or src are not directories or if ref is not an xml file.
     * @throws FileNotFoundException if either argument is not null and does not exist.
     */
    public static void collect(String dest, String src, String ref) throws SAXException, ParserConfigurationException, FileNotFoundException, IOException {
        collect(dest, src, ref, null);
    }
    
    /**
     * This method is for collecting different xdocs to create html files for the helpset from them.
     * @param dest The destination directory for the collection of xdoc files.
     * @param src The source directory for additional xdoc files not contained in the set of existing (and reusable) files.
     * @param ref The xml file containing references to reusable files that are copied into the dest folder (before copying files from the src folder).
     * @param target The target in the references file to be used for copying of xdocs. Only used if ref not null. If target is null, "default" is used.
     * @throws NullPointerException if either the dest argument is null or src and ref are both null.
     * @throws InvalidArgumentException if either dest or src are not directories or if ref is not an xml file.
     * @throws FileNotFoundException if either argument is not null and does not exist.
     */
    public static void collect(String dest, String src, String ref, String target) throws SAXException, ParserConfigurationException, FileNotFoundException, IOException {
        if (dest == null || (src == null && ref == null))
            throw new NullPointerException();
        File dFile = new File(dest);
        File sFile = (src == null) ? null : new File(src);
        File rFile = (ref == null) ? null : new File(ref);
        String tgt = target.trim();
        if (!dFile.isDirectory())
            throw new IllegalArgumentException("Destination is not a directory");
        if (sFile != null) {
            if (!sFile.exists())
                throw new FileNotFoundException(src);
            if (!sFile.isDirectory())
                throw new IllegalArgumentException("Source is not a directory");
        }
        if (rFile != null) {
            if (!rFile.exists())
                throw new FileNotFoundException(ref);
            if (!rFile.isFile() || rFile.getName().length() < 5 || !ref.toLowerCase().endsWith(".xml"))
                throw new IllegalArgumentException("Xdoc reference is not an xml file");
            if (tgt == null || tgt.length() < 1)
                tgt = "default";
        }
        if (rFile != null)
            copyReferencedFiles(dFile, rFile, tgt);
        if (sFile != null)
            copyEditedFiles(dFile, sFile);
    }
    
    private static void copyEditedFiles(File dest, File src) throws IOException {
        File[] files = src.listFiles(new XdocsFilter());
        File   file;
        File   df;
        for(int i = 0; i < files.length; i++) {
            file = files[i];
            if (file.isFile()) {
                df = new File(dest.getCanonicalPath() + System.getProperty("file.separator") + file.getName());
                copyFile(df, file);
            } else if (file.isDirectory()) {
                df = new File(dest.getCanonicalPath() + System.getProperty("file.separator") + file.getName());
                copyEditedFiles(df, file);
            }
        }
        df = null;
    }
    private static void copyReferencedFiles(File dst, File src, String target) throws SAXException, ParserConfigurationException, IOException {
        XmlToFilenameMapper xmlprov = new XmlToFilenameMapper(dst, src);
        File[] files;
        xmlprov.init(target);
        while (xmlprov.hasNext()) {
            files = xmlprov.next();
            copyFile(xmlprov.getDestination(files), xmlprov.getSource(files));
        }
        xmlprov = null;
        files   = null;
    }
    private static void copyFile(File dst, File src) throws IOException {
        if (dst.exists())
            throw new IOException("File already exists: " + dst.getName());
        // Create channel on the source
        FileChannel srcChannel = new FileInputStream(src).getChannel();
        // Create channel on the destination
        FileChannel dstChannel = new FileOutputStream(dst).getChannel();
        // Copy file contents from source to destination
        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
        // Close the channels
        srcChannel.close();
        dstChannel.close();
    }
}
