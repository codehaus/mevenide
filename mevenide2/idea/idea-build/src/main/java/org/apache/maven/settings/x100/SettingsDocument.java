package org.apache.maven.settings.x100;

import org.apache.xmlbeans.XmlOptions;
import org.apache.maven.settings.x100.impl.SettingsDocumentImpl;

import java.io.File;

public interface SettingsDocument {
    Settings getSettings();

    interface Settings {

        String getLocalRepository();
    }

    public class Factory {
        public static SettingsDocument parse(File file, XmlOptions xmlOptions) {
            return new SettingsDocumentImpl( file, xmlOptions);
        }
    }
}
