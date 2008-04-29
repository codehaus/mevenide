package org.codehaus.mevenide.idea.xml;

import java.io.InputStream;
import java.util.List;

public interface PluginDocument {
    Plugin getPlugin();

    interface Plugin {
        String getGoalPrefix();

        Mojos getMojos();

        String getGroupId();

        String getArtifactId();

        String getVersion();
    }

    interface Mojos {
        List<Mojo> getMojoList();
    }

    interface Mojo {
        String getGoal();
    }


    public class Factory {
        public static PluginDocument parse(InputStream inputStream) {
            return new PluginDocumentImpl(inputStream);
        }
    }
}
