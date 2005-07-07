package org.mevenide.idea.project.support;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import java.beans.PropertyChangeEvent;
import javax.swing.event.EventListenerList;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.util.components.AbstractProjectComponent;

/**
 * @author Arik
 */
public abstract class AbstractPomSettingsManager extends AbstractProjectComponent {
    /**
     * Event listeners support.
     */
    protected final EventListenerList listenerList = new EventListenerList();

    protected AbstractPomSettingsManager(final Project pProject) {
        super(pProject);
    }

    protected final boolean isRegistered(final String pUrl) {
        return PomManager.getInstance(project).contains(pUrl);
    }

    protected final boolean isValid(final String pUrl) {
        return PomManager.getInstance(project).isValid(pUrl);
    }

    protected final <T> T get(final Key<T> pKey, final String pUrl) {
        final VirtualFilePointer file = PomManager.getInstance(project).getPointer(pUrl);
        if (file == null)
            return null;

        return file.getUserData(pKey);
    }

    protected final <T> void put(final Key<T> pKey, final String pUrl, final T pValue) {
        final VirtualFilePointer file = PomManager.getInstance(project).getPointer(pUrl);
        if (file == null)
            return;

        file.putUserData(pKey, pValue);
    }

    protected final VirtualFile getFile(final String pUrl) {
        final VirtualFilePointer filePointer = PomManager.getInstance(project).getPointer(pUrl);
        if (filePointer == null)
            return null;

        return filePointer.getFile();
    }

    protected static class PomPropertyChangeEvent<SourceType,ValueType>
            extends PropertyChangeEvent {
        private final String url;

        protected PomPropertyChangeEvent(final SourceType source,
                                         final String pUrl,
                                         final String propertyName,
                                         final ValueType oldValue,
                                         final ValueType newValue) {
            super(source, propertyName, oldValue, newValue);
            url = pUrl;
        }

        @Override
        public ValueType getNewValue() {
            //noinspection UNCHECKED_WARNING
            return (ValueType) super.getNewValue();
        }

        @Override
        public ValueType getOldValue() {
            //noinspection UNCHECKED_WARNING
            return (ValueType) super.getOldValue();
        }

        public String getUrl() {
            return url;
        }

        @Override
        public SourceType getSource() {
            //noinspection UNCHECKED_WARNING
            return (SourceType) super.getSource();
        }
    }

}
