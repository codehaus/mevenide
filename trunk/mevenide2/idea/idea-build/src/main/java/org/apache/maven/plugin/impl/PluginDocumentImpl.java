package org.apache.maven.plugin.impl;

import org.apache.maven.plugin.MojoDocument;
import org.apache.maven.plugin.PluginDocument;
import org.apache.xmlbeans.JDOMReader;
import org.apache.xmlbeans.XmlOptions;
import org.jdom.Element;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PluginDocumentImpl extends JDOMReader implements PluginDocument {
    public PluginDocumentImpl(InputStream inputStream, XmlOptions xmlOptions) {
        init(inputStream, xmlOptions);
    }

    public Plugin getPlugin() {
        return new Plugin() {
            public String getGoalPrefix() {
                return getChildText(rootElement, "goalPrefix");
            }

            public Mojos getMojos() {
                final Element mojosElement = getChild(rootElement, "mojos");
                return new Mojos() {
                    public List<MojoDocument.Mojo> getMojoList() {
                        final List<MojoDocument.Mojo> list = new ArrayList<MojoDocument.Mojo>();
                        for (final Element mojoElement : getChildren(mojosElement, "mojo")) {
                            list.add(new MojoDocument.Mojo() {
                                public String getGoal() {
                                    return getChildText(mojoElement, "goal");
                                }
                            });
                        }
                        return list;
                    }
                };
            }
        };
    }
}
