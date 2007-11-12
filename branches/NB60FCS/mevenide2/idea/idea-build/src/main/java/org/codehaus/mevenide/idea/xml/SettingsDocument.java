package org.codehaus.mevenide.idea.xml;

import java.io.File;

public interface SettingsDocument {
    Settings getSettings();

    interface Settings {

        String getLocalRepository();
    }

    public class Factory {
        public static SettingsDocument parse(File file) {
            return new SettingsDocumentImpl( file);
        }
    }
}
