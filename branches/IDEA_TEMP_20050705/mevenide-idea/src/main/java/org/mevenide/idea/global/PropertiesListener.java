package org.mevenide.idea.global;

import java.util.EventListener;

/**
 * @author Arik
 */
public interface PropertiesListener extends EventListener {
    void propertiesChanged(PropertiesEvent pEvent);

}
