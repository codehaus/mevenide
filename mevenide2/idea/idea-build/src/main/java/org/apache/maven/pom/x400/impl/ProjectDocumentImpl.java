package org.apache.maven.pom.x400.impl;

import org.apache.maven.pom.x400.Plugin;
import org.apache.maven.pom.x400.ProjectDocument;
import org.apache.xmlbeans.JDOMReader;
import org.apache.xmlbeans.XmlOptions;
import org.jdom.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ProjectDocumentImpl extends JDOMReader implements ProjectDocument {

    public ProjectDocumentImpl(File file, XmlOptions xmlOptions) {
        try {
            init(new FileInputStream(file), xmlOptions);
        } catch (FileNotFoundException ignored) {
        }
    }

    public Project getProject() {
        return new Project() {
            public String getName() {
                return getChildText(rootElement, "name");
            }

            public String getArtifactId() {
                return getChildText(rootElement, "artifactId");
            }

            public Build getBuild() {
                final Element buildElement = getChild(rootElement, "build");
                return new Build() {

                    public Plugins getPlugins() {
                        final Element pluginsElement = getChild(buildElement, "plugins");
                        return new Plugins() {

                            public List<Plugin> getPluginList() {
                                final List<Plugin> list = new ArrayList<Plugin>();
                                for (final Element pluginElement : getChildren(pluginsElement, "plugin")) {
                                    list.add(new Plugin() {
                                        public String getGroupId() {
                                            return getChildText(pluginElement, "groupId");
                                        }

                                        public String getArtifactId() {
                                            return getChildText(pluginElement, "artifactId");
                                        }

                                        public String getVersion() {
                                            return getChildText(pluginElement, "version");
                                        }
                                    });
                                }
                                return list;
                            }
                        };
                    }
                };
            }
        };
    }
}
