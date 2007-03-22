package org.codehaus.mevenide.idea.xml.impl;

import org.codehaus.mevenide.idea.xml.SettingsDocument;
import org.codehaus.mevenide.idea.xml.impl.JDOMReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SettingsDocumentImpl extends JDOMReader implements SettingsDocument {

    public SettingsDocumentImpl(File file) {
        try {
            init(new FileInputStream(file));
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
