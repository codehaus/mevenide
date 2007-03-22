package org.codehaus.mevenide.idea.xml.impl;

import org.codehaus.mevenide.idea.xml.MavenDefaultsDocument;
import org.jdom.Element;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MavenDefaultsDocumentImpl extends JDOMReader implements MavenDefaultsDocument {

    public MavenDefaultsDocumentImpl(URL resource) {
        try {
            init(resource.openStream());
        } catch (IOException ignore) {
        }
    }

    public MavenDefaultsDocument.IdeaMavenPlugin getIdeaMavenPlugin() {
        return new IdeaMavenPlugin(){
            public PluginConfig getPluginConfig() {
                final Element pluginConfigElement = getChild(rootElement, "plugin-config");
                return new PluginConfig() {
                    public Maven getMaven() {
                        final Element mavenElement = getChild(pluginConfigElement, "maven");
                        return new Maven () {
                            public Goals getGoals() {
                                final Element goalsElement = getChild(mavenElement,"goals");
                                return new Goals(){
                                    public Standard getStandard() {
                                        final Element standardElement = getChild(goalsElement, "standard");
                                        return new Standard() {
                                            public List<Goal> getGoalList() {
                                                final List<Goal> list = new ArrayList<Goal>();
                                                for (final Element goal : getChildren(standardElement, "goal")) {
                                                    list.add(new Goal() {
                                                        public Name getName() {
                                                            return new Name() {
                                                                public String toString() {
                                                                    return getChildText(goal, "name");
                                                                }
                                                            };
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
                };
            }
        };
    }
}
