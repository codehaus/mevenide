package org.codehaus.mevenide.idea.xml;

import org.codehaus.mevenide.idea.xml.impl.MavenDefaultsDocumentImpl;

import java.net.URL;
import java.util.List;

public interface MavenDefaultsDocument {
    IdeaMavenPlugin getIdeaMavenPlugin();
    
    interface IdeaMavenPlugin {
        PluginConfig getPluginConfig();
    } 

    public interface PluginConfig {
        Maven getMaven();
    }

    interface Maven {
        Goals getGoals();
    }

    interface Goals {
        Standard getStandard();
    }

    interface Standard {
        List<Goal> getGoalList();
    }

    public interface Goal {
        public Name getName();
    }

    interface Name {
    }

    public class Factory {
        public static MavenDefaultsDocument parse(URL resource) {
            return new MavenDefaultsDocumentImpl(resource);
        }
    }
}
