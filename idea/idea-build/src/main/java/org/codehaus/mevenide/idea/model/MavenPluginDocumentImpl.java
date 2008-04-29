/* ==========================================================================
 * Copyright 2006 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */



package org.codehaus.mevenide.idea.model;

import org.codehaus.mevenide.idea.xml.PluginDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenPluginDocumentImpl implements MavenPluginDocument {
    private PluginDocument pluginDocument;
    private String pluginPath;
    private List<String> pluginGoalList = new ArrayList<String>();

    public MavenPluginDocumentImpl(PluginDocument pluginDocument, String pluginPath ) {
        this.pluginDocument = pluginDocument;
        this.pluginPath = pluginPath;

        pluginGoalList = new ArrayList<String>();
        for (PluginDocument.Mojo mojo : pluginDocument.getPlugin().getMojos().getMojoList()) {
            pluginGoalList.add(mojo.getGoal());
        }
    }

    public String toString() {
        return pluginDocument.getPlugin().getGoalPrefix();
    }

    public PluginDocument getPluginDocument() {
        return pluginDocument;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public List<String> getPluginGoalList() {
        return pluginGoalList;
    }
}
