package org.mevenide.idea.util.psi;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagValue;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.IDEUtils;

/**
 * @author Arik
 */
public abstract class PsiUtils {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(PsiUtils.class);

    /**
     * Resources.
     */
    private static final Res RES = Res.getInstance(PsiUtils.class);

    public static XmlFile findXmlFile(final Project pProject, final Document pDocument) {
        final PsiDocumentManager psiDocMgr = PsiDocumentManager.getInstance(pProject);

        PsiFile psiFile = psiDocMgr.getCachedPsiFile(pDocument);
        if (psiFile == null)
            psiFile = psiDocMgr.getPsiFile(pDocument);

        if (psiFile == null || psiFile instanceof XmlFile)
            return (XmlFile) psiFile;

        throw new IllegalArgumentException(RES.get("not.xml.document",
                                                   psiFile.getVirtualFile().getPath()));
    }

    public static String getTagValue(final XmlTag pTag) {
        if (pTag == null)
            return null;

        final XmlTagValue value = pTag.getValue();
        if (value == null)
            return null;

        return value.getTrimmedText();
    }

    public static void setTagValue(final Project pProject,
                                   final XmlTag pParentTag,
                                   final String pTagName,
                                   final String pValue) {
        IDEUtils.runCommand(pProject, new Runnable() {
            public void run() {
                final XmlTag child = pParentTag.findFirstSubTag(pTagName);
                if (child != null) {
                    child.getValue().setText(pValue);
                }
                else {
                    final XmlTag newChild = pParentTag.createChildTag(
                            pTagName,
                            pParentTag.getNamespace(),
                            pValue,
                            false);
                    try {
                        pParentTag.add(newChild);
                    }
                    catch (IncorrectOperationException e) {
                        LOG.error(e.getMessage(), e);
                    }

                }
            }
        });
    }

    public static void setTagValue(final Project pProject,
                                   final XmlTag pParentTag,
                                   final String pValue) {
        IDEUtils.runCommand(pProject, new Runnable() {
            public void run() {
                pParentTag.getValue().setText(pValue);
            }
        });
    }
}
