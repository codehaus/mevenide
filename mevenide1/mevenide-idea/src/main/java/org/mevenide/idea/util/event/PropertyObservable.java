package org.mevenide.idea.util.event;

import com.jgoodies.binding.beans.Observable;
import java.beans.PropertyChangeListener;

/**
 * @author Arik
 */
public interface PropertyObservable extends Observable {
    /**
     * Adds a <code>PropertyChangeListener</code> to the listener list for the given property name.
     * The listener is registered only for the specified property.
     *
     * @param pListener the PropertyChangeListener to be added
     *
     * @see #removePropertyChangeListener(String, java.beans.PropertyChangeListener)
     */
    void addPropertyChangeListener(String pPropertyName,
                                   PropertyChangeListener pListener);


    /**
     * Removes a <code>PropertyChangeListener</code> from the listener list for the given property
     * name. This method should be used to remove PropertyChangeListeners that were registered for
     * the specific property name.
     *
     * @param pListener the PropertyChangeListener to be removed
     *
     * @see #addPropertyChangeListener(String, PropertyChangeListener)
     */
    void removePropertyChangeListener(String pPropertyName,
                                      PropertyChangeListener pListener);
}
