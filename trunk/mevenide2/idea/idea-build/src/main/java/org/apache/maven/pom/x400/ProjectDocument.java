package org.apache.maven.pom.x400;

import org.apache.xmlbeans.XmlOptions;
import org.apache.maven.pom.x400.impl.ProjectDocumentImpl;

import java.io.File;
import java.util.List;

public interface ProjectDocument {
    Project getProject();

    interface Project {
        String getName();

        String getArtifactId();

        Build getBuild();

        interface Build {

            Plugins getPlugins();

            interface Plugins {
                List<Plugin> getPluginList();
            }
        }
    }

    public class Factory {
        public static ProjectDocument parse(File file, XmlOptions xmlOptions) {
            return new ProjectDocumentImpl( file, xmlOptions );
        }
    }
}
