package org.mevenide.idea.util;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import java.io.File;

/**
 * @author Arik
 */
public abstract class FileUtils {
    public static VirtualFile find(final String pUrl) {
        final FileRefresher refresher = new FileRefresher(pUrl);
        IDEUtils.runWriteAction(refresher);
        return refresher.getFile();
    }

    public static boolean exists(final VirtualFile pFile) {
        if (pFile == null)
            return false;

        final File file = VfsUtil.virtualToIoFile(pFile);
        return file != null && file.exists();
    }

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
        if (pFile == null)
            return null;

        return VfsUtil.virtualToIoFile(pFile).getAbsolutePath();
    }

    private static class FileRefresher implements Runnable {
        private final String url;
        private VirtualFile file;

        public FileRefresher(final String pUrl) {
            url = pUrl;
        }

        public void run() {
            final VirtualFileManager vfm = VirtualFileManager.getInstance();
            file = vfm.refreshAndFindFileByUrl(url);
        }

        public VirtualFile getFile() {
            return file;
        }
    }
}
