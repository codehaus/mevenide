package org.mevenide.idea.psi.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.IDEUtils;

/**
 * @author Arik
 * @todo add support for attribute selection in path (e.g. like XPath's "@id='abc'")
 */
public class XmlTagPath {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(XmlTagPath.class);

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
    private String path;

    /**
     * Optional parent for the path.
     */
    private final XmlTagPath parent;

    /**
     * Creates an instance using the given file.
     *
     * @param pFile the XML file
     */
    public XmlTagPath(final XmlFile pFile) {
        this(pFile, null);
    }

    /**
     * Creates an instance using the given file and tag path expression.
     *
     * @param pFile the XML file
     * @param pPath the tag path expression
     */
    public XmlTagPath(final XmlFile pFile, final String pPath) {
        if (pFile == null)
            throw new IllegalArgumentException(RES.get("null.arg", "pFile"));
        if (!isValidPath(pPath))
            throw new IllegalArgumentException(RES.get("indexed.first.token.err"));

        parent = null;
        file = pFile;
        path = pPath;
    }

    /**
     * Creates an instance that extends the given parent path with the specified path.
     *
     * @param pParent the parent path to extend
     * @param pPath   the extending path
     */
    public XmlTagPath(final XmlTagPath pParent, final String pPath) {
        if (!isValidPath(pPath))
            throw new IllegalArgumentException(RES.get("indexed.first.token.err"));

        parent = pParent;
        file = parent.getFile();
        path = pPath;
    }

    private static boolean isValidPath(final String pPath) {
        if (pPath == null || pPath.trim().length() == 0)
            return false;

        final String[] pathTokens = pPath.split("/");
        final XmlFilterExpression expr = XmlFilterExpression.create(pathTokens[0]);
        return expr.getIndex() == null;
    }

    /**
     * Returns the path project. The project is derived from the XML file we are searching.
     *
     * @return project
     */
    public final Project getProject() {
        return file.getProject();
    }

    /**
     * Returns the XML file.
     *
     * @return the file
     */
    public final XmlFile getFile() {
        return file;
    }

    /**
     * Returns the tag path parent, if any.
     *
     * @return the parent, or {@code null} if there isn't any
     */
    public XmlTagPath getParent() {
        return parent;
    }

    /**
     * Returns the document (not the xml document).
     *
     * @return idea document
     */
    public final Document getDocument() {
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
     * IncorrectOperationException}, as currently the PSI api does not allow creating a {@link
     * XmlDocument} instance.
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
     * Returns the tag path.
     *
     * @return string
     */
    public final String getPath() {
        return getPath(true);
    }

    /**
     * Returns the tag path, including or excluding the parent path, as specified by the given
     * flag.
     *
     * @param pIncludeParent whether to use the parent path as a prefix
     *
     * @return string
     */
    public final String getPath(final boolean pIncludeParent) {
        if (path == null || path.trim().length() == 0)
            throw new IllegalStateException(RES.get("path.not.set"));

        if (pIncludeParent && parent != null) {
            final String parentPath = parent.getPath();
            if (parentPath.endsWith("/"))
                return parentPath + path;
            else
                return parentPath + "/" + path;
        }
        else
            return path;
    }

    /**
     * Sets the tag path.
     *
     * @param pPath the new path (may be {@code null})
     */
    public final void setPath(final String pPath) {
        path = pPath;
    }

    /**
     * Returns the row for the last path path.
     *
     * <p>If this tag path has no path, this method will throw an {@link IllegalStateException}. If
     * the last tag has no row, {@code null} is returned.</p>
     *
     * @return the row number, or {@code null}
     */
    public final Integer getRow() {
        final String path = getPath(false);

        if (path.endsWith("]")) {
            final int start = path.lastIndexOf('[');
            final String rowStr = path.substring(start + 1, path.length() - 1);
            return Integer.valueOf(rowStr);
        }

        return null;
    }

    /**
     * Sets the row for the last path path.
     *
     * <p>If this tag path has no path, this method will throw an {@link IllegalStateException}. If
     * the last tag already has a row, it is replaced with the new row number.</p>
     *
     * @param pRow the new row number
     */
    public final void setRow(final Integer pRow) {
        final String path = getPath(false);

        final StringBuilder buf = new StringBuilder(path);
        if (path.endsWith("]")) {
            final int start = path.lastIndexOf('[');
            buf.delete(start, buf.length());
        }

        if (pRow != null)
            buf.append('[').append(pRow).append(']');

        setPath(buf.toString());
    }

    /**
     * Returns the tag path as string tokens, where each item in the array is a tag name or
     * expression.
     *
     * @return string array
     */
    public final String[] getPathTokens() {
        return getPathTokens(true);
    }

    /**
     * Returns the tag path as string tokens, where each item in the array is a tag name or
     * expression.
     *
     * @return string array
     */
    public final String[] getPathTokens(final boolean pIncludeParent) {
        return getPath(pIncludeParent).split("/");
    }

    /**
     * Returns the tag path as string tokens, concatenating the given tag name to the end of the tag
     * path.
     *
     * @param pTagName the tag name to add at the end of the path
     *
     * @return string array
     */
    public final String[] getPathAndConcat(final String pTagName) {
        return PsiUtils.getPathAndConcat(getTag(), pTagName);
    }

    /**
     * Returns the root tag.
     *
     * <p>If the root tag does not exist, or if it exists but has the wrong name (according to the
     * tag path expression), {@code null} is returned. </p>
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
     * <p>This method will first check if the XML document already contains a root tag. If so, it
     * will make sure that it matches the tag expression - if it doesn't, an {@link
     * IncorrectOperationException} is thrown, indicating that.</p>
     *
     * <p>Otherwise, if the root tag does exist and satisfy the tag expression, it is simply
     * returned. If it does not exist, it will be created using the tag expression to find its
     * name.</p>
     *
     * @return the root tag (existing, or newly created) - never {@code null}
     * @throws IncorrectOperationException if the root already exists, but does not satisfy the tag
     *                                     path expression
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
     * <p>This method will <b>not</b> create the path, and will return {@code null} if the path
     * cannot be parsed (missing tags).</p>
     *
     * @return the xml tag, or {@code null}
     */
    public final XmlTag getTag() {
        final String[] pathTokens = getPathTokens();
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
            if (context == null)
                break;
        }

        return context;
    }

    /**
     * Returns (but does not create) all xml tags for the tag path expression. The difference
     * between this method and the {@link #getTag()} method is that if the last tag name matches
     * multiple tags, all of them are returned.
     *
     * <p>For example, suppose you use the path "project/goal", and there are multiple "goal" tags
     * under "project" tag, all the "goal" tag instances are returned, whereas the {@link #getTag()}
     * would simply return the first "goal" tag.</p>
     *
     * <p>This method will <b>not</b> create the path, and will return {@code null} if the path
     * cannot be parsed (missing tags).</p>
     *
     * <p>If the path does not fully exist (some tag in the path does not exist), this method will
     * return an empty array, rather than {@code null}. </p>
     *
     * <p><b>NOTE: it is illegal to call this method for a tag path whose last token has an index
     * (e.g. has a {@code [x]} at the end).</b></p>
     *
     * @return an array of xml tags (never {@code null})
     */
    public final XmlTag[] getAllTags() {
        final String[] pathTokens = getPathTokens();
        final XmlTag rootTag = getRootTag();
        if (rootTag == null)
            return new XmlTag[0];

        //
        //The getRootTag method used above already makes sure that the root
        //tag name matches the tag name in the tag-path's first token, and
        //returns null if not, therefor we can safely start the iteration
        //at index 1 rather than 0 (where 0 is the root tag's expression).
        //
        XmlTag context = rootTag;
        for (int i = 1; i < pathTokens.length - 1; i++) {
            final XmlFilterExpression expr =
                    XmlFilterExpression.create(pathTokens[i]);

            context = expr.findChildTag(context);
            if (context == null)
                break;
        }

        if (context != null)
            return context.findSubTags(pathTokens[pathTokens.length - 1]);
        else
            return new XmlTag[0];
    }

    /**
     * Returns the tags in the tag-path as an array. The length of the array will be of the number
     * of tags that exist in the path.
     *
     * <p>For instance, if the path is set to {@code "a/b/c"}, and tag {@code "c"} does not exist,
     * the returned array will be: {@code ["a","b"]}.</p>
     *
     * @return a tag of {@link XmlTag} instances
     */
    public final XmlTag[] getTags() {
        return PsiUtils.getPath(getTag());
    }

    /**
     * Like the {@link #getTag()} method, this method will return the final tag for the tag path.
     *
     * <p>If, however, the tag path cannot be found, it will be created.</p>
     *
     * @return the xml tag
     * @throws IncorrectOperationException if the root tag does not satisfy the tag expression
     *                                     (incorrect name)
     */
    public final XmlTag ensureTag() throws IncorrectOperationException {
        //
        //The getRootTag method used above already makes sure that the root
        //tag name matches the tag name in the tag-path's first token, and
        //returns null if not, therefor we can safely start the iteration
        //at index 1 rather than 0 (where 0 is the root tag's expression).
        //
        XmlTag context = ensureRootTag();
        final String[] pathTokens = getPathTokens();
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

    public final String getStringValue() {
        return PsiUtils.getTagValue(getTag());
    }

    public void setValue(final Object pValue) throws IncorrectOperationException {
        final String value = pValue == null ? null : pValue.toString().trim();
        if(value == null || value.length() == 0 ) {
            XmlTag tag = getTag();
            if(tag != null)
                tag.delete();
        }
        else
            PsiUtils.setTagValue(getProject(), ensureTag(), value);
    }

    public final void setValueProtected(final Object pValue) {
        IDEUtils.runCommand(file.getProject(), new Runnable() {
            public void run() {
                try {
                    setValue(pValue);
                }
                catch (IncorrectOperationException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    private String parseRootTagName() {
        final String[] pathTokens = getPathTokens();
        final XmlFilterExpression expr = XmlFilterExpression.create(pathTokens[0]);
        return expr.getTagName();
    }
}
