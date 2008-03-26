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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.project.build.model.ModelLineage;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.NullEmbedderLogger;

/**
 * Various maven model related utilities.
 * @author mkleint
 */
public final class ModelUtils {

    /**
     * Get all possible profiles defined in pom and it's parents.
     * 
     * @param pom
     * @return
     */
    public static List<String> retrieveAllProfiles(File pom) {
        List<String> values = new ArrayList<String>();
        ModelLineage lin = EmbedderFactory.createModelLineage(pom,
                EmbedderFactory.createExecuteEmbedder(new NullEmbedderLogger()), true);
        List<Model> models = lin.getModelsInDescendingOrder();
        for (Model mdl : models) {
            List<Profile> profs = mdl.getProfiles();
            if (profs != null) {
                for (Profile prof : profs) {
                    values.add(prof.getId());
                }
            }
        }
        return values;
    }
}
