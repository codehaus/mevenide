/*
 *  Copyright 2008 mkleint.
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

package org.netbeans.modules.maven.navigator;

import java.util.Collection;
import javax.swing.JComponent;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author mkleint
 */
public class POMInheritanceNavigator implements NavigatorPanel {
    private POMInheritancePanel component;
    
    protected Lookup.Result selection;

    protected final LookupListener selectionListener = new LookupListener() {
        public void resultChanged(LookupEvent ev) {
            if(selection == null)
                return;
            navigate(selection.allInstances());
        }
    };
    

    public String getDisplayName() {
        return org.openide.util.NbBundle.getMessage(POMInheritanceNavigator.class, "POM_NAME");
    }

    public String getDisplayHint() {
        return org.openide.util.NbBundle.getMessage(POMInheritanceNavigator.class, "POM_HINT");
    }

    public JComponent getComponent() {
        return getNavigatorUI();
    }
    
    private POMInheritancePanel getNavigatorUI() {
        if (component == null) {
            component = new POMInheritancePanel();
        }
        return component;
    }

    public void panelActivated(Lookup context) {
        getNavigatorUI().showWaitNode();
        selection = context.lookup(new Lookup.Template<DataObject>(DataObject.class));
        selection.addLookupListener(selectionListener);
        selectionListener.resultChanged(null);
    }
    
    public void panelDeactivated() {
        getNavigatorUI().showWaitNode();
        if(selection != null) {
            selection.removeLookupListener(selectionListener);
            selection = null;
        }
        getNavigatorUI().release();
    }

    public Lookup getLookup() {
        return Lookup.EMPTY;
    }
    
    /**
     * 
     * @param selectedFiles 
     */

    public void navigate(Collection<DataObject> selectedFiles) {
        if(selectedFiles.size() == 1) {
            DataObject d = (DataObject) selectedFiles.iterator().next();
            getNavigatorUI().navigate(d);           
        }
    }
    

}