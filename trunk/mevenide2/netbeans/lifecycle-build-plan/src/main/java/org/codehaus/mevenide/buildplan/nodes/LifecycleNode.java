/*
 *  Copyright 2008 Mevenide Team.
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
import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.LifecycleLoaderException;
import org.apache.maven.lifecycle.LifecycleSpecificationException;
import org.apache.maven.lifecycle.plan.BuildPlan;
import org.apache.maven.lifecycle.plan.BuildPlanner;
import org.apache.maven.lifecycle.plan.LifecyclePlannerException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.buildplan.BuildPlanGroup;
import org.codehaus.mevenide.buildplan.BuildPlanUtil;
import org.codehaus.mevenide.buildplan.BuildPlanView;
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
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class LifecycleNode extends AbstractNode {

    private MavenProject nmp;

    public LifecycleNode(BuildPlanView view, MavenProject nmp) {
        super(new PhaseChildren(view, nmp), Lookups.fixed(view, nmp));
        this.nmp = nmp;
        setDisplayName(nmp.getName() + " (" + nmp.getPackaging() + ")");
        setShortDescription(NbBundle.getMessage(LifecycleNode.class, "LBL_Lifecycle", getDisplayName()));
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

    private static class PhaseChildren extends Children.Keys<BuildPlanGroup> {

        private final BuildPlanGroup loading = new BuildPlanGroup();
        MavenProject nmp;
        private BuildPlanView view;

        public PhaseChildren(BuildPlanView view, MavenProject nmp) {
            this.nmp = nmp;
            this.view = view;
        }

        @Override
        protected Node[] createNodes(BuildPlanGroup bpg) {
            if (loading.equals(bpg)) {
                return new Node[]{NodeUtils.createLoadingNode()};
            }
            List<String> phaseList = bpg.getPhaseList();
            Node[] ns = new Node[phaseList.size()];
            for (int i = 0; i < phaseList.size(); i++) {
                String phase = phaseList.get(i);
                ns[i] = new PhaseNode(view, nmp, phase, bpg.getMojoBindings(phase));
            }


            return ns;
        }

        @Override
        protected void addNotify() {
            //set dummy loading node
            setKeys(new BuildPlanGroup[]{loading});
            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    BuildPlanGroup bpg = view.retrieveBuildPlanGroup(nmp);
                    setKeys(new BuildPlanGroup[]{bpg});                    
                }
            });

        }
    }
}