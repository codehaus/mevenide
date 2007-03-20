package org.apache.xmlbeans;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JDOMReader {

    protected Element rootElement;
    protected Namespace namespace;

    public void init(InputStream is, XmlOptions xmlOptions) {
        try {
            rootElement = new SAXBuilder().build(is).getRootElement();
        } catch (JDOMException ignore) {
        } catch (IOException ignore) {
        }
        this.namespace = rootElement.getNamespace();
    }

    public Element getChild(Element element, String tag) {
        return element == null ? null : element.getChild(tag, namespace);
    }

    public List<Element> getChildren(Element element, String tag) {
        return element == null ? new ArrayList<Element>() : (List<Element>)element.getChildren(tag, namespace);
    }

    public String getChildText(Element element, String tag) {
        return element == null ? null : element.getChildText(tag, namespace);
    }
}
