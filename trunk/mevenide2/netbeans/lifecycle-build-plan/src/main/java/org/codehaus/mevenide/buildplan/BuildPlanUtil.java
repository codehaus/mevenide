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
package org.codehaus.mevenide.buildplan;

import java.util.List;
import java.util.Stack;
import org.apache.maven.lifecycle.LifecycleUtils;
import org.apache.maven.lifecycle.NoSuchPhaseException;
import org.apache.maven.lifecycle.model.MojoBinding;
import org.apache.maven.lifecycle.model.Phase;
import org.apache.maven.lifecycle.plan.BuildPlan;
import org.codehaus.mevenide.buildplan.nodes.MojoNode;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class BuildPlanUtil {

    public static final String PHASE_NONE_SPECIFIED = NbBundle.getMessage(MojoNode.class, "LBL_None_Specified");

    private BuildPlanUtil() {
    }

    public static BuildPlanGroup getMojoBindingsGroupByPhase(BuildPlan buildPlan) throws NoSuchPhaseException {
        BuildPlanGroup bpg = new BuildPlanGroup();
        List<MojoBinding> bindings = buildPlan.renderExecutionPlan(new Stack());


        for (MojoBinding mb : bindings) {
            Phase phase = LifecycleUtils.findPhaseForMojoBinding(mb, buildPlan.getLifecycleBindings(), true);
            String phaseKey = PHASE_NONE_SPECIFIED;
            if (phase != null) {
                phaseKey = phase.getName();
            }
            bpg.putMojoBinding(phaseKey, mb);
        }





        buildPlan.resetExecutionProgress();
        return bpg;

    }
}
