package org.mevenide.javahelp;

import java.io.File;
import java.io.IOException;

public class TargetEncoder {

    private static final String fs = System.getProperty("file.separator");

    /** Utility class without instances */
    private TargetEncoder() {
    }

    /** Calculate the root from the canonical path to the directory and the current file */
    public static String[] encode(String dir, File file) throws IOException {
        String dir0 = (dir.endsWith(fs)) ? dir : dir + fs;
        String dir1 = file.getParentFile().getCanonicalPath();
        if (!dir1.endsWith(fs)) {
            dir1 += fs;
        }
        String name = file.getName();
        if (name.lastIndexOf('.') >= 0) {
            name = name.substring(0, name.lastIndexOf('.'));
        }
        String s = (fs.length() > 1) ?
                        (dir1 + name).substring(dir0.length()).replaceAll(fs, ".")
                        :
                        (dir1 + name).substring(dir0.length()).replace(fs.charAt(0), '/');    ;
        return new String[]{ s.replace('/', '.'), s};
    }

    /** Calculate the root from the canonical path to the directory, the current file and the target name */
    public static String[] encode(String dir, File file, String target) throws IOException {
        String[] s = encode(dir, file);
        s[0] += "." + target.replace(' ', '_');
        s[1] += "#" + target;
        return s;
    }

    public static String[] encode(String dir, String fname) throws IOException {
        return encode(dir, new File(fname));
    }

    public static String[] encode(String dir, String fname, String target) throws IOException {
        return encode(dir, new File(fname), target);
    }

}