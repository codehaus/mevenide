package org.mevenide.idea.util.ui.text;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.psi.PsiUtils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @author Arik
 */
public class XmlPsiSwingDocument extends PlainDocument {
    private static final Log LOG = LogFactory.getLog(XmlPsiSwingDocument.class);

    private final Object LOCK = new Object();

    private final Project project;
    private final XmlFile xmlFile;
    private final UpdatePsiCommand command;

    private int inDocumentEventCounter = 0;
    private int inPsiEventCounter = 0;

    public XmlPsiSwingDocument(final Project pProject,
                               final Document pIdeaDocument) {
        this(pProject, pIdeaDocument, (String[]) null);
    }

    public XmlPsiSwingDocument(final Project pProject,
                               final Document pIdeaDocument,
                               final String pElementName) {
        this(pProject, pIdeaDocument, pElementName.split("/"));
    }

    public XmlPsiSwingDocument(final Project pProject,
                               final Document pIdeaDocument,
                               final String[] pElementNames) {
        //
        //initialize instance fields
        //
        project = pProject;
        xmlFile = PsiUtils.findXmlFile(project, pIdeaDocument);

        //
        //create the command which will be used to update the PSI when the document changes
        //
        command = new UpdatePsiCommand(project, xmlFile, pElementNames);

        //
        //make sure we start off with the correct text currently in PSI
        //
        try {
            synchronized (LOCK) {
                final XmlTag psiTag = command.findPsiElement();
                final String text = psiTag == null ? null : psiTag.getValue().getTrimmedText();
                inPsiEventCounter++;
                try {
                    replace(0, getLength(), text, null);
                }
                finally {
                    inPsiEventCounter--;
                }
            }
        }
        catch (BadLocationException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        //
        //add this as a PSI listener, so that we can update this instance when the
        //PSI changes
        //TODO: shouldn't we remove this listener sometime? weak reference?
        //
        PsiManager.getInstance(project).addPsiTreeChangeListener(new XmlPsiListener());
    }

    private void updatePsi() throws BadLocationException {
        synchronized (LOCK) {
            if(inPsiEventCounter > 0 || inDocumentEventCounter > 1)
                return;

            final String str = getText(0, getLength());
            command.executeWriteAction(str);
        }
    }

    @Override public void remove(final int pOffset,
                                 final int pLength) throws BadLocationException {
        synchronized (LOCK) {
            inDocumentEventCounter++;
            try {
                super.remove(pOffset, pLength);
                updatePsi();
            }
            finally {
                inDocumentEventCounter--;
            }
        }
    }

    @Override public void insertString(final int pOffset,
                                       final String pText,
                                       final AttributeSet pAttributes) throws BadLocationException {
        synchronized (LOCK) {
            inDocumentEventCounter++;
            try {
                super.insertString(pOffset, pText, pAttributes);
                updatePsi();
            }
            finally {
                inDocumentEventCounter--;
            }
        }
    }

    @Override public void replace(final int pOffset,
                                  final int pLength,
                                  final String pText,
                                  final AttributeSet pAttributes) throws BadLocationException {
        synchronized (LOCK) {
            inDocumentEventCounter++;
            try {
                super.replace(pOffset, pLength, pText, pAttributes);
                updatePsi();
            }
            finally {
                inDocumentEventCounter--;
            }
        }
    }

    private class XmlPsiListener extends PsiTreeChangeAdapter {

        public void childAdded(PsiTreeChangeEvent event) {
            synchronized (LOCK) {
                if(inDocumentEventCounter > 0)
                    return;

                inPsiEventCounter++;
                try {
                    if(!isOurFile(event))
                        return;

                    //
                    //if we have no tag - than the new child is not us, and there's
                    //no point going forward.
                    //
                    final XmlTag tag = command.findPsiElement();
                    if(tag == null)
                        return;

                    //
                    //if our tag is the new child itself (a whole new tag added),
                    //simply take its text and synchronize it
                    //
                    if(tag == event.getChild() || tag == event.getParent())
                        replace(0, getLength(), tag.getValue().getTrimmedText(), null);
                }
                catch (BadLocationException e) {
                    LOG.error(e.getMessage(), e);
                }
                finally {
                    inPsiEventCounter--;
                }
            }
        }

        public void childRemoved(PsiTreeChangeEvent event) {
            synchronized (LOCK) {
                if(inDocumentEventCounter > 0)
                    return;

                inPsiEventCounter++;
                try {
                    if(!isOurFile(event))
                        return;

                    //if we have no text, no point going forward since we only remove text here
                    final int length = getLength();
                    if(length == 0)
                        return;

                    //
                    //if we have no xml tag, but have content - remedy this by
                    //removing all our content. This will happen even if this
                    //event was triggered for a different text field(!) because
                    //it is an illegal state.
                    //
                    final XmlTag tag = command.findPsiElement();
                    if(tag == null) {
                        replace(0, length, null, null);
                        return;
                    }

                    //
                    //otherwise, if the removed child belongs to our xml tag,
                    //update this document to the new value of our xml tag
                    //
                    if(tag == event.getParent())
                        replace(0, length, tag.getValue().getTrimmedText(), null);
                }
                catch (BadLocationException e) {
                    LOG.error(e.getMessage(), e);
                }
                finally {
                    inPsiEventCounter--;
                }
            }
        }

        public void childReplaced(PsiTreeChangeEvent event) {
            synchronized (LOCK) {
                if(inDocumentEventCounter > 0)
                    return;

                inPsiEventCounter++;
                try {
                    if(!isOurFile(event))
                        return;

                    final int length = getLength();

                    //
                    //if we have no tag - than the new child is not us, and there's
                    //no point going forward.
                    //
                    final XmlTag tag = command.findPsiElement();
                    if(tag == null) {

                        //this has nothing to do with the psi synchronization -
                        //if we have text in the document model, and not in the psi,
                        //synchronize it - just a simple fallback mechanism...
                        if(length > 0)
                            replace(0, length, null, null);
                        return;
                    }

                    //
                    //synchronize the PSI text with the document model text
                    //
                    if(tag == event.getChild() || tag == event.getParent())
                        replace(0, length, tag.getValue().getTrimmedText(), null);
                }
                catch (BadLocationException e) {
                    LOG.error(e.getMessage(), e);
                }
                finally {
                    inPsiEventCounter--;
                }
            }
        }

        private boolean isOurFile(final PsiTreeChangeEvent pEvent) {
            final PsiFile eventFile = pEvent.getFile();
            return pEvent.getManager().getProject().equals(project) &&
                    eventFile != null && eventFile.equals(xmlFile);
        }
    }
}
