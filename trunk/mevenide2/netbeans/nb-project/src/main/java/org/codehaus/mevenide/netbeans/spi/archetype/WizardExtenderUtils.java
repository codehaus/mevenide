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
package org.codehaus.mevenide.netbeans.spi.archetype;

import java.io.IOException;
import java.io.StringReader;
import org.apache.maven.model.Model;
import org.apache.maven.profiles.ProfilesRoot;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import org.codehaus.mevenide.netbeans.customizer.CustomizerProviderImpl;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Some random utility methods to allow post creation modifications of the project model.
 * 
 * @author mkleint
 */
public final class WizardExtenderUtils {
    
    private WizardExtenderUtils() {}
    
    public static ModelHandle createModelHandle(NbMavenProject project) throws IOException, XmlPullParserException {
        Model model = project.getEmbedder().readModel(project.getPOMFile());
        ProfilesRoot prof = MavenSettingsSingleton.createProfilesModel(project.getProjectDirectory());
        UserActionGoalProvider usr = project.getLookup().lookup(org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider.class);
        ActionToGoalMapping mapping = new NetbeansBuildActionXpp3Reader().read(new StringReader(usr.getRawMappingsAsString()));
        return CustomizerProviderImpl.ACCESSOR.createHandle(model, prof, project.getOriginalMavenProject(), mapping);
    }
    
    public static void writeModelHandle(ModelHandle handle, NbMavenProject project) throws IOException {
        CustomizerProviderImpl.writeAll(handle, project);
    }
}
