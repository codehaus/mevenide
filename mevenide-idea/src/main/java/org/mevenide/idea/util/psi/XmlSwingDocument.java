package org.mevenide.idea.util.psi;

import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.psi.util.XmlTagPath;
import org.mevenide.idea.util.IDEUtils;

/**
 * @author Arik
 */
public class XmlSwingDocument extends PlainDocument implements SimplePsiListener {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(XmlSwingDocument.class);

    /**
     * The path parser.
     */
    private final XmlTagPath path;

    /**
     * The source of current modification.
     */
    private final AtomicReference<ModificationSource> modSrc = new AtomicReference<ModificationSource>(
        null);

    public XmlSwingDocument(final XmlFile pFile, final String pPath) {
        this(new XmlTagPath(pFile, pPath));
    }

    public XmlSwingDocument(final XmlTagPath pPath) {
        path = pPath;

        final XmlTag tag = path.getTag();
        if (tag != null) {
            try {
                final String text = tag.getValue().getTrimmedText();
                replace(0, getLength(), text, null);
            }
            catch (BadLocationException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        final PsiManager psiMgr = PsiManager.getInstance(path.getProject());
        final XmlFile file = path.getFile();
        psiMgr.addPsiTreeChangeListener(new PsiSimpleChangeNotifier(file, this));
    }

    private void updatePsiModel() {
        if (modSrc.compareAndSet(null, ModificationSource.UI))
            try {
                IDEUtils.runCommand(path.getProject(), new PsiUpdateRunnable());
            }
            finally {
                modSrc.set(null);
            }
    }

    public void replace(int offset, int length, String text, AttributeSet attrs)
        throws BadLocationException {
        super.replace(offset, length, text, attrs);
        updatePsiModel();
    }

    public void insertString(int offs, String str, AttributeSet a)
        throws BadLocationException {
        super.insertString(offs, str, a);
        updatePsiModel();
    }

    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        updatePsiModel();
    }

    public ModificationSource getModificationSource() {
        return modSrc.get();
    }

    public void setModificationSource(final ModificationSource pSource) {
        modSrc.set(pSource);
    }

    public void refreshModel(final PsiEventType pEventType,
                             final PsiTreeChangeEvent pEvent) {
        final XmlTag tag = path.getTag();
        try {
            if (tag == null)
                replace(0, getLength(), "", null);
            else {
                final String text = tag.getValue().getTrimmedText();
                replace(0, getLength(), text, null);
            }
        }
        catch (BadLocationException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private class PsiUpdateRunnable implements Runnable {
        public void run() {
            try {
                final String text = getText(0, getLength());
                if (text == null || text.trim().length() == 0) {
                    final XmlTag tag = path.getTag();
                    if (tag != null)
                        tag.delete();
                }
                else
                    path.ensureTag().getValue().setText(text);
            }
            catch (BadLocationException e) {
                LOG.error(e.getMessage(), e);
            }
            catch (IncorrectOperationException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
