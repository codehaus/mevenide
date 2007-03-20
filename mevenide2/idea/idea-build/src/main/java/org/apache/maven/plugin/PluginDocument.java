package org.apache.maven.plugin;

import org.apache.xmlbeans.XmlOptions;
import org.apache.maven.plugin.impl.PluginDocumentImpl;

import java.io.InputStream;
import java.util.List;

public interface PluginDocument {
    Plugin getPlugin();

    interface Plugin {

        String getGoalPrefix();

        Mojos getMojos();

        interface Mojos {
            List<MojoDocument.Mojo> getMojoList();
        }
    }

    public class Factory {
        public static PluginDocument parse(InputStream inputStream, XmlOptions xmlOptions) {
            return new PluginDocumentImpl(inputStream, xmlOptions);
        }
    }
}
