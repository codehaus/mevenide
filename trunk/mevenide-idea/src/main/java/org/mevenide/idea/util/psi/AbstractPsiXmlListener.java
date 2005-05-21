package org.mevenide.idea.util.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Arik
 */
public abstract class AbstractPsiXmlListener extends AbstractPsiListener {
    private static final Log LOG = LogFactory.getLog(AbstractPsiXmlListener.class);

    protected final XmlFile xmlFile;

    protected AbstractPsiXmlListener(final XmlFile pXmlFile) {
        super(pXmlFile);
        xmlFile = (XmlFile) psiFile;
    }

    @Override protected void appendProperties(final StringBuilder buf,
                                              final PsiElement pElt,
                                              final boolean pDeep) {
        super.appendProperties(buf, pElt, pDeep);

        if(!(pElt instanceof XmlTag))
            return;

        final XmlTag xmlElt = (XmlTag) pElt;

        //
        //append attributes
        //
        final XmlAttribute[] attrs = xmlElt.getAttributes();
        buf.append("attrs={");
        for(XmlAttribute attr : attrs)
            buf.append(attr.getName()).append('=').append(attr.getValue()).append(',');
        if(buf.charAt(buf.length() - 1) == ',')
            buf.replace(buf.length() - 1, buf.length(), "");
        buf.append('}');

        buf.append("value=").append(xmlElt.getValue().getText());
    }

    @Override public void childAdded(PsiTreeChangeEvent event) {
        if(!isEventRelevant(event))
            return;

        final PsiElement elt = event.getChild();
        if(elt instanceof XmlTag)
            tagAdded(event);
        else if(elt instanceof XmlText)
            textAdded(event);
    }

    @Override public void childRemoved(PsiTreeChangeEvent event) {
        if(!isEventRelevant(event))
            return;

        final PsiElement elt = event.getChild();
        if(elt instanceof XmlTag)
            tagRemoved(event);
        else if(elt instanceof XmlText)
            textRemoved(event);
    }

    @Override public void childMoved(PsiTreeChangeEvent event) {
        if(!isEventRelevant(event))
            return;

        final PsiElement elt = event.getChild();
        if(elt instanceof XmlTag)
            tagMoved(event);
        else if(elt instanceof XmlText)
            textMoved(event);
    }

    @Override public void childrenChanged(PsiTreeChangeEvent event) {
        if(!isEventRelevant(event))
            return;

        LOG.trace("childrenChanged");
    }

    @Override public void childReplaced(PsiTreeChangeEvent event) {
        if(!isEventRelevant(event))
            return;

        final PsiElement elt = event.getChild();
        if(elt instanceof XmlTag)
            tagReplaced(event);
        else if(elt instanceof XmlText)
            textReplaced(event);
    }

    protected void tagAdded(final PsiTreeChangeEvent event) {}

    protected void textAdded(final PsiTreeChangeEvent event) {}

    protected void tagMoved(final PsiTreeChangeEvent event) {}

    protected void textMoved(final PsiTreeChangeEvent event) {}

    protected void tagReplaced(final PsiTreeChangeEvent event) {}

    protected void textReplaced(final PsiTreeChangeEvent event) {}

    protected void tagRemoved(final PsiTreeChangeEvent pEvent) {}

    protected void textRemoved(final PsiTreeChangeEvent pEvent) {}
}
