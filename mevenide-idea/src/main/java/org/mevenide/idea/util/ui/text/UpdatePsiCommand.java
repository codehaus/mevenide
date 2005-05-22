package org.mevenide.idea.util.ui.text;

import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.IDEUtils;

/**
 * @author Arik
 */
class UpdatePsiCommand implements Runnable {
    private static final Log LOG = LogFactory.getLog(UpdatePsiCommand.class);

    private final Object LOCK = new Object();

    private final Project project;
    private final XmlFile xmlFile;
    private final String[] childrenPath;

    private String text = null;
    private boolean textWasSet = false;

    UpdatePsiCommand(final Project pProject,
                     final XmlFile pXmlFile,
                     final String[] pElementNames) {
        if(pElementNames == null)
            throw new NullPointerException("pElementNames must be a legal array (can be empty)");

        project = pProject;
        xmlFile = pXmlFile;
        childrenPath = pElementNames;
    }

    public XmlTag findPsiElement() {
        try {
            return findPsiElement(false);
        }
        catch (IncorrectOperationException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public XmlTag findPsiElement(final boolean pCreateIfNotFound) throws IncorrectOperationException {
        final XmlDocument xmlDocument = xmlFile.getDocument();
        XmlTag context = xmlDocument.getRootTag();

        for (final String childName : childrenPath) {
            XmlTag child = context.findFirstSubTag(childName);
            if (child == null) {
                if(!pCreateIfNotFound)
                    return null;

                child = context.createChildTag(
                        childName,
                        context.getNamespace(),
                        null,
                        false);
                context.add(child);
            }
            context = child;
        }

        return context;
    }

    public XmlTag findPsiElement(final String pContent) throws IncorrectOperationException {
        final XmlDocument xmlDocument = xmlFile.getDocument();
        boolean elementCreated = false;
        XmlTag context = xmlDocument.getRootTag();

        for (int i = 0; i < childrenPath.length; i++) {
            final String childName = childrenPath[i];
            XmlTag child = context.findFirstSubTag(childName);
            if (child == null) {
                final boolean lastInPath = i == childrenPath.length - 1;
                if(lastInPath)
                    elementCreated = true;

                child = context.createChildTag(
                    childName,
                    context.getNamespace(),
                    lastInPath ? pContent : null,
                    false);
                context.add(child);
            }
            context = child;
        }

        if(!elementCreated && context != null)
            context.getValue().setText(pContent);

        return context;
    }

    public void executeWriteAction(final String pText) {
        synchronized(LOCK) {
            text = pText;
            textWasSet = true;
            try {
                IDEUtils.runCommand(project, this);
            }
            finally {
                textWasSet = false;
            }
        }
    }

    public void run() {
        synchronized(LOCK ) {
            if(!textWasSet)
                throw new IllegalStateException("Text was not set on this runnable.");

            try {
                if(text == null || text.length() == 0) {
                    final XmlTag elt = findPsiElement();
                    if(elt != null)
                        elt.delete();
                }
                else
                    findPsiElement(text);
            }
            catch (IncorrectOperationException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
