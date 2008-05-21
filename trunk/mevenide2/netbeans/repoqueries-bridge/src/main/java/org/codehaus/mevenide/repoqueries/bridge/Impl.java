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

package org.codehaus.mevenide.repoqueries.bridge;

import java.io.File;
import java.util.List;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.RepositoryQueries;
import org.netbeans.modules.repoqueries.spi.RepositoryQueryImplementation;

/**
 *
 * @author mkleint
 */
public class Impl implements RepositoryQueryImplementation {

    public Result findInRepository(String shaChecksum) {
        List<NBVersionInfo> infos = RepositoryQueries.findBySHA1(shaChecksum);
        if (infos != null && infos.size() > 0) {
            return new Res(infos.get(0));
        }
        return null;
    }
    
    private class Res implements RepositoryQueryImplementation.Result {
        private NBVersionInfo info;
        private Res(NBVersionInfo info) {
            this.info = info;
        }
        public boolean hasJavadoc() {
            return info.isJavadocExists();
        }

        public boolean hasSource() {
            return info.isSourcesExists();
        }

        public void downloadArtifact(File newLocation) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void downloadJavadoc(File newLocation) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void downloadSources(File newLocation) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getMavenId() {
            return info.getGroupId() + ":" + info.getArtifactId() + ":" + info.getVersion() + ":" + info.getClassifier() + ":" + info.getType();
        }
        
    }
            

}
