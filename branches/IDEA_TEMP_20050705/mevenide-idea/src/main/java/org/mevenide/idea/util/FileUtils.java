package org.mevenide.idea.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VfsUtil;
import java.io.File;

/**
 * @author Arik
 */
public abstract class FileUtils {
    public static boolean equals(final File pFirst, final File pSecond) {
        if (pFirst == pSecond)
            return true;

        if (pFirst == null || pSecond == null)
            return false;

        return pFirst.equals(pSecond);
    }

    public static String fixPath(final VirtualFile pFile) {
        final StringBuilder buf = new StringBuilder(pFile.getPath());
        if (pFile.getPath().endsWith("!/"))
            buf.delete(buf.length() - 2, buf.length());

        if (pFile.getPath().endsWith("!"))
            buf.delete(buf.length() - 1, buf.length());

        return buf.toString();
    }

    public static String getAbsolutePath(final VirtualFile pFile) {
        return VfsUtil.virtualToIoFile(pFile).getAbsolutePath();
    }
}
