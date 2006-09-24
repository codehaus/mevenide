/*
 * NbModelValidator.java
 *
 * Created on February 24, 2006, 5:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.embedder;

import java.util.HashMap;
import org.apache.maven.model.Model;
import org.apache.maven.project.validation.DefaultModelValidator;
import org.apache.maven.project.validation.ModelValidationResult;
import org.apache.maven.project.validation.ModelValidator;

/**
 *
 * @author mkleint
 */
public class NbModelValidator implements ModelValidator {
    
    private static ThreadLocal listener = new ThreadLocal();
    
    /** Creates a new instance of NbModelValidator */
    public NbModelValidator() {
    }
    
    /**
     * use in the IDE environment to set the validator into the embedder
     */
    public static void setDelegateValidator(ModelValidator listen) {
        listener.set(listen);
    }
    
    public static void clearModelValidator() {
        // 1.5 equivalent is remove()
        listener.set(null);
    }
    
    private ModelValidator getDelegate() {
        Object ret = listener.get();
        if (ret != null) {
            return (ModelValidator)ret;
        }
        return null;
    }
    
    public ModelValidationResult validate(Model model) {
        if (getDelegate() != null) {
            return getDelegate().validate(model);
        }
        return new DefaultModelValidator().validate(model);
    }
    
    public static class Delegate implements ModelValidator {
        
        ModelValidationResult result;
        DefaultModelValidator original;
        
        public Delegate() {
            original = new DefaultModelValidator();
        }
        
        public ModelValidationResult getValidationResult() {
            return result;
        }
        
        public ModelValidationResult validate(Model model) {
            ModelValidationResult orig = original.validate(model);
            result = orig;
            return orig;
        }
    }
    
}
