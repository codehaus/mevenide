package org.codehaus.mevenide.idea.config.impl;

import org.apache.xmlbeans.JDOMReader;
import org.apache.xmlbeans.XmlOptions;
import org.codehaus.mevenide.idea.config.GoalDocument;
import org.codehaus.mevenide.idea.config.IdeaMavenPluginDocument;
import org.codehaus.mevenide.idea.config.NameDocument;
import org.codehaus.mevenide.idea.config.PluginConfigDocument;
import org.jdom.Element;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IdeaMavenPluginDocumentImpl extends JDOMReader implements IdeaMavenPluginDocument {

    public IdeaMavenPluginDocumentImpl(URL resource, XmlOptions xmlOptions) {
        try {
            init(resource.openStream(), xmlOptions);
        } catch (IOException ignore) {
        }
    }

    public IdeaMavenPluginDocument.IdeaMavenPlugin getIdeaMavenPlugin() {
        return new IdeaMavenPluginDocument.IdeaMavenPlugin(){
            public PluginConfigDocument.PluginConfig getPluginConfig() {
                final Element pluginConfigElement = getChild(rootElement, "plugin-config");
                return new PluginConfigDocument.PluginConfig() {
                    public Maven getMaven() {
                        final Element mavenElement = getChild(pluginConfigElement, "maven");
                        return new Maven () {
                            public Goals getGoals() {
                                final Element goalsElement = getChild(mavenElement,"goals");
                                return new Goals(){
                                    public Standard getStandard() {
                                        final Element standardElement = getChild(goalsElement, "standard");
                                        return new Standard() {
                                            public List<GoalDocument.Goal> getGoalList() {
                                                final List<GoalDocument.Goal> list = new ArrayList<GoalDocument.Goal>();
                                                for (final Element goal : getChildren(standardElement, "goal")) {
                                                    list.add(new GoalDocument.Goal() {
                                                        public NameDocument.Name.Enum getName() {
                                                            return new NameDocument.Name.Enum() {
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
