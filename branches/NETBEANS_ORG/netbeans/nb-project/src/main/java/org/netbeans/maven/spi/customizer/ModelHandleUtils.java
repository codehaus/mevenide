/*
 *  Copyright 2007 Mevenide Team
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
package org.netbeans.maven.spi.customizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import org.apache.maven.model.Model;
import org.apache.maven.profiles.ProfilesRoot;
import org.netbeans.maven.NbMavenProjectImpl;
import org.netbeans.maven.api.customizer.ModelHandle;
import org.netbeans.maven.configurations.M2Configuration;
import org.netbeans.maven.customizer.CustomizerProviderImpl;
import org.netbeans.maven.embedder.MavenSettingsSingleton;
import org.netbeans.maven.execute.UserActionGoalProvider;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.Project;

/**
 * Some random utility methods to allow post creation modifications of the project model.
 * 
 * @author mkleint
 */
public final class ModelHandleUtils {
    
    private ModelHandleUtils() {}
    
    public static ModelHandle createModelHandle(Project prj) throws IOException, XmlPullParserException {
        NbMavenProjectImpl project = prj.getLookup().lookup(NbMavenProjectImpl.class);
        Model model = project.getEmbedder().readModel(project.getPOMFile());
        ProfilesRoot prof = MavenSettingsSingleton.createProfilesModel(project.getProjectDirectory());
        UserActionGoalProvider usr = project.getLookup().lookup(org.netbeans.maven.execute.UserActionGoalProvider.class);
        ActionToGoalMapping mapping = new NetbeansBuildActionXpp3Reader().read(new StringReader(usr.getRawMappingsAsString()));
        return CustomizerProviderImpl.ACCESSOR.createHandle(model, prof, project.getOriginalMavenProject(), 
                Collections.<String, ActionToGoalMapping>singletonMap(M2Configuration.DEFAULT,mapping), null, null);
    }
    
    public static void writeModelHandle(ModelHandle handle, Project prj) throws IOException {
        NbMavenProjectImpl project = prj.getLookup().lookup(NbMavenProjectImpl.class);
        CustomizerProviderImpl.writeAll(handle, project);
    }
}
