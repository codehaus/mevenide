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
    
    private static HashMap map = new HashMap();
    DefaultModelValidator original;
    
    /** Creates a new instance of NbModelValidator */
    public NbModelValidator() {
        original = new DefaultModelValidator();
    }
    
    public ModelValidationResult getResultFor(String id) {
        synchronized (map) {
            return (ModelValidationResult)map.get(id);
        }
    }

    public ModelValidationResult validate(Model model) {
        ModelValidationResult result = new ModelValidationResult();
        ModelValidationResult orig = original.validate(model);
        synchronized (map) {
            map.put(model.getId(), orig);
        }
        return result;
    }
    
}
