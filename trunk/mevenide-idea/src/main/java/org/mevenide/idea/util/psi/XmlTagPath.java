package org.mevenide.idea.util.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.mevenide.idea.Res;

/**
 * @author Arik
 */
public final class XmlTagPath {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(XmlTagPath.class);

    /**
     * The XML file this path is using.
     */
    private final XmlFile file;

    /**
     * The tag expression path.
     */
    private final String path;

    /**
     * Creates an instance using the given file and tag path expression.
     *
     * @param pFile the XML file
     * @param pPath the tag path expression
     * @todo move messages to resource bundle
     */
    public XmlTagPath(final XmlFile pFile, final String pPath) {
        if (pFile == null)
            throw new IllegalArgumentException(RES.get("null.arg", "pFile"));
        if (pPath == null || pPath.trim().length() == 0)
            throw new IllegalArgumentException(RES.get("null.arg", "pPath"));

        final String[] pathTokens = pPath.split("/");
        final XmlFilterExpression expr = XmlFilterExpression.create(pathTokens[0]);
        if (expr.getIndex() != null)
            throw new IllegalArgumentException("The first path token cannot have an index.");

        file = pFile;
        path = pPath;
    }

    /**
     * Returns the path project. The project is derived from the XML file
     * we are searching.
     *
     * @return project
     */
    public Project getProject() {
        return file.getProject();
    }

    /**
     * Returns the XML file.
     *
     * @return the file
     */
    public XmlFile getFile() {
        return file;
    }

    /**
     * Returns the document (not the xml document).
     *
     * @return idea document
     */
    public Document getDocument() {
        final VirtualFile virtualFile = file.getVirtualFile();
        final FileDocumentManager fileMgr = FileDocumentManager.getInstance();
        return fileMgr.getDocument(virtualFile);
    }

    /**
     * Returns the XML document, or {@code null} if it does not exist.
     *
     * @return xml document
     */
    private XmlDocument getXmlDocument() {
        return file.getDocument();
    }

    /**
     * Returns the XML document. If there is no xml document, this method will throw a {@link
     * IncorrectOperationException}, as currently the PSI api does not allow creating a {@link XmlDocument} instance.
     *
     * @return xml document (never {@code null})
     * @throws IncorrectOperationException
     */
    private XmlDocument ensureXmlDocument() throws IncorrectOperationException {
        final XmlDocument xmlDocument = getXmlDocument();
        if (xmlDocument == null)
            throw new IncorrectOperationException(RES.get("missing.xml.document"));

        return xmlDocument;
    }

    /**
     * Returns the root tag.
     *
     * <p>If the root tag does not exist, or if it exists but has the wrong name (according to the tag path expression),
     * {@code null} is returned. </p>
     *
     * @return the root tag, or {@code null}
     */
    private XmlTag getRootTag() {
        final XmlDocument xmlDocument = getXmlDocument();
        if (xmlDocument == null)
            return null;
        else {
            final XmlTag rootTag = xmlDocument.getRootTag();
            if (rootTag == null)
                return null;

            if (!rootTag.getName().equals(parseRootTagName()))
                return null;

            return rootTag;
        }
    }

    /**
     * Returns (and creates if necessary) the root tag.
     *
     * <p>This method will first check if the XML document already contains a root tag. If so, it will make sure that it
     * matches the tag expression - if it doesn't, an {@link IncorrectOperationException} is thrown, indicating
     * that.</p>
     *
     * <p>Otherwise, if the root tag does exist and satisfy the tag expression, it is simply returned. If it does not
     * exist, it will be created using the tag expression to find its name.</p>
     *
     * @return the root tag (existing, or newly created) - never {@code null}
     * @throws IncorrectOperationException if the root already exists, but does not satisfy the tag path expression
     */
    private XmlTag ensureRootTag() throws IncorrectOperationException {
        final XmlDocument xmlDocument = ensureXmlDocument();
        XmlTag rootTag = xmlDocument.getRootTag();
        if (rootTag != null) {
            final String rootTagName = rootTag.getName();
            if (!rootTagName.equals(parseRootTagName()))
                throw new IncorrectOperationException(
                    RES.get("incorrect.root.tag", rootTagName));
            else
                return rootTag;
        }

        final Project project = file.getProject();
        final PsiManager psiMgr = PsiManager.getInstance(project);
        final PsiElementFactory eltFactory = psiMgr.getElementFactory();

        final String tagExpr = "<" + parseRootTagName() + "/>";
        rootTag = eltFactory.createTagFromText(tagExpr);
        return (XmlTag) xmlDocument.add(rootTag);
    }

    /**
     * Returns (but does not create) the xml tag for the tag path expression.
     *
     * <p>This method will <b>not</b> create the path, and will return {@code null} if the path cannot be parsed
     * (missing tags).</p>
     *
     * @return the xml tag, or {@code null}
     */
    public XmlTag getTag() {
        final String[] pathTokens = path.split("/");
        final XmlTag rootTag = getRootTag();
        if (rootTag == null)
            return null;

        //
        //The getRootTag method used above already makes sure that the root
        //tag name matches the tag name in the tag-path's first token, and
        //returns null if not, therefor we can safely start the iteration
        //at index 1 rather than 0 (where 0 is the root tag's expression).
        //
        XmlTag context = rootTag;
        for (int i = 1; i < pathTokens.length; i++) {
            final XmlFilterExpression expr =
                XmlFilterExpression.create(pathTokens[i]);

            context = expr.findChildTag(context);
            if(context == null)
                break;
        }

        return context;
    }

    /**
     * Like the {@link #getTag()} method, this method will return the final tag for the tag path.
     *
     * <p>If, however, the tag path cannot be found, it will be created.</p>
     *
     * @return the xml tag
     * @throws IncorrectOperationException if the root tag does not satisfy the tag expression (incorrect name)
     */
    public XmlTag ensureTag() throws IncorrectOperationException {
        //
        //The getRootTag method used above already makes sure that the root
        //tag name matches the tag name in the tag-path's first token, and
        //returns null if not, therefor we can safely start the iteration
        //at index 1 rather than 0 (where 0 is the root tag's expression).
        //
        XmlTag context = ensureRootTag();
        final String[] pathTokens = path.split("/");
        for (int i = 1; i < pathTokens.length; i++) {
            final XmlFilterExpression expr =
                XmlFilterExpression.create(pathTokens[i]);

            XmlTag child = expr.findChildTag(context);
            if (child == null) {
                child = context.createChildTag(
                    expr.getTagName(),
                    context.getNamespace(),
                    null,
                    false);
                child = (XmlTag) context.add(child);
            }

            context = child;
        }

        return context;
    }

    private String parseRootTagName() {
        final String[] pathTokens = path.split("/");
        final XmlFilterExpression expr = XmlFilterExpression.create(pathTokens[0]);
        return expr.getTagName();
    }
}
