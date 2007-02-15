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

import org.apache.maven.plugin.PluginDocument;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenPluginDocumentImpl implements MavenPluginDocument {
    private boolean memberOfPom = true;
    private Set<PluginGoal> pluginGoalList = new LinkedHashSet<PluginGoal>();
    private PluginDocument pluginDocument;
    private String pluginPath;

    /**
     * Constructs ...
     *
     * @param pluginDocument Document me!
     */
    public MavenPluginDocumentImpl(PluginDocument pluginDocument) {
        this.pluginDocument = pluginDocument;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public String toString() {
        return pluginDocument.getPlugin().getGoalPrefix();
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public PluginDocument getPluginDocument() {
        return pluginDocument;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public Set<PluginGoal> getPluginGoalList() {
        return pluginGoalList;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public String getPluginPath() {
        return pluginPath;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public boolean isMemberOfPom() {
        return memberOfPom;
    }

    /**
     * Method description
     *
     * @param memberOfPom Document me!
     */
    public void setMemberOfPom(boolean memberOfPom) {
        this.memberOfPom = memberOfPom;
    }

    /**
     * Method description
     *
     * @param pluginDocument Document me!
     */
    public void setPluginDocument(PluginDocument pluginDocument) {
        this.pluginDocument = pluginDocument;
    }

    /**
     * Method description
     *
     * @param pluginGoalList Document me!
     */
    public void setPluginGoalList(Set<PluginGoal> pluginGoalList) {
        this.pluginGoalList = pluginGoalList;
    }

    /**
     * Method description
     *
     * @param pluginPath Document me!
     */
    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }
}
