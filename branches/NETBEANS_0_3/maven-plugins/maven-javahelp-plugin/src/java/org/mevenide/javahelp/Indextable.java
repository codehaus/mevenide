package org.mevenide.javahelp;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

/**
 * This class adds to Hashtable the ability to hold paths for HelpSet reference IDs and target URLs.
 *
 * @author  Peter Nabbefeld
 */
public class Indextable extends Hashtable {

    private static final String fs = System.getProperty("file.separator");

    private String pathId  = null;

    private String pathUrl = null;

    /** Constructor does nothing special */
    public Indextable() {
    }

    /**
     * Get the path id used as a reference.
     * @return the path id.
     */
    public String getPathId() {
        return pathId;
    }

    /**
     * Get the URL of the document containing the help text.
     * @return the URL.
     */
    public String getPathUrl() {
        return pathUrl;
    }

    /**
     * Calculate the root from the canonical path to the directory and the current file.
     * @param relRoot The path relative to the helpset's root, e.g. org/mevenide/help.
     * @param dir The canonical path of the directory, where the helpset is (may be a subdirectory of the root).
     * @param file The file for which to calculate the relative path.
     * @throws IOException if an I/O error occurs while getting the canonical path of the file.
     */
    public void setPath(String relRoot, String dir, File file) throws IOException {
        String[] s = TargetEncoder.encode(relRoot, dir, file);
        pathId  = s[0];
        pathUrl = s[1];
    }

    /**
     * Calculate the root from the canonical path to the directory, the current file and the target name.
     * @param relRoot The path relative to the helpset's root, e.g. org/mevenide/help.
     * @param dir The canonical path of the directory, where the helpset is (may be a subdirectory of the root).
     * @param file The file for which to calculate the relative path.
     * @param target The relative target address in the file (the part of an URL after '#').
     * @throws IOException if an I/O error occurs while getting the canonical path of the file.
     */
    public void setPath(String relRoot, String dir, File file, String target) throws IOException {
        String[] s = TargetEncoder.encode(relRoot, dir, file, target);
        pathId  = s[0];
        pathUrl = s[1];
    }

    /**
     * Calculate the root from the canonical path to the directory and the current filename. This method first creates a java.io.File object, so the canonical path can be evaluated.
     * @param relRoot The path relative to the helpset's root, e.g. org/mevenide/help.
     * @param dir The canonical path of the directory, where the helpset is (may be a subdirectory of the root).
     * @param fname The name of the file for which to calculate the relative path.
     * @throws IOException if an I/O error occurs while getting the canonical path of the file.
     */
    public void setPath(String relRoot, String dir, String fname) throws IOException {
        String[] s = TargetEncoder.encode(relRoot, dir, fname);
        pathId  = s[0];
        pathUrl = s[1];
    }

    /**
     * Calculate the root from the canonical path to the directory, the current filename and the target name. This method first creates a java.io.File object, so the canonical path can be evaluated.
     * @param relRoot The path relative to the helpset's root, e.g. org/mevenide/help.
     * @param dir The canonical path of the directory, where the helpset is (may be a subdirectory of the root).
     * @param fname The name of the file for which to calculate the relative path.
     * @param target The relative target address in the file (the part of an URL after '#').
     * @throws IOException if an I/O error occurs while getting the canonical path of the file.
     */
    public void setPath(String relRoot, String dir, String fname, String target) throws IOException {
        String[] s = TargetEncoder.encode(relRoot, dir, fname, target);
        pathId  = s[0];
        pathUrl = s[1];
    }

}