package org.mevenide.idea.util.ui.text;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

import javax.swing.text.JTextComponent;

import org.mevenide.idea.Res;

/**
 * @author Arik
 */
public class XmlPsiDocumentBinder {
    /**
     * Resources.
     */
    private static final Res RES = Res.getInstance(XmlPsiDocumentBinder.class);

    /**
     * The project this binder will find documents for.
     */
    private final Project project;

    /**
     * The document that text components will be bound to.
     */
    private final Document document;

    /**
     * Creates a binder instance for the given project and document.
     *
     * @param pProject the project
     * @param pDocument the document that bound text components will update/listen
     */
    public XmlPsiDocumentBinder(final Project pProject, final Document pDocument) {
        project = pProject;
        document = pDocument;
    }

    /**
     * Bind the given text component to the specified element path in this binder's
     * project and document.
     *
     * @param pComponent the component to bind
     * @param pElementName the element path the component will be bound to - can be a path of elements, a-la {@code project/build/sourceDirectory}.
     */
    public void bind(final JTextComponent pComponent,final String pElementName) {
        //
        //create the listener
        //
        final XmlPsiSwingDocument model = new XmlPsiSwingDocument(project,
                                                                  document,
                                                                  pElementName);
        //
        //bind the new listener
        //
        pComponent.setDocument(model);
    }

    /**
     * Bind the given text component to the specified element path in this binder's
     * project and document.
     *
     * @param pComponent the component to bind
     * @param pElementNames the element path the component will be bound to
     */
    public void bind(final JTextComponent pComponent, final String[] pElementNames) {
        //
        //create the listener
        //
        final XmlPsiSwingDocument model = new XmlPsiSwingDocument(project,
                                                                  document,
                                                                  pElementNames);
        //
        //bind the new listener
        //
        pComponent.setDocument(model);
    }

    static XmlFile findXmlFile(final Project pProject, final Document pDocument) {
        final PsiDocumentManager psiDocMgr = PsiDocumentManager.getInstance(pProject);

        PsiFile psiFile = psiDocMgr.getCachedPsiFile(pDocument);
        if(psiFile == null)
            psiFile = psiDocMgr.getPsiFile(pDocument);

        if(psiFile == null || psiFile instanceof XmlFile)
            return (XmlFile) psiFile;

        throw new IllegalArgumentException(RES.get("not.xml.document", psiFile.getVirtualFile().getPath()));
    }
}
