/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.idea.util.components;

import com.intellij.openapi.util.UserDataHolderBase;
import com.jgoodies.binding.beans.ExtendedPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;

/**
 * @author Arik
 */
public abstract class AbstractIdeaComponent extends UserDataHolderBase {
    /**
     * A lock object, which can be used by implementors to synchronize operations. <p>This does not
     * mean that implementors MUST use it - use at your own discretion if needed.</p>
     */
    protected final Object LOCK = new Object();

    /**
     * The component name.
     */
    protected final String NAME;

    /**
     * Resources.
     */
    protected final Res RES;

    /**
     * The logger to use.
     */
    protected final Log LOG;

    /**
     * Event listener support.
     */
    protected final ExtendedPropertyChangeSupport changeSupport = new ExtendedPropertyChangeSupport(
            this);

    /**
     * Creates an instance with a given log, resources and name.
     */
    protected AbstractIdeaComponent() {
        NAME = getClass().getName();
        RES = Res.getInstance(getClass());
        LOG = LogFactory.getLog(getClass());
    }

    public String getComponentName() {
        return NAME;
    }

    public void addPropertyChangeListener(final PropertyChangeListener pListener) {
        changeSupport.addPropertyChangeListener(pListener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener pListener) {
        changeSupport.removePropertyChangeListener(pListener);
    }

    public void addPropertyChangeListener(final String pPropertyName,
                                          final PropertyChangeListener pListener) {
        changeSupport.addPropertyChangeListener(pPropertyName, pListener);
    }

    public void removePropertyChangeListener(final String pPropertyName,
                                             final PropertyChangeListener pListener) {
        changeSupport.removePropertyChangeListener(pPropertyName, pListener);
    }

    public void disposeComponent() {
    }

    public void initComponent() {
    }
}
