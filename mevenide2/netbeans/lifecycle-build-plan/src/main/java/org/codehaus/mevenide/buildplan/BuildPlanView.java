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

import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.buildplan.ui.BuildPlanTopComponent;
import org.codehaus.mevenide.buildplan.ui.BuildPlanViewUI;

/**
 *
 * @author Anuradha G
 */
public class BuildPlanView {

    private MavenProject project;
    private String[] tasks;
    private BuildPlanViewUI bpvui;
    public BuildPlanView(MavenProject project, String... tasks) {
        this.project = project;
        this.tasks = tasks;
        bpvui=new BuildPlanViewUI(this);
    }

    public void open() {
        BuildPlanTopComponent bptc = BuildPlanTopComponent.findInstance();
        
        bptc.addView( bpvui);
        bpvui.buildNodeView();
        bptc.open();
        bptc.requestActive();
        
        
    }

    public void refesh() {
        bpvui.buildNodeView();
    }

    public void close() {
        //todo
    }

    public MavenProject getProject() {
        return project;
    }

    public String[] getTasks() {
        return tasks;
    }


}
