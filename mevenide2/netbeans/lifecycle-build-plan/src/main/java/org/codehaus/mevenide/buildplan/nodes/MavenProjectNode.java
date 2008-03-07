/*
 *  Copyright 2008 Anuradha.
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
package org.codehaus.mevenide.buildplan.nodes;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Map;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.lifecycle.NoSuchPhaseException;
import org.apache.maven.lifecycle.model.MojoBinding;
import org.apache.maven.lifecycle.plan.BuildPlan;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.buildplan.BuildPlanUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha
 */
public class MavenProjectNode extends AbstractNode {

    private MavenProject nmp;

    public MavenProjectNode(MavenEmbedder embedder, MavenProject nmp, String... tasks) {
        super(createChildern(embedder, nmp, tasks));
        this.nmp = nmp;
        setDisplayName(nmp.getName() + " (" + nmp.getPackaging() + ")");
        setShortDescription(nmp.getDescription());
    }

    @Override
    public Image getIcon(int arg0) {
        return Utilities.loadImage("org/codehaus/mevenide/buildplan/nodes/Maven2Icon.gif");
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }

    private static class PhaseChildern extends Children.Keys<String> {

        java.util.Map<String, List<MojoBinding>> map;

        public PhaseChildern(java.util.Map<String, List<MojoBinding>> map) {
            this.map = map;
        }

        @Override
        protected Node[] createNodes(String key) {
            return new Node[]{new PhaseNode(key, map.get(key))};
        }

        @Override
        protected void addNotify() {
            super.addNotify();
         setKeys(new ArrayList<String>(map.keySet()));
        }
    }

    public static Children createChildern(MavenEmbedder embedder, MavenProject nmp, String... tasks) {


        try {
            BuildPlan buildPlan = embedder.getBuildPlan(Arrays.asList(tasks),
                    nmp);


            Map<String, List<MojoBinding>> map = BuildPlanUtil.getMojoBindingsGroupByPhase(buildPlan);


          return new PhaseChildern(map);

        } catch (NoSuchPhaseException ex) {
            Exceptions.printStackTrace(ex);
        } catch (MavenEmbedderException ex) {
            ex.printStackTrace();
        }
        return new Children.Array();
    }
}
