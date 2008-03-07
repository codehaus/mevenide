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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.lifecycle.model.MojoBinding;
import org.codehaus.mevenide.buildplan.nodes.MojoNode;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class BuildPlanGroup {

    private List<String> phaseList = new ArrayList<String>();
    Map<String, List<MojoBinding>> map = new HashMap<String, List<MojoBinding>>();

    public List<String> getPhaseList() {
        return new ArrayList<String>(phaseList);
    }

    public List<MojoBinding> getMojoBindings(String phase) {
        return map.get(phase);
    }

    public void putMojoBinding(String key, MojoBinding mb) {
        if (!phaseList.contains(key)) {
            phaseList.add(key);
        }
        List<MojoBinding> mbs = map.get(key);
        if (mbs == null) {
            mbs = new ArrayList<MojoBinding>();
            map.put(key, mbs);
        }
        mbs.add(mb);
    }

    public boolean removePhase(String o) {
        return phaseList.remove(o);
    }

    public boolean containsPhase(String o) {
        return phaseList.contains(o);
    }
}
