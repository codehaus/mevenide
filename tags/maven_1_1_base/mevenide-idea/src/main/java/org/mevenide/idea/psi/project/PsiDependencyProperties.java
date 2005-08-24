package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.XmlPsiObject;
import org.mevenide.idea.util.event.BeanRowsObservable;
import org.mevenide.idea.util.event.PropertyObservable;

/**
 * @author Arik
 */
public interface PsiDependencyProperties
        extends PropertyObservable, BeanRowsObservable, XmlPsiObject,
                PsiChild<PsiDependencies> {
    String getProperty(String pPropertyName);

    void setProperty(String pPropertyName, Object pValue);

    String[] getPropertyNames();

    void renameProperty(String pPropertyName, String pNewPropertyName);

    String getUnknownPropertyName();
}
