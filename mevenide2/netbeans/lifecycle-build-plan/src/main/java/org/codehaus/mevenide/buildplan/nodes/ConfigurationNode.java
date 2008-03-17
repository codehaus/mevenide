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
import javax.swing.Action;
import org.apache.maven.lifecycle.model.MojoBinding;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class ConfigurationNode extends AbstractNode {

    private MojoBinding mb;

    public ConfigurationNode(MojoBinding mb) {
        super(mb.getConfiguration() == null ? Children.LEAF : new ElementChildren((Xpp3Dom) mb.getConfiguration()));
        setDisplayName(NbBundle.getMessage(ConfigurationNode.class, "LBL_Configuration"));
        this.mb = mb;
    }

    @Override
    public Image getIcon(int arg0) {
        return Utilities.loadImage("org/codehaus/mevenide/buildplan/nodes/config.png");
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[0];
    }
}
