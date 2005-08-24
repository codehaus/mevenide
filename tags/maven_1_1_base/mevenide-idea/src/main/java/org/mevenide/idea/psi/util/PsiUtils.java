package org.mevenide.idea.psi.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagValue;
import com.intellij.util.IncorrectOperationException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.IDEUtils;

/**
 * PSI utilities.
 *
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

    public static XmlFile findXmlFile(final Project pProject,
                                      final VirtualFile pFile) {
        final FileDocumentManager fileDocMgr = FileDocumentManager.getInstance();
        Document doc = fileDocMgr.getCachedDocument(pFile);
        if (doc == null)
            doc = fileDocMgr.getDocument(pFile);

        return findXmlFile(pProject, doc);
    }

    /**
     * Returns the {@link XmlFile} associated with the specified document in the specified project.
     *
     * @param pProject  the project context
     * @param pDocument the document to find the file for
     *
     * @return the XML project, or {@code null} if the file can't be found (shouldn't happen)
     */
    public static XmlFile findXmlFile(final Project pProject,
                                      final Document pDocument) {
        final PsiDocumentManager psiDocMgr = PsiDocumentManager.getInstance(pProject);

        PsiFile psiFile = psiDocMgr.getCachedPsiFile(pDocument);
        if (psiFile == null)
            psiFile = psiDocMgr.getPsiFile(pDocument);

        if (psiFile == null || psiFile instanceof XmlFile)
            return (XmlFile) psiFile;

        final VirtualFile virtualFile = psiFile.getVirtualFile();
        final String path;
        if (virtualFile != null)
            path = virtualFile.getPath();
        else
            path = "No virtual file.";
        throw new IllegalArgumentException(RES.get("not.xml.document", path));
    }

    /**
     * Returns the {@link XmlFile} associated with the specified document in the specified module.
     *
     * @param pModule   the module context
     * @param pDocument the document to find the file for
     *
     * @return the XML file, or {@code null} if the project can't be found (shouldn't happen)
     */
    public static XmlFile findXmlFile(final Module pModule,
                                      final Document pDocument) {
        return findXmlFile(pModule.getProject(), pDocument);
    }

    public static XmlFile findXmlFile(final Module pModule,
                                      final VirtualFile pFile) {
        return findXmlFile(pModule.getProject(), pFile);
    }

    public static XmlTag[] getPath(final XmlTag pTag) {
        return getPath(pTag, null);
    }

    public static String[] getPathTokens(final XmlTag pTag) {
        final XmlTag[] path = getPath(pTag);
        final String[] tokens = new String[path.length];
        for (int i = 0; i < path.length; i++)
            tokens[i] = path[i].getName();

        return tokens;
    }

    public static XmlTag[] getPath(final XmlTag pTag, final XmlTag[] pBuffer) {
        if (pTag == null)
            return new XmlTag[0];

        final List<XmlTag> tags = new ArrayList<XmlTag>(5);
        XmlTag context = pTag;
        while (context != null) {
            tags.add(0, context);
            context = context.getParentTag();
        }

        final XmlTag[] buffer;
        if (pBuffer == null || pBuffer.length < tags.size())
            buffer = new XmlTag[tags.size()];
        else
            buffer = pBuffer;
        return tags.toArray(buffer);
    }

    /**
     * Convenience method for getting a value from a tag. If the given tag is {@code null}, this
     * method will simply return {@code null} and not fail.
     *
     * @param pTag the tag to retrieve the value from (can be {@code null}
     *
     * @return the text value of the tag (trimmed), or {@code null}
     */
    public static String getTagValue(final XmlTag pTag) {
        if (pTag == null)
            return null;

        final XmlTagValue value = pTag.getValue();
        if (value == null)
            return null;

        final String text = value.getTrimmedText();
        if (text == null || text.trim().length() == 0)
            return null;

        return text;
    }

    public static String getTagValue(final XmlTag pParentTag,
                                     final String pChildTagName) {
        if (pParentTag == null)
            return null;

        final XmlTag childTag = pParentTag.findFirstSubTag(pChildTagName);
        if (childTag == null)
            return null;

        return getTagValue(childTag);
    }

    /**
     * Sets the value of the first tag with the specified name under the given parent tag.
     *
     * <p>If the parent tag contains multiple tags with that name, the first is used.</p>
     *
     * @param pModule    the module context
     * @param pParentTag the parent tag (must not be {@code null}
     * @param pTagName   the child tag name to find (must not be {@code null}
     * @param pValue     the value to set in the tag
     */
    public static void setTagValue(final Module pModule,
                                   final XmlTag pParentTag,
                                   final String pTagName,
                                   final String pValue) {
        setTagValue(pModule.getProject(),
                    pParentTag,
                    pTagName,
                    pValue);
    }

    /**
     * Sets the value of the first tag with the specified name under the given parent tag.
     *
     * <p>If the parent tag contains multiple tags with that name, the first is used.</p>
     *
     * @param pProject   the project context
     * @param pParentTag the parent tag (must not be {@code null}
     * @param pTagName   the child tag name to find (must not be {@code null}
     * @param pValue     the value to set in the tag
     */
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

    /**
     * Sets the value of the given tag.
     *
     * @param pModule the module context
     * @param pTag    the tag (must not be {@code null}
     * @param pValue  the value to set in the tag
     */
    public static void setTagValue(final Module pModule,
                                   final XmlTag pTag,
                                   final String pValue) {
        setTagValue(pModule.getProject(),
                    pTag,
                    pValue);
    }

    /**
     * Sets the value of the given tag.
     *
     * @param pProject the project context
     * @param pTag     the tag (must not be {@code null}
     * @param pValue   the value to set in the tag
     */
    public static void setTagValue(final Project pProject,
                                   final XmlTag pTag,
                                   final String pValue) {
        IDEUtils.runCommand(pProject, new Runnable() {
            public void run() {
                pTag.getValue().setText(pValue);
            }
        });
    }

    /**
     * Returns the tag path as string tokens, concatenating the given tag name to the end of the tag
     * path.
     *
     * @param pTagName the tag name to add at the end of the path
     *
     * @return string array
     */
    public static String[] getPathAndConcat(final XmlTag pTag, final String pTagName) {
        final XmlTag[] parentPath = getPath(pTag);
        final String[] path = new String[parentPath.length + 1];

        for (int i = 0; i < parentPath.length; i++)
            path[i] = parentPath[i].getName();

        path[path.length - 1] = pTagName;
        return path;
    }
}
