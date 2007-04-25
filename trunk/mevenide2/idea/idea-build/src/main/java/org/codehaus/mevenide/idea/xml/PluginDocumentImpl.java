package org.codehaus.mevenide.idea.xml;

import org.codehaus.mevenide.idea.xml.impl.JDOMReader;
import org.jdom.Element;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PluginDocumentImpl extends JDOMReader implements PluginDocument {
    public PluginDocumentImpl(InputStream inputStream) {
        init(inputStream);
    }

    public Plugin getPlugin() {
        return new Plugin() {
            public String getGoalPrefix() {
                return getChildText(rootElement, "goalPrefix");
            }

            public Mojos getMojos() {
                final Element mojosElement = getChild(rootElement, "mojos");
                return new Mojos() {
                    public List<Mojo> getMojoList() {
                        final List<Mojo> list = new ArrayList<Mojo>();
                        for (final Element mojoElement : getChildren(mojosElement, "mojo")) {
                            list.add(new Mojo() {
                                public String getGoal() {
                                    return getChildText(mojoElement, "goal");
                                }
                            });
                        }
                        return list;
                    }
                };
            }

            public String getGroupId() {
                return getChildText(rootElement, "groupId");
            }

            public String getArtifactId() {
                return getChildText(rootElement, "artifactId");
            }

            public String getVersion() {
                return getChildText(rootElement, "version");
            }
        };
    }
}
