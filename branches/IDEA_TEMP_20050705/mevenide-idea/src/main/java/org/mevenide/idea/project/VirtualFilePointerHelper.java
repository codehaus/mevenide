package org.mevenide.idea.project;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerContainer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerListener;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;

/**
 * @author Arik
 */
public class VirtualFilePointerHelper {

    /**
     * The map of file pointers to acquire their associated PSI projects.
     */
    protected final VirtualFilePointerContainer entries;

    /**
     * Creates an instance for the given project and registers the given
     * listener for pointers events.
     *
     * @param pListener the pointers listener
     */
    public VirtualFilePointerHelper(final VirtualFilePointerListener pListener) {
        final VirtualFilePointerManager mgr = VirtualFilePointerManager.getInstance();
        if(pListener != null)
            entries = mgr.createContainer(pListener);
        else
            entries = mgr.createContainer();
    }

    /**
     * Registers a new entry file with the manager. The file does not have to exist.
     *
     * @param pFile the entry file
     *
     * @return the new entry
     */
    public final VirtualFilePointer add(final VirtualFile pFile) {
        return add(pFile.getUrl());
    }

    /**
     * Registers a new entry with the manager. The file does not have to exist.
     *
     * @param pUrl the file url (must be absolute, with a protocol)
     *
     * @return the new entry
     */
    public VirtualFilePointer add(final String pUrl) {
        entries.add(pUrl);
        return entries.findByUrl(pUrl);
    }

    /**
     * Returns {@code true} if the given file is a registered POM. {@code false} otherwise.
     *
     * @param pFile the file to check
     *
     * @return boolean
     */
    public final boolean isRegistered(final VirtualFile pFile) {
        return isRegistered(pFile.getUrl());
    }

    /**
     * Returns {@code true} if the given url is a registered POM. {@code false} otherwise.
     *
     * @param pUrl the url to check
     *
     * @return boolean
     */
    public boolean isRegistered(final String pUrl) {
        return entries.findByUrl(pUrl) != null;
    }

    public VirtualFilePointer get(final String pUrl) {
        return entries.findByUrl(pUrl);
    }

    public VirtualFilePointer get(final VirtualFile pFile) {
        return entries.findByUrl(pFile.getUrl());
    }

    /**
     * Returns the list of registered POM file pointers.
     *
     * @return list of file pointers
     */
    public final VirtualFilePointer[] getFilePointers() {
        final int count = entries.size();
        final VirtualFilePointer[] buffer = new VirtualFilePointer[count];
        return entries.getList().toArray(buffer);
    }

    /**
     * Returns the list of registered POM urls.
     *
     * @return urls
     */
    public final String[] getUrls() {
        return entries.getUrls();
    }

    /**
     * Removes the POM associated with the specified file from the manager. If the file was not
     * registered, this method does nothing.
     *
     * @param pFile the file to remove
     */
    public final VirtualFilePointer remove(final VirtualFile pFile) {
        return remove(pFile.getUrl());
    }

    /**
     * Removes the POM associated with the specified URL from the manager. If the URL was not
     * registered, this method does nothing.
     *
     * @param pUrl the URL to remove
     */
    public VirtualFilePointer remove(final String pUrl) {
        final VirtualFilePointer pointer = entries.findByUrl(pUrl);
        if (pointer != null)
            entries.remove(pointer);

        return pointer;
    }
}
