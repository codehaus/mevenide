package org.mevenide.idea.util.psi;

/**
 * An enum specifying the types of events raised by the PSI tree.
 *
 * @author Arik
 */
public enum PsiEventType {
    CHILD_ADDED,
    CHILD_MOVED,
    CHILD_REPLACED,
    CHILD_REMOVED,
    CHILDREN_CHANGED,
    PROPERTY_CHANGED
}
