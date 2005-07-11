package org.mevenide.idea.synchronize;

import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Arik Kfir
 */
public interface FileProblemInfo extends ProblemInfo {
    VirtualFile getFile();

}
