package org.mevenide.idea.global.properties;

import java.util.EventObject;

/**
 * @author Arik
 */
public class PropertiesEvent extends EventObject {
    public PropertiesEvent(final PropertiesManager source) {
        super(source);
    }

    @Override
    public PropertiesManager getSource() {
        return (PropertiesManager) super.getSource();
    }
}
