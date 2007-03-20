package org.apache.xmlbeans;

import org.jdom.Namespace;

import java.util.Map;

public class XmlOptions {
    private Map<String, String> xmlOptionsMap;

    public void setLoadSubstituteNamespaces(final Map<String, String> xmlOptionsMap) {
        this.xmlOptionsMap = xmlOptionsMap;
    }

    public Namespace getNamespace () {
        return Namespace.getNamespace(xmlOptionsMap.get(""));
    }
}
