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
import java.io.IOException;
import java.util.Arrays;

import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.lifecycle.NoSuchPhaseException;
import org.apache.maven.lifecycle.plan.BuildPlan;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.buildplan.BuildPlanGroup;
import org.codehaus.mevenide.buildplan.BuildPlanUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
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

        BuildPlanGroup bpg;

        public PhaseChildern(BuildPlanGroup bpg) {
            this.bpg =  bpg;
        }

        @Override
        protected Node[] createNodes(String key) {
            return new Node[]{new PhaseNode(key, bpg.getMojoBindings(key))};
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(bpg.getPhaseList());
        }
    }

    public static Children createChildern(MavenEmbedder embedder, MavenProject nmp, String... tasks) {


        try {

            BuildPlan buildPlan = embedder.getBuildPlan(Arrays.asList(tasks),
                    new MavenProject(embedder.readModel(nmp.getFile())));


            BuildPlanGroup bpg = BuildPlanUtil.getMojoBindingsGroupByPhase(buildPlan);


            return new PhaseChildern(bpg);

        } catch (XmlPullParserException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (NoSuchPhaseException ex) {
            Exceptions.printStackTrace(ex);
        } catch (MavenEmbedderException ex) {
            ex.printStackTrace();
        }
        return new Children.Array();
    }
}
