package org.apache.maven.settings.x100.impl;

import org.apache.maven.settings.x100.SettingsDocument;
import org.apache.xmlbeans.JDOMReader;
import org.apache.xmlbeans.XmlOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SettingsDocumentImpl extends JDOMReader implements SettingsDocument {

    public SettingsDocumentImpl(File file, XmlOptions xmlOptions) {
        try {
            init(new FileInputStream(file), xmlOptions);
        } catch (IOException ignore) {
        }
    }

    public Settings getSettings() {
        return new Settings () {
            public String getLocalRepository() {
                return getChildText(rootElement, "localRepository");
            }
        };
    }
}
