/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.netbeans.api;

import java.io.IOException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.openide.filesystems.FileObject;

/**
 * Various maven model related utilities.
 * @author mkleint
 * @author Anuradha G
 */
public final class ModelUtils {

    /**
     * 
     * @param pom       FolderObject that represent POM 
     * @param group     
     * @param artifact
     * @param version
     * @param type
     * @param scope
     * @param classifier
     * @param acceptNull accept null values to scope,type and classifier.
     *                   If true null values will remove corresponding tag.
     */
    public static void addDependency(FileObject pom,
            String group,
            String artifact,
            String version,
            String type,
            String scope,
            String classifier, boolean acceptNull) {

        Model model = WriterUtils.loadModel(pom);
        if (model != null) {
            Dependency dep = PluginPropertyUtils.checkModelDependency(model, group, artifact, true);
            dep.setVersion(version);
            if (acceptNull || scope != null) {
                dep.setScope(scope);
            }
            if (acceptNull || type != null) {
                dep.setType(type);
            }
            if (acceptNull || classifier != null) {
                dep.setClassifier(classifier);
            }
            try {
                WriterUtils.writePomModel(pom, model);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

  
}
