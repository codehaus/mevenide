package org.mevenide.javahelp;

import java.io.File;
import java.io.IOException;

/**
 * This is a utility class for Indextable. The encode methods return an array of two strings. The first one is a reference id, separating elements with dots and without file extensions, the second is an URL to the referenced document.
 */
public class TargetEncoder {

    private static final String fs = System.getProperty("file.separator");

    /** Utility class without instances */
    private TargetEncoder() {
    }

    /**
     * Calculate the root from the canonical path to the directory and the current file.
     * @param relRoot The path relative to the helpset's root, e.g. org/mevenide/help.
     * @param dir The canonical path of the directory, where the helpset is (may be a subdirectory of the root).
     * @param file The file for which to calculate the relative path.
     * @throws IOException if an I/O error occurs while getting the canonical path of the file.
     * @return an array of String, @see #TargetEncoder.
     */
    public static String[] encode(String relRoot, String dir, File file) throws IOException {
        String dir0 = (relRoot.endsWith("/")) ? relRoot : relRoot + '/';
        String dir1 = (dir.endsWith(fs)) ? dir : dir + fs;
        String dir2 = file.getParentFile().getCanonicalPath();
        dir0 = dir0.replace('/', '.');
        if (!dir2.endsWith(fs)) {
            dir2 += fs;
        }
        String name = file.getName();
        String ext  = "";
        int pos;
        if ((pos = name.lastIndexOf('.')) >= 0) {
            ext  = name.substring(pos);
            name = name.substring(0, pos);
        }
        String s = (fs.length() > 1) ?
                        (dir2 + name).substring(dir1.length()).replaceAll(fs, "/")
                        :
                        (dir2 + name).substring(dir1.length()).replace(fs.charAt(0), '/');
        return new String[]{ dir0 + s.replace('/', '.'), s + ext};
    }

    /**
     * Calculate the root from the canonical path to the directory, the current file and the target name.
     * @param relRoot The path relative to the helpset's root, e.g. org/mevenide/help.
     * @param dir The canonical path of the directory, where the helpset is (may be a subdirectory of the root).
     * @param file The file for which to calculate the relative path.
     * @param target The relative target address in the file (the part of an URL after '#').
     * @throws IOException if an I/O error occurs while getting the canonical path of the file.
     * @return an array of String, @see #TargetEncoder.
     */
    public static String[] encode(String relRoot, String dir, File file, String target) throws IOException {
        String[] s = encode(relRoot, dir, file);
        s[0] += "." + target.replace(' ', '_');
        s[1] += "#" + target;
        return s;
    }

    /**
     * Calculate the root from the canonical path to the directory, the current filename and the target name. This method first creates a java.io.File object, so the canonical path can be evaluated.
     * @param relRoot The path relative to the helpset's root, e.g. org/mevenide/help.
     * @param dir The canonical path of the directory, where the helpset is (may be a subdirectory of the root).
     * @param fname The name of the file for which to calculate the relative path.
     * @throws IOException if an I/O error occurs while getting the canonical path of the file.
     * @return an array of String, @see #TargetEncoder.
     */
    public static String[] encode(String relRoot, String dir, String fname) throws IOException {
        return encode(relRoot, dir, new File(fname));
    }

    /**
     * Calculate the root from the canonical path to the directory, the current filename and the target name. This method first creates a java.io.File object, so the canonical path can be evaluated.
     * @param relRoot The path relative to the helpset's root, e.g. org/mevenide/help.
     * @param dir The canonical path of the directory, where the helpset is (may be a subdirectory of the root).
     * @param fname The name of the file for which to calculate the relative path.
     * @param target The relative target address in the file (the part of an URL after '#').
     * @throws IOException if an I/O error occurs while getting the canonical path of the file.
     * @return an array of String, @see #TargetEncoder.
     */
    public static String[] encode(String relRoot, String dir, String fname, String target) throws IOException {
        return encode(relRoot, dir, new File(fname), target);
    }

}