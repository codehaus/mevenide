package org.mevenide.idea.util.ui.text;

import com.intellij.psi.xml.XmlFile;
import javax.swing.text.JTextComponent;
import org.mevenide.idea.util.psi.XmlSwingDocument;
import org.mevenide.idea.util.psi.XmlTagPath;

/**
 * @author Arik
 */
public class XmlPsiDocumentBinder {
    /**
     * The project this binder will find documents for.
     */
    private final XmlFile file;

    /**
     * Creates a binder instance for the given project and document.
     *
     * @param pFile the XML file to bind to
     */
    public XmlPsiDocumentBinder(final XmlFile pFile) {
        file = pFile;
    }

    /**
     * Bind the given text component to the specified element path in this binder's project and document.
     *
     * @param pComponent the component to bind
     * @param pTagPath   the element path the component will be bound to - can be a path of elements, a-la {@code
     *                   project/build/sourceDirectory}.
     */
    public XmlSwingDocument bind(final JTextComponent pComponent,
                                 final String pTagPath) {
        //
        //create the listener
        //
        final XmlSwingDocument model = new XmlSwingDocument(file, pTagPath);
        //
        //bind the new listener
        //
        pComponent.setDocument(model);
        return model;
    }

    /**
     * Bind the given text component to the specified element path in this binder's project and document.
     *
     * @param pComponent the component to bind
     * @param pTagPath   the element path the component will be bound to
     */
    public XmlSwingDocument bind(final JTextComponent pComponent,
                                 final XmlTagPath pTagPath) {
        //
        //create the listener
        //
        final XmlSwingDocument model = new XmlSwingDocument(pTagPath);

        //
        //bind the new listener
        //
        pComponent.setDocument(model);
        return model;
    }

}
