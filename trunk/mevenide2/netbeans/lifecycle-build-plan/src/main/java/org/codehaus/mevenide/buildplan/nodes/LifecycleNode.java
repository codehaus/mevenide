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
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.lifecycle.LifecycleLoaderException;
import org.apache.maven.lifecycle.LifecycleSpecificationException;
import org.apache.maven.lifecycle.plan.BuildPlan;
import org.apache.maven.lifecycle.plan.BuildPlanner;
import org.apache.maven.lifecycle.plan.LifecyclePlannerException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.buildplan.BuildPlanGroup;
import org.codehaus.mevenide.buildplan.BuildPlanUtil;
import org.codehaus.mevenide.netbeans.embedder.exec.NBBuildPlanner;
import org.codehaus.mevenide.netbeans.embedder.exec.ProgressTransferListener;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class LifecycleNode extends AbstractNode {

    private MavenProject nmp;

    public LifecycleNode(MavenEmbedder embedder, MavenProject nmp, String... tasks) {
        super(createChildern(embedder, nmp, tasks));
        this.nmp = nmp;
        setDisplayName(NbBundle.getMessage(LifecycleNode.class, "LBL_Lifecycle"));
        setShortDescription(nmp.getDescription());
    }

    @Override
    public Image getIcon(int arg0) {
        return Utilities.loadImage("org/codehaus/mevenide/buildplan/nodes/lifecycle.png");
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[]{};
    }

    @Override
    public String getHtmlDisplayName() {
        return getDisplayName()+" : <b>"+nmp.getName()+" ("+nmp.getPackaging()+")";
    }

    

    private static class PhaseChildern extends Children.Keys<String> {

        BuildPlanGroup bpg;

        public PhaseChildern(BuildPlanGroup bpg) {
            this.bpg = bpg;
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


        AggregateProgressHandle handle = AggregateProgressFactory.createSystemHandle("Constructing Build Plan", new ProgressContributor[0], null, null);
        handle.setInitialDelay(2000);
        handle.start();
        ProgressTransferListener.setAggregateHandle(handle);
        try {
            synchronized (embedder) {
                NBBuildPlanner buildPlanner = (NBBuildPlanner) embedder.getPlexusContainer().lookup(BuildPlanner.class);
                if(buildPlanner.getMavenSession()==null ){
                 return Children.LEAF;
                }
                List<String> list = Arrays.asList(tasks);

                BuildPlan buildPlan = buildPlanner.constructBuildPlan(list, nmp, buildPlanner.getMavenSession());
                
                BuildPlanGroup bpg = BuildPlanUtil.getMojoBindingsGroupByPhase(buildPlan);


                return new PhaseChildern(bpg);
            }
        } catch (LifecycleLoaderException ex) {
            Exceptions.printStackTrace(ex);
        } catch (LifecyclePlannerException ex) {
            Exceptions.printStackTrace(ex);
        } catch (LifecycleSpecificationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ComponentLookupException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            ProgressTransferListener.clearAggregateHandle();
            handle.finish();
        }
        return new Children.Array();
    }
}