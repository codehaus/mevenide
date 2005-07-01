package org.mevenide.idea.util.psi;

/**
 * An enum for specifying what is the source of current event.
 *
 * <p>When the user modifies the UI (not from the text editor), the {@link
 * SimplePsiListener#getModificationSource} method should return {@link #UI}. If the user
 * modifies using the text editor, the method should return {@link #EDITOR}.
 *
 * <p>This is done because the code responding to UI modifications updates the PSI tree,
 * which invokes the code responding to PSI modifications, which updates the UI - this can
 * cause an infinite loop, so we need to know who started the loop to avoid it.</p>
 */
public enum ModificationSource {
    UI,
    EDITOR
}
