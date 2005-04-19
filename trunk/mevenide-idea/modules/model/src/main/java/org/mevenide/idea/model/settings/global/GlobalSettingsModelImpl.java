package org.mevenide.idea.model.settings.global;

import com.intellij.openapi.ui.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.environment.SysEnvLocationFinder;
import org.mevenide.idea.common.util.Res;
import org.mevenide.idea.common.ui.UI;
import org.mevenide.idea.common.settings.global.GlobalSettingsListener;
import org.mevenide.idea.common.settings.global.GlobalSettingsModel;
import org.mevenide.idea.common.settings.global.MavenHomeChangedEvent;

import javax.swing.event.EventListenerList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.EventListener;

/**
 * @author Arik
 */
public class GlobalSettingsModelImpl implements GlobalSettingsModel {
    private static final Res RES = Res.getInstance(GlobalSettingsModelImpl.class);
    private static final Log LOG = LogFactory.getLog(GlobalSettingsModelImpl.class);

    /**
     * Event listener support.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * The maven home.
     */
    private File mavenHome;

    public void initComponent() {
        if (mavenHome == null) {
            final String mavenHomePath = SysEnvLocationFinder.getInstance().getMavenHome();
            if (mavenHomePath != null && mavenHomePath.trim().length() > 0) {
                final File mavenHomeFile = new File(mavenHomePath);
                if (mavenHomeFile.exists())
                    try {
                        setMavenHome(mavenHomeFile);
                    }
                    catch (FileNotFoundException e) {
                        Messages.showErrorDialog(e.getMessage(), UI.ERR_TITLE);
                        LOG.error(e.getMessage(), e);
                    }
            }
        }
    }

    public File getMavenHome() {
        return mavenHome;
    }

    public void setMavenHome(final File pMavenHome) throws FileNotFoundException {
        if (pMavenHome != null && !pMavenHome.exists())
            throw new FileNotFoundException(RES.get("dir.doesnt.exist",
                                                    new Object[]{pMavenHome.getAbsolutePath()}));

        final File oldHome = mavenHome;
        mavenHome = pMavenHome;
        fireMavenHomeChangedEvent(oldHome, mavenHome);
    }

    public void addGlobalSettingsListener(GlobalSettingsListener pListener) {
        listenerList.add(GlobalSettingsListener.class, pListener);
    }

    public void removeGlobalSettingsListener(GlobalSettingsListener pListener) {
        listenerList.remove(GlobalSettingsListener.class, pListener);
    }

    public void fireMavenHomeChangedEvent(final File pOldHome, final File pNewHome) {

        final MavenHomeChangedEvent event = new MavenHomeChangedEvent(this,
                                                                      pOldHome,
                                                                      pNewHome);

        final EventListener[] listeners = listenerList.getListeners(GlobalSettingsListener.class);
        for (int i = 0; i < listeners.length; i++) {
            GlobalSettingsListener listener = (GlobalSettingsListener) listeners[i];
            listener.mavenHomeChanged(event);
        }
    }
}
