package org.codehaus.mevenide.idea.config;

import org.apache.xmlbeans.XmlOptions;
import org.codehaus.mevenide.idea.config.impl.IdeaMavenPluginDocumentImpl;

import java.net.URL;

public interface IdeaMavenPluginDocument {
    IdeaMavenPlugin getIdeaMavenPlugin();
    
    interface IdeaMavenPlugin {

        PluginConfigDocument.PluginConfig getPluginConfig();
    } 

    public class Factory {
        public static IdeaMavenPluginDocument parse(URL resource, XmlOptions xmlOptions) {
            return new IdeaMavenPluginDocumentImpl(resource, xmlOptions);
        }
    }
}
