package org.mevenide.javahelp;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class Indextable extends Hashtable {

    private static final String fs = System.getProperty("file.separator");

    private String pathId  = null;
    private String pathUrl = null;

    /** Constructor does nothing special */
    public Indextable() {
    }

    public String getPathId() {
        return pathId;
    }

    public String getPathUrl() {
        return pathUrl;
    }

    /** Calculate the root from the canonical path to the directory and the current file */
    public void setPath(String dir, File file) throws IOException {
        String[] s = TargetEncoder.encode(dir, file);
        pathId  = s[0];
        pathUrl = s[1];
    }

    /** Calculate the root from the canonical path to the directory, the current file and the target name */
    public void setPath(String dir, File file, String target) throws IOException {
        String[] s = TargetEncoder.encode(dir, file, target);
        pathId  = s[0];
        pathUrl = s[1];
    }

    /** Calculate the root from the canonical path to the directory and the current filename */
    public void setPath(String dir, String fname) throws IOException {
        String[] s = TargetEncoder.encode(dir, fname);
        pathId  = s[0];
        pathUrl = s[1];
    }

    /** Calculate the root from the canonical path to the directory, the current filename and the target name */
    public void setPath(String dir, String fname, String target) throws IOException {
        String[] s = TargetEncoder.encode(dir, fname, target);
        pathId  = s[0];
        pathUrl = s[1];
    }

}