package org.mevenide.idea.util;

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
}
